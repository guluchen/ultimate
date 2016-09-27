package de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.algorithm;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.Boogie2SMT;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.model.IAbstractPostOperator;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.model.IAbstractState;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.model.IAbstractStateBinaryOperator;

/**
 * An {@link AbstractMultiState} is an abstract state that consists of many abstract states of the same type.
 *
 * @author Daniel Dietsch (dietsch@informatik.uni-freiburg.de)
 *
 * @param <STATE>
 * @param <ACTION>
 * @param <VARDECL>
 */
public class AbstractMultiState<STATE extends IAbstractState<STATE, ACTION, VARDECL>, ACTION, VARDECL>
		implements IAbstractState<AbstractMultiState<STATE, ACTION, VARDECL>, ACTION, VARDECL> {

	private static int sNextFreeId;
	private final Set<STATE> mStates;
	private final int mMaxSize;
	private final int mId;

	AbstractMultiState(final int maxSize) {
		this(maxSize, newSet(maxSize));
	}

	AbstractMultiState(final int maxSize, final STATE state) {
		this(maxSize, newSet(maxSize));
		mStates.add(state);
	}

	private AbstractMultiState(final int maxSize, final Set<STATE> states) {
		mMaxSize = maxSize;
		mStates = states;
		sNextFreeId++;
		mId = sNextFreeId;
	}

	@Override
	public AbstractMultiState<STATE, ACTION, VARDECL> addVariable(final VARDECL variable) {
		return applyToAll(a -> a.addVariable(variable));
	}

	@Override
	public AbstractMultiState<STATE, ACTION, VARDECL> removeVariable(final VARDECL variable) {
		return applyToAll(a -> a.removeVariable(variable));
	}

	@Override
	public AbstractMultiState<STATE, ACTION, VARDECL> addVariables(final Collection<VARDECL> variables) {
		return applyToAll(a -> a.addVariables(variables));
	}

	@Override
	public AbstractMultiState<STATE, ACTION, VARDECL> removeVariables(final Collection<VARDECL> variables) {
		return applyToAll(a -> a.removeVariables(variables));
	}

	@Override
	public boolean containsVariable(final VARDECL var) {
		return mStates.stream().anyMatch(a -> a.containsVariable(var));
	}

	@Override
	public Set<VARDECL> getVariables() {
		if (mStates.isEmpty()) {
			return Collections.emptySet();
		}
		return Collections.unmodifiableSet(mStates.iterator().next().getVariables());
	}

	@Override
	public AbstractMultiState<STATE, ACTION, VARDECL>
			patch(final AbstractMultiState<STATE, ACTION, VARDECL> dominator) {
		assert mStates.size() != dominator.mStates
				.size() : "Cannot apply symmetrical with differently sized multi-states";
		final Set<STATE> newSet = newSet(mStates.size());
		final Iterator<STATE> iter = mStates.iterator();
		final Iterator<STATE> otherIter = dominator.mStates.iterator();
		while (iter.hasNext() && otherIter.hasNext()) {
			newSet.add(iter.next().patch(otherIter.next()));
		}
		return new AbstractMultiState<>(mMaxSize, newSet);
	}

	@Override
	public boolean isEmpty() {
		return mStates.stream().anyMatch(a -> a.isEmpty());
	}

	@Override
	public boolean isBottom() {
		return mStates.isEmpty() || mStates.stream().allMatch(a -> a.isBottom());
	}

	@Override
	public boolean isEqualTo(final AbstractMultiState<STATE, ACTION, VARDECL> other) {
		if (other == null) {
			return false;
		}
		if (!other.getVariables().equals(getVariables())) {
			return false;
		}

		for (final STATE state : mStates) {
			if (!other.mStates.stream().anyMatch(state::isEqualTo)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public SubsetResult isSubsetOf(final AbstractMultiState<STATE, ACTION, VARDECL> other) {
		if (other == null) {
			return SubsetResult.NONE;
		}
		if (!other.getVariables().equals(getVariables())) {
			return SubsetResult.NONE;
		}
		if (other.mStates.isEmpty() && !mStates.isEmpty()) {
			return SubsetResult.NONE;
		}

		SubsetResult result = SubsetResult.EQUAL;
		for (final STATE state : mStates) {
			final Optional<SubsetResult> min =
					other.mStates.stream().map(a -> state.isSubsetOf(a)).min((a, b) -> a.compareTo(b));
			if (min.isPresent()) {
				result = result.update(min.get());
			}
			if (result == SubsetResult.NONE) {
				break;
			}
		}
		return result;
	}

	@Override
	public Term getTerm(final Script script, final Boogie2SMT bpl2smt) {
		return SmtUtils.or(script, mStates.stream().map(a -> a.getTerm(script, bpl2smt)).collect(Collectors.toSet()));
	}

	@Override
	public String toLogString() {
		final StringBuilder sb = new StringBuilder();
		sb.append('{');
		for (final STATE state : mStates) {
			final String logStr = state.toLogString();
			if (logStr == null || logStr.isEmpty()) {
				sb.append("__");
			} else {
				sb.append(logStr);
			}
			sb.append(", ");
		}
		if (!mStates.isEmpty()) {
			sb.delete(sb.length() - 2, sb.length());
		}
		sb.append('}');
		return sb.toString();
	}

	private AbstractMultiState<STATE, ACTION, VARDECL> applyToAll(final Function<STATE, STATE> func) {
		final Set<STATE> newSet = newSet(mStates.size());
		for (final STATE state : mStates) {
			newSet.add(func.apply(state));
		}
		if (mStates.equals(newSet)) {
			return this;
		}
		return new AbstractMultiState<>(mMaxSize, newSet);
	}

	private AbstractMultiState<STATE, ACTION, VARDECL>
			applyToAllCollection(final Function<STATE, Collection<STATE>> func) {
		final Set<STATE> newSet = newSet();
		for (final STATE state : mStates) {
			newSet.addAll(func.apply(state));
		}
		return new AbstractMultiState<>(mMaxSize, getMaximalElements(newSet));
	}

	private Set<STATE> newSet() {
		return newSet(mMaxSize);
	}

	private static <STATE> Set<STATE> newSet(final int maxSize) {
		return new LinkedHashSet<>(maxSize, 1.0F);
	}

	@Override
	public int hashCode() {
		return mId;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AbstractMultiState<?, ?, ?> other = (AbstractMultiState<?, ?, ?>) obj;
		if (mId != other.mId) {
			return false;
		}
		if (mStates == null) {
			if (other.mStates != null) {
				return false;
			}
		} else if (!mStates.equals(other.mStates)) {
			return false;
		}
		return true;
	}

	Set<STATE> getStates() {
		return Collections.unmodifiableSet(mStates);
	}

	public STATE getSingleState(final IAbstractStateBinaryOperator<STATE> mergeOp) {
		return mStates.stream().reduce((a, b) -> mergeOp.apply(a, b)).orElse(null);
	}

	public AbstractMultiState<STATE, ACTION, VARDECL> merge(final IAbstractStateBinaryOperator<STATE> mergeOp,
			final AbstractMultiState<STATE, ACTION, VARDECL> other) {
		assert other != null && other.getVariables().equals(getVariables()) : "Cannot merge incompatible states";
		final Set<STATE> set = newSet();
		set.addAll(mStates);
		set.addAll(other.mStates);
		return new AbstractMultiState<>(mMaxSize, reduce(mergeOp, set));
	}

	private Set<STATE> reduce(final IAbstractStateBinaryOperator<STATE> mergeOp, final Set<STATE> states) {
		final Set<STATE> maximalElements = getMaximalElements(states);
		if (maximalElements.size() <= mMaxSize) {
			return maximalElements;
		}
		return reduceByOrderedMerge(mergeOp, maximalElements);
	}

	private Set<STATE> reduceByOrderedMerge(final IAbstractStateBinaryOperator<STATE> mergeOp,
			final Set<STATE> states) {
		final Set<STATE> reducibleSet = new LinkedHashSet<>(states);
		int numberOfMerges = states.size() - mMaxSize;
		while (numberOfMerges > 0) {
			final Iterator<STATE> iter = reducibleSet.iterator();
			final STATE first = iter.next();
			iter.remove();
			final STATE second = iter.next();
			iter.remove();
			reducibleSet.add(mergeOp.apply(first, second));
			--numberOfMerges;
		}
		assert reducibleSet.size() <= mMaxSize;
		return reducibleSet;
	}

	private Set<STATE> getMaximalElements(final Set<STATE> states) {
		final Set<STATE> maximalElements = newSet(states.size());
		for (final STATE state : states) {
			final Iterator<STATE> iter = maximalElements.iterator();
			boolean maximal = true;
			while (iter.hasNext()) {
				final STATE candidate = iter.next();
				final SubsetResult stateIsCovered = state.isSubsetOf(candidate);
				final SubsetResult stateCovers = candidate.isSubsetOf(state);
				if (stateIsCovered != SubsetResult.NONE) {
					// state is covered by someone, it cannot be maximal
					maximal = false;
					break;
				}
				if (stateCovers != SubsetResult.NONE) {
					// state covers someone
					iter.remove();
				}
			}

			if (maximal) {
				maximalElements.add(state);
			}
		}
		assert maximalElements.stream().filter(a -> a.isBottom()).count() <= 1 : "There can be only one bottom element";
		return maximalElements;
	}

	/**
	 * This state is the local pre state.
	 *
	 * @param varProvider
	 * @param current
	 * @param hierachicalPreState
	 * @return
	 */
	public AbstractMultiState<STATE, ACTION, VARDECL> defineVariablesAfter(
			final IVariableProvider<STATE, ACTION, VARDECL> varProvider, final ACTION transition,
			final AbstractMultiState<STATE, ACTION, VARDECL> hierachicalPreState) {

		final Set<STATE> newSet = newSet(mStates.size() * hierachicalPreState.mStates.size());
		for (final STATE localState : mStates) {
			for (final STATE hierState : hierachicalPreState.mStates) {
				newSet.add(varProvider.defineVariablesAfter(transition, localState, hierState));
			}
		}
		if (newSet.equals(mStates)) {
			return this;
		}
		return new AbstractMultiState<>(mMaxSize, getMaximalElements(newSet));
	}

	public AbstractMultiState<STATE, ACTION, VARDECL> apply(final IAbstractPostOperator<STATE, ACTION, VARDECL> postOp,
			final ACTION transition) {
		return applyToAllCollection(a -> postOp.apply(a, transition));
	}

	public AbstractMultiState<STATE, ACTION, VARDECL> apply(final IAbstractPostOperator<STATE, ACTION, VARDECL> postOp,
			final AbstractMultiState<STATE, ACTION, VARDECL> multiStateBeforeLeaving, final ACTION transition) {
		final Set<STATE> newSet = newSet(mStates.size() * multiStateBeforeLeaving.mStates.size());
		for (final STATE stateAfterLeaving : mStates) {
			for (final STATE stateBeforeLeaving : multiStateBeforeLeaving.mStates) {
				newSet.addAll(postOp.apply(stateBeforeLeaving, stateAfterLeaving, transition));
			}
		}
		return new AbstractMultiState<>(mMaxSize, getMaximalElements(newSet));
	}

	public AbstractMultiState<STATE, ACTION, VARDECL> apply(final IAbstractStateBinaryOperator<STATE> op,
			final AbstractMultiState<STATE, ACTION, VARDECL> other) {
		final Set<STATE> newSet = newSet(mStates.size() * other.mStates.size());
		for (final STATE firstOper : mStates) {
			for (final STATE secondOper : other.mStates) {
				newSet.add(op.apply(firstOper, secondOper));
			}
		}
		return new AbstractMultiState<>(mMaxSize, getMaximalElements(newSet));
	}

	@Override
	public String toString() {
		return toLogString();
	}
}
