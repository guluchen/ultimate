/*
 * Copyright (C) 2015 Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 * Copyright (C) 2015 Marius Greitschus (greitsch@informatik.uni-freiburg.de)
 * Copyright (C) 2015 University of Freiburg
 * 
 * This file is part of the ULTIMATE AbstractInterpretationV2 plug-in.
 * 
 * The ULTIMATE AbstractInterpretationV2 plug-in is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The ULTIMATE AbstractInterpretationV2 plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE AbstractInterpretationV2 plug-in. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE AbstractInterpretationV2 plug-in, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP), 
 * containing parts covered by the terms of the Eclipse Public License, the 
 * licensors of the ULTIMATE AbstractInterpretationV2 plug-in grant you additional permission 
 * to convey the resulting work.
 */

package de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import de.uni_freiburg.informatik.ultimate.core.preferences.UltimatePreferenceStore;
import de.uni_freiburg.informatik.ultimate.core.services.model.IProgressAwareTimer;
import de.uni_freiburg.informatik.ultimate.core.services.model.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.Boogie2SMT;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.Activator;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.model.IAbstractDomain;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.model.IAbstractPostOperator;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.model.IAbstractState;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.model.IAbstractStateBinaryOperator;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.preferences.AbsIntPrefInitializer;
import de.uni_freiburg.informatik.ultimate.util.ToolchainCanceledException;
import de.uni_freiburg.informatik.ultimate.util.relation.Pair;

/**
 * 
 * @author Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 * @author Marius Greitschus (greitsch@informatik.uni-freiburg.de)
 *
 */
public class FixpointEngine<STATE extends IAbstractState<STATE, ACTION, VARDECL>, ACTION, VARDECL, LOCATION> {

	private final int mMaxUnwindings;
	private final int mMaxParallelStates;

	private final ITransitionProvider<ACTION, LOCATION> mTransitionProvider;
	private final IAbstractStateStorage<STATE, ACTION, VARDECL, LOCATION> mStateStorage;
	private final IAbstractDomain<STATE, ACTION, VARDECL> mDomain;
	private final IVariableProvider<STATE, ACTION, VARDECL, LOCATION> mVarProvider;
	private final ILoopDetector<ACTION> mLoopDetector;
	private final IDebugHelper<STATE, ACTION, VARDECL, LOCATION> mDebugHelper;
	private final IProgressAwareTimer mTimer;
	private final Logger mLogger;

	private AbstractInterpretationBenchmark<ACTION, LOCATION> mBenchmark;
	private AbstractInterpretationResult<STATE, ACTION, VARDECL, LOCATION> mResult;

	public FixpointEngine(final IUltimateServiceProvider services, final IProgressAwareTimer timer,
			final FixpointEngineParameters<STATE, ACTION, VARDECL, LOCATION> params) {
		assert timer != null;
		assert services != null;
		assert params != null;

		mTimer = timer;
		mLogger = services.getLoggingService().getLogger(Activator.PLUGIN_ID);
		mTransitionProvider = params.getTransitionProvider();
		mStateStorage = params.getStorage();
		mDomain = params.getAbstractDomain();
		mVarProvider = params.getVariableProvider();
		mLoopDetector = params.getLoopDetector();
		mDebugHelper = params.getDebugHelper();

		final UltimatePreferenceStore ups = new UltimatePreferenceStore(Activator.PLUGIN_ID);
		mMaxUnwindings = ups.getInt(AbsIntPrefInitializer.LABEL_ITERATIONS_UNTIL_WIDENING);
		mMaxParallelStates = ups.getInt(AbsIntPrefInitializer.LABEL_STATES_UNTIL_MERGE);
		// mMaxParallelStates = 1;
	}

	public AbstractInterpretationResult<STATE, ACTION, VARDECL, LOCATION> run(final ACTION start, final Script script,
			final Boogie2SMT bpl2smt,
			final AbstractInterpretationResult<STATE, ACTION, VARDECL, LOCATION> intermediateResult) {
		mLogger.info("Starting fixpoint engine with domain " + mDomain.getClass().getSimpleName());
		mResult = (intermediateResult == null ? new AbstractInterpretationResult<>() : intermediateResult);
		mBenchmark = mResult.getBenchmark();
		calculateFixpoint(start);
		mResult.saveTerms(mStateStorage, start, script, bpl2smt);
		return mResult;
	}

	public AbstractInterpretationResult<STATE, ACTION, VARDECL, LOCATION> run(final ACTION start, final Script script,
			final Boogie2SMT bpl2smt) {
		return run(start, script, bpl2smt, new AbstractInterpretationResult<>());
	}

	private void calculateFixpoint(final ACTION start) {
		final Deque<WorklistItem<STATE, ACTION, VARDECL, LOCATION>> worklist = new ArrayDeque<WorklistItem<STATE, ACTION, VARDECL, LOCATION>>();
		final IAbstractPostOperator<STATE, ACTION, VARDECL> postOp = mDomain.getPostOperator();
		final IAbstractStateBinaryOperator<STATE> wideningOp = mDomain.getWideningOperator();
		final IAbstractStateBinaryOperator<STATE> mergeOp = mDomain.getMergeOperator();
		final Set<ACTION> reachedErrors = new HashSet<>();

		worklist.add(createInitialWorklistItem(start));

		while (!worklist.isEmpty()) {
			checkTimeout();

			final WorklistItem<STATE, ACTION, VARDECL, LOCATION> currentItem = worklist.removeFirst();
			mBenchmark.addIteration(currentItem);

			if (mLogger.isDebugEnabled()) {
				mLogger.debug(getLogMessageCurrentTransition(currentItem));
			}

			final List<STATE> postStates = calculateAbstractPost(currentItem, postOp, mergeOp);
			if (postStates == null) {
				continue;
			}

			for (final STATE pendingNewPostState : postStates) {
				final STATE postState = preprocessPostState(currentItem, wideningOp, pendingNewPostState);
				if (null == postState) {
					continue;
				}

				savePostState(currentItem, postState);

				checkReachedError(currentItem, postState, reachedErrors);

				addSuccessors(worklist, currentItem, wideningOp);
			}
		}
	}

	private List<STATE> calculateAbstractPost(final WorklistItem<STATE, ACTION, VARDECL, LOCATION> currentItem,
			final IAbstractPostOperator<STATE, ACTION, VARDECL> postOp,
			final IAbstractStateBinaryOperator<STATE> mergeOp) {

		final STATE preState = currentItem.getPreState();
		final STATE hierachicalPreState = currentItem.getHierachicalPreState();
		final ACTION currentAction = currentItem.getAction();

		// calculate the (abstract) effect of the current action by first
		// declaring variables in the prestate, and then calculating their
		// values
		final STATE preStateWithFreshVariables = mVarProvider.defineVariablesAfter(currentAction, preState,
				hierachicalPreState);

		List<STATE> postStates;
		if (preState == preStateWithFreshVariables) {
			postStates = postOp.apply(preStateWithFreshVariables, currentAction);
		} else {
			// a context switch happened
			// TODO: save the preState for evaluation of old variables
			postStates = postOp.apply(preState, preStateWithFreshVariables, currentAction);
		}

		assert mDebugHelper.isPostSound(preState, preStateWithFreshVariables, postStates,
				currentAction) : getLogMessageUnsoundPost(preState, preStateWithFreshVariables, postStates,
						currentAction);

		if (postStates.isEmpty()) {
			// if there are no post states, we interpret this as bottom
			if (mLogger.isDebugEnabled()) {
				mLogger.debug(getLogMessageEmptyIsBottom());
			}
			return null;
		}

		prepareScope(currentItem);

		if (postStates.size() > mMaxParallelStates) {
			mLogger.warn(getLogMessageWarnTooManyPostStates(postStates));
			mBenchmark.addMerge(postStates.size());
			postStates = Collections.singletonList(postStates.stream().reduce((a, b) -> mergeOp.apply(a, b)).get());
		}
		return postStates;
	}

	private STATE preprocessPostState(final WorklistItem<STATE, ACTION, VARDECL, LOCATION> currentItem,
			final IAbstractStateBinaryOperator<STATE> wideningOp, final STATE pendingPostState) {
		if (pendingPostState.isBottom()) {
			// if the new abstract state is bottom, we do not enter loops and we do not add
			// new actions to the worklist
			if (mLogger.isDebugEnabled()) {
				mLogger.debug(getLogMessagePostIsBottom(pendingPostState));
			}
			return null;
		}

		final IAbstractStateStorage<STATE, ACTION, VARDECL, LOCATION> currentStateStorage = currentItem
				.getCurrentStorage();
		final ACTION currentAction = currentItem.getAction();

		// check if the pending post state is a fixpoint
		if (checkFixpoint(currentStateStorage, currentAction, pendingPostState)) {
			// we can skip all successors safely, except if one of our successors is a summary successor
			final ACTION currentScope = currentItem.getCurrentScope();
			if (!mTransitionProvider.getSuccessors(currentAction, currentScope).stream()
					.anyMatch(a -> mTransitionProvider.isSummaryForCall(a, currentScope))) {
				if (mLogger.isDebugEnabled()) {
					mLogger.debug(getLogMessagePostIsFixpoint(pendingPostState));
				}
				return null;
			}
		}

		// check if we are entering a loop
		if (mLoopDetector.isEnteringLoop(currentAction)) {
			loopEnter(currentItem);
		}

		// check if we are leaving a loop
		if (currentItem.isActiveLoopHead(mTransitionProvider.getTarget(currentAction))) {
			loopLeave(currentItem);
		}

		return pendingPostState;
	}

	private void savePostState(final WorklistItem<STATE, ACTION, VARDECL, LOCATION> currentItem, STATE postState) {
		if (mLogger.isDebugEnabled()) {
			mLogger.debug(getLogMessageNewPostState(postState));
		}
		// add post state to this location
		currentItem.getCurrentStorage().addAbstractPostState(currentItem.getAction(), postState);
	}

	private void checkReachedError(final WorklistItem<STATE, ACTION, VARDECL, LOCATION> currentItem,
			final STATE postState, final Set<ACTION> reachedErrors) {
		final ACTION currentAction = currentItem.getAction();
		if (mTransitionProvider.isPostErrorLocation(currentAction, currentItem.getCurrentScope())
				&& !postState.isBottom() && reachedErrors.add(currentAction)) {
			if (mLogger.isDebugEnabled()) {
				mLogger.debug(new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(" Error state reached"));
			}
			mResult.reachedError(mTransitionProvider, currentItem, postState);
		}
	}

	private void loopLeave(final WorklistItem<STATE, ACTION, VARDECL, LOCATION> currentItem) {
		final int loopCounterValue = currentItem.leaveCurrentLoop();
		if (mLogger.isDebugEnabled()) {
			final ACTION current = currentItem.getAction();
			final LOCATION loopHead = mTransitionProvider.getTarget(current);
			mLogger.debug(getLogMessageLeaveLoop(loopCounterValue, loopHead));
		}
	}

	private void loopEnter(final WorklistItem<STATE, ACTION, VARDECL, LOCATION> currentItem) {
		final LOCATION currentLoopHead = mTransitionProvider.getSource(currentItem.getAction());
		final int loopCounterValue = currentItem.enterLoop(currentLoopHead, currentItem.getPreState());
		if (mLogger.isDebugEnabled()) {
			mLogger.debug(getLogMessageEnterLoop(loopCounterValue, currentLoopHead));
		}
	}

	private WorklistItem<STATE, ACTION, VARDECL, LOCATION> createInitialWorklistItem(final ACTION elem) {
		final STATE preState = mVarProvider.defineVariablesBefore(elem, mDomain.createFreshState());
		mStateStorage.addAbstractPreState(elem, preState);
		return new WorklistItem<STATE, ACTION, VARDECL, LOCATION>(preState, elem, mStateStorage);
	}

	private void addSuccessors(final Deque<WorklistItem<STATE, ACTION, VARDECL, LOCATION>> worklist,
			final WorklistItem<STATE, ACTION, VARDECL, LOCATION> currentItem,
			final IAbstractStateBinaryOperator<STATE> wideningOp) {
		final ACTION current = currentItem.getAction();
		final Collection<ACTION> successors = mTransitionProvider.getSuccessors(current, currentItem.getCurrentScope());

		if (successors.isEmpty()) {
			if (mLogger.isDebugEnabled()) {
				mLogger.debug(new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(" No successors"));
			}
			return;
		}

		final IAbstractStateStorage<STATE, ACTION, VARDECL, LOCATION> currentStateStorage = currentItem
				.getCurrentStorage();
		Collection<STATE> availablePostStates = currentStateStorage.getAbstractPostStates(current);

		// check if we have to merge before adding successors
		if (availablePostStates.size() > mMaxParallelStates) {
			final STATE mergedState = merge(worklist, current, successors, currentStateStorage, availablePostStates);
			availablePostStates = Collections.singleton(mergedState);
		}

		// prepare successor filters
		Predicate<Pair<STATE, ACTION>> summaryFilter = p -> !mTransitionProvider
				.isSummaryWithImplementation(p.getSecond());
		Predicate<Pair<STATE, ACTION>> filter = p -> !worklist.stream()
				.anyMatch(w -> w.getAction() == p.getSecond() && w.getPreState() == p.getFirst());
		// check if we should widen at this location before adding new successors
		// we should widen if the current item is a transition to a loop head
		// or it is a transition that enters a scope
		final LOCATION target = mTransitionProvider.getTarget(current);
		final Pair<Integer, STATE> loopPair = currentItem.getLoopPair(target);
		if (loopPair != null && loopPair.getFirst() > mMaxUnwindings) {
			// we should widen all current states with the last state at this loop head
			final STATE oldLoopState = loopPair.getSecond();

			removeWorklistItems(worklist, availablePostStates, successors);
			availablePostStates = widen(currentStateStorage, wideningOp, "loop", current, oldLoopState,
					availablePostStates);
			final Set<STATE> fixpoints = availablePostStates.stream().filter(a -> checkFixpoint(oldLoopState, a))
					.collect(Collectors.toSet());
			if (!fixpoints.isEmpty()) {
				filter = filter
						.and(p -> !fixpoints.contains(p.getFirst()) || !mLoopDetector.isEnteringLoop(p.getSecond()));
			}
		} else if (mTransitionProvider.isEnteringScope(current)) {
			final STATE oldScopeState = getLastStateAtScopeEntry(currentItem);
			if (oldScopeState != null) {
				// we should widen all current states with the last state at this scope entry
				removeWorklistItems(worklist, availablePostStates, successors);
				availablePostStates = widen(currentStateStorage, wideningOp, "scope", current, oldScopeState,
						availablePostStates);
				final Set<STATE> fixpoints = availablePostStates.stream().filter(a -> checkFixpoint(oldScopeState, a))
						.collect(Collectors.toSet());
				if (!fixpoints.isEmpty()) {
					filter = filter.and(p -> !fixpoints.contains(p.getFirst()));
				}
			}
		}

		filter = summaryFilter.and(filter);

		// construct a list of pairs <state,action> for successors, and filter out the following:
		// do not add already existing items
		// do not continue with fixpoints into loops
		// then, add for each remaining pair a successor
		final List<Pair<STATE, ACTION>> actualSuccessors = availablePostStates.stream()
				.flatMap(st -> successors.stream().map(act -> new Pair<>(st, act))).filter(filter)
				.collect(Collectors.toList());
		actualSuccessors.stream().forEach(p -> addSuccessor(worklist, currentItem, p.getFirst(), p.getSecond()));
	}

	private void addSuccessor(final Deque<WorklistItem<STATE, ACTION, VARDECL, LOCATION>> worklist,
			final WorklistItem<STATE, ACTION, VARDECL, LOCATION> currentItem, final STATE postState,
			final ACTION successor) {
		// note: postState is the new preState
		final WorklistItem<STATE, ACTION, VARDECL, LOCATION> successorItem = new WorklistItem<STATE, ACTION, VARDECL, LOCATION>(
				postState, successor, currentItem);

		if (mLogger.isDebugEnabled()) {
			mLogger.debug(getLogMessageAddTransition(successorItem));
		}
		worklist.add(successorItem);
	}

	private List<STATE> widen(final IAbstractStateStorage<STATE, ACTION, VARDECL, LOCATION> currentStateStorage,
			final IAbstractStateBinaryOperator<STATE> wideningOp, final String loopOrScope, final ACTION current,
			final STATE oldPostState, final Collection<STATE> currentPostStates) {
		// TODO: Remove all worklist items that will be superseded by this widening operation, i.e. all abstract states
		// from the source of oldPostState
		// TODO: Remove all stored states that are superseded

		if (mLogger.isDebugEnabled()) {
			currentPostStates.forEach(a -> mLogger.debug(getLogMessageUnwinding(oldPostState, a)));
		}
		mBenchmark.addWiden();

		final List<STATE> newPostStates = currentStateStorage.widenPostState(current, wideningOp, oldPostState);
		if (mLogger.isDebugEnabled()) {
			newPostStates.forEach(a -> mLogger.debug(getLogMessageWideningResult(loopOrScope, a)));
		}
		return newPostStates;
	}

	private STATE merge(final Deque<WorklistItem<STATE, ACTION, VARDECL, LOCATION>> worklist, final ACTION current,
			final Collection<ACTION> successors,
			final IAbstractStateStorage<STATE, ACTION, VARDECL, LOCATION> currentStateStorage,
			final Collection<STATE> statesToMerge) {
		mBenchmark.addMerge(statesToMerge.size());
		if (mLogger.isDebugEnabled()) {
			mLogger.debug(getLogMessageMergeStates(statesToMerge.size(), statesToMerge));
		}
		removeWorklistItems(worklist, statesToMerge, successors);
		final STATE newPostState = currentStateStorage.mergePostStates(current);
		if (mLogger.isDebugEnabled()) {
			mLogger.debug(getLogMessageMergeResult(newPostState));
		}
		assert currentStateStorage.getAbstractPostStates(current).size() == 1;
		return newPostState;
	}

	/**
	 * Remove all items from the worklist that have a prestate in states2remove and an action in successors.
	 */
	private void removeWorklistItems(final Deque<WorklistItem<STATE, ACTION, VARDECL, LOCATION>> worklist,
			final Collection<STATE> states2remove, final Collection<ACTION> successors) {
		final Iterator<WorklistItem<STATE, ACTION, VARDECL, LOCATION>> iter = worklist.iterator();
		final Set<ACTION> successorSet = new HashSet<>(successors);
		final Set<STATE> stateSet = new HashSet<STATE>(states2remove);
		while (iter.hasNext()) {
			final WorklistItem<STATE, ACTION, VARDECL, LOCATION> currentItem = iter.next();
			// note that here the state has to be equal, i.e., the same instance
			if (successorSet.contains(currentItem.getAction()) && stateSet.contains(currentItem.getPreState())) {
				iter.remove();
				if (mLogger.isDebugEnabled()) {
					mLogger.debug(getLogMessageRemoveFromWorklist(currentItem));
				}
			}
		}
	}

	/**
	 * Check if we are entering or leaving a scope and if so, create or delete it.
	 */
	private void prepareScope(final WorklistItem<STATE, ACTION, VARDECL, LOCATION> currentItem) {
		final ACTION action = currentItem.getAction();
		if (mTransitionProvider.isEnteringScope(action)) {
			currentItem.addScope(action);
			if (mLogger.isDebugEnabled()) {
				mLogger.debug(getLogMessageEnterScope(currentItem));
			}
		} else if (mTransitionProvider.isLeavingScope(action, currentItem.getCurrentScope())) {
			currentItem.removeCurrentScope();
			if (mLogger.isDebugEnabled()) {
				mLogger.debug(getLogMessageLeaveScope(currentItem));
			}
		}
	}

	private STATE getLastStateAtScopeEntry(final WorklistItem<STATE, ACTION, VARDECL, LOCATION> currentItem) {
		final ACTION currentAction = currentItem.getAction();

		final Deque<Pair<ACTION, IAbstractStateStorage<STATE, ACTION, VARDECL, LOCATION>>> stackAtCallLocation = currentItem
				.getStack();

		// get all stack items in the correct order that contain only calls to the current scope
		final List<Pair<ACTION, IAbstractStateStorage<STATE, ACTION, VARDECL, LOCATION>>> relevantStackItems = stackAtCallLocation
				.stream().sequential().filter(a -> a.getFirst() == currentAction || a.getFirst() == null)
				.collect(Collectors.toList());
		if (relevantStackItems.isEmpty()) {
			// there is no relevant sequence
			return null;
		}

		// if the stack is too small, we do not need to widen
		if (relevantStackItems.size() <= mMaxUnwindings) {
			return null;
		}

		final List<STATE> orderedStates = relevantStackItems.stream().sequential()
				.map(a -> a.getSecond().getAbstractPostStates(currentAction)).flatMap(a -> a.stream().sequential())
				.collect(Collectors.toList());
		if (orderedStates.isEmpty()) {
			throw new AssertionError("Could not find last state in call stack");
		}
		// select the last state
		assert orderedStates.size() >= mMaxUnwindings;
		final STATE lastState = orderedStates.get(orderedStates.size() - 2);
		
		if (mLogger.isDebugEnabled()) {
			mLogger.debug("CurrentAction [" + currentAction.hashCode() + "] " + currentAction);
			mLogger.debug("Stack");
			stackAtCallLocation.stream().sequential().map(a -> a.getFirst())
					.map(a -> a == null ? "[G]" : "[" + a.hashCode() + "] " + a.toString()).forEach(mLogger::debug);
			mLogger.debug("Relevant stack");
			relevantStackItems.stream().sequential().forEach(a -> {
				mLogger.debug(
						a.getFirst() == null ? "[G]" : "[" + a.getFirst().hashCode() + "] " + a.getFirst().toString());
				mLogger.debug("  " + a.getSecond().toString());
			});
			mLogger.debug("Ordered states [" + currentAction.hashCode() + "] " + currentAction);
			orderedStates.stream().sequential().forEach(a -> {
				mLogger.debug("[" + a.hashCode() + "] " + a.toLogString());
			});
			mLogger.debug("Selected " + lastState.hashCode());
		}
		return lastState;
	}

	private boolean checkFixpoint(final STATE oldState, final STATE newState) {
		if (oldState.isEqualTo(newState)) {
			if (mLogger.isDebugEnabled()) {
				mLogger.debug(getLogMessageFixpointFound(oldState, newState));
			}
			mBenchmark.addFixpoint();
			return true;
		}
		return false;
	}

	private boolean checkFixpoint(final IAbstractStateStorage<STATE, ACTION, VARDECL, LOCATION> currentStorage,
			ACTION currentAction, STATE newPostState) {
		final Collection<STATE> oldPostStates = currentStorage.getAbstractPostStates(currentAction);
		return oldPostStates.stream().anyMatch(old -> checkFixpoint(old, newPostState));
	}

	private void checkTimeout() {
		if (!mTimer.continueProcessing()) {
			mLogger.warn("Received timeout, aborting fixpoint engine");
			throw new ToolchainCanceledException(getClass(), "Got cancel request during abstract interpretation");
		}
	}

	private StringBuilder getLogMessageWarnTooManyPostStates(List<STATE> postStates) {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(" Domain ")
				.append(mDomain.getClass().getSimpleName()).append(" produced too many abstract states during post: ")
				.append(mMaxParallelStates).append(" allowed, ").append(postStates.size()).append(" received.");
	}

	private String getLogMessageUnsoundPost(final STATE preState, final STATE preStateWithFreshVariables,
			final List<STATE> postStates, final ACTION currentAction) {
		final StringBuilder sb = new StringBuilder();
		sb.append("Post is unsound because the term-transformation of the following triple is not valid: {");
		sb.append(preState.toLogString());
		sb.append("} ");
		if (preState != preStateWithFreshVariables) {
			sb.append("{");
			sb.append(preStateWithFreshVariables.toLogString());
			sb.append("} ");
		}
		sb.append(mTransitionProvider.toLogString(currentAction));
		sb.append(" {");
		final Iterator<STATE> iter = postStates.iterator();
		while (iter.hasNext()) {
			sb.append(iter.next().toLogString());
			if (iter.hasNext()) {
				sb.append(" OR ");
			}
		}
		sb.append("}");
		return sb.toString();
	}

	private StringBuilder getLogMessageEmptyIsBottom() {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT)
				.append(" Skipping all successors because there was no post state (i.e., post is bottom)");
	}

	private StringBuilder getLogMessagePostIsBottom(final STATE pendingNewPostState) {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT)
				.append(" Skipping all successors because post state [").append(pendingNewPostState.hashCode())
				.append("] is bottom");
	}

	private StringBuilder getLogMessagePostIsFixpoint(final STATE pendingNewPostState) {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT)
				.append(" Skipping all successors because post state [").append(pendingNewPostState.hashCode())
				.append("] ").append(pendingNewPostState.toLogString()).append(" is fixpoint");
	}

	private StringBuilder getLogMessageLeaveScope(final WorklistItem<STATE, ACTION, VARDECL, LOCATION> successorItem) {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(AbsIntPrefInitializer.INDENT)
				.append(" Successor transition [").append(successorItem.getAction().hashCode())
				.append("] leaves scope (new depth=").append(successorItem.getCallStackDepth()).append(")");
	}

	private StringBuilder getLogMessageEnterScope(final WorklistItem<STATE, ACTION, VARDECL, LOCATION> successorItem) {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(AbsIntPrefInitializer.INDENT)
				.append(" Successor transition [").append(successorItem.getAction().hashCode())
				.append("] enters scope (new depth=").append(successorItem.getCallStackDepth()).append(")");
	}

	private StringBuilder getLogMessageFixpointFound(STATE oldPostState, final STATE newPostState) {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(" State [")
				.append(oldPostState.hashCode()).append("] ").append(oldPostState.toLogString())
				.append(" is equal to [").append(newPostState.hashCode()).append("]");
	}

	private StringBuilder getLogMessageMergeResult(STATE newPostState) {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(" Merging resulted in [")
				.append(newPostState.hashCode()).append("] ").append(newPostState.toLogString());
	}

	private StringBuilder getLogMessageRemoveFromWorklist(final WorklistItem<STATE, ACTION, VARDECL, LOCATION> item) {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(" Removing [")
				.append(item.getPreState().hashCode()).append("]").append(" --[").append(item.getAction().hashCode())
				.append("]-> from worklist");
	}

	private StringBuilder getLogMessageMergeStates(final int availablePostStatesCount,
			Collection<STATE> availablePostStates) {
		final List<String> postStates = availablePostStates.stream().map(a -> "[" + String.valueOf(a.hashCode()) + "]")
				.collect(Collectors.toList());
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(" Merging ")
				.append(availablePostStatesCount).append(" states at target location: ")
				.append(String.join(",", postStates));
	}

	private StringBuilder getLogMessageNewPostState(STATE newPostState) {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(" Adding post state [")
				.append(newPostState.hashCode()).append("] ").append(newPostState.toLogString());
	}

	private StringBuilder getLogMessageEnterLoop(final int loopCounterValue, final LOCATION loopHead) {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(" Entering loop ").append(loopHead)
				.append(" (").append(loopCounterValue).append(")");
	}

	private StringBuilder getLogMessageLeaveLoop(final int loopCounterValue, final LOCATION loopHead) {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(" Leaving loop ").append(loopHead)
				.append(" (").append(loopCounterValue).append(")");
	}

	private StringBuilder getLogMessageWideningResult(final String loopOrScope, final STATE newPostState) {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(" Widening at ").append(loopOrScope)
				.append(" resulted in post state [").append(newPostState.hashCode()).append("] ")
				.append(newPostState.toLogString());
	}

	private StringBuilder getLogMessageUnwinding(final STATE oldPostState, STATE newPostState) {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(" Widening with old post state [")
				.append(oldPostState.hashCode()).append("] ").append(oldPostState.toLogString())
				.append(" and new post state [").append(newPostState.hashCode()).append("] ")
				.append(newPostState.toLogString());
	}

	private StringBuilder getLogMessageCurrentTransition(
			final WorklistItem<STATE, ACTION, VARDECL, LOCATION> currentItem) {
		final STATE preState = currentItem.getPreState();
		final ACTION current = currentItem.getAction();
		final int depth = currentItem.getCallStackDepth();
		final String preStateString = preState == null ? "NULL"
				: addHashCodeString(new StringBuilder(), preState).append(" ").append(preState.toLogString())
						.toString();
		return addHashCodeString(new StringBuilder(), current).append(" ")
				.append(mTransitionProvider.toLogString(current)).append(" processing for pre state ")
				.append(preStateString).append(" (depth=").append(depth).append(")");
	}

	private StringBuilder getLogMessageAddTransition(
			final WorklistItem<STATE, ACTION, VARDECL, LOCATION> newTransition) {
		return new StringBuilder().append(AbsIntPrefInitializer.INDENT).append(" Adding [")
				.append(newTransition.getPreState().hashCode()).append("]").append(" --[")
				.append(newTransition.getAction().hashCode()).append("]->");
	}

	private StringBuilder addHashCodeString(final StringBuilder builder, final Object current) {
		if (current == null) {
			return builder.append("[?]");
		}
		return builder.append("[").append(current.hashCode()).append("]");
	}
}
