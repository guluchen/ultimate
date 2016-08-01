/*
 * Copyright (C) 2013-2015 Christian Schilling (schillic@informatik.uni-freiburg.de)
 * Copyright (C) 2014-2015 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * Copyright (C) 2009-2015 University of Freiburg
 * 
 * This file is part of the ULTIMATE Automata Library.
 * 
 * The ULTIMATE Automata Library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The ULTIMATE Automata Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE Automata Library. If not, see <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE Automata Library, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP), 
 * containing parts covered by the terms of the Eclipse Public License, the 
 * licensors of the ULTIMATE Automata Library grant you additional permission 
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.automata.nwalibrary.operations.minimization;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryException;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.AutomataOperationCanceledException;
import de.uni_freiburg.informatik.ultimate.automata.IOperation;
import de.uni_freiburg.informatik.ultimate.automata.LibraryIdentifiers;
import de.uni_freiburg.informatik.ultimate.automata.ResultChecker;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.INestedWordAutomatonSimple;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.StateFactory;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.operations.IsDeterministic;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.operations.IsIncluded;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;

/**
 * This is the superclass of all minimization classes. It provides a correctness check for all subclasses and an
 * optional DFA check for subclasses that only work for DFAs.
 * 
 * Since the classes of the <code>NwaLibrary.Operations</code> package must automatically execute their operations from
 * within the constructor call, the correctness check cannot be inherited automatically. Hence all implementing
 * subclasses must explicitly call the respective method themselves.
 * 
 * @author Christian Schilling
 */
public abstract class AMinimizeNwa<LETTER, STATE>
		implements IOperation<LETTER, STATE> {

	protected final AutomataLibraryServices mServices;
	/**
	 * The logger.
	 */
	protected final ILogger mLogger;

	/**
	 * The operation name.
	 */
	protected final String mName;

	/**
	 * The input automaton.
	 */
	protected final INestedWordAutomaton<LETTER, STATE> mOperand;

	/**
	 * StateFactory for the construction of states of the resulting automaton.
	 */
	protected final StateFactory<STATE> mStateFactory;

	/**
	 * This constructor should be called by all subclasses and only by them.
	 * 
	 * @param name
	 *            operation name
	 * @param operand
	 *            input automaton
	 */
	protected AMinimizeNwa(final AutomataLibraryServices services,
			final StateFactory<STATE> stateFactory, final String name,
			final INestedWordAutomaton<LETTER, STATE> operand) {
		mServices = services;
		mLogger = mServices.getLoggingService().getLogger(LibraryIdentifiers.PLUGIN_ID);
		mName = name;
		mOperand = operand;
		mStateFactory = stateFactory;
		mLogger.info(startMessage());
	}

	@Override
	public final String operationName() {
		return mName;
	}

	@Override
	public final String startMessage() {
		return "Start " + operationName() + ". Operand has " +
				mOperand.sizeInformation();
	}

	@Override
	public final String exitMessage() {
		return "Finished " + operationName() + ". Reduced states from " +
				mOperand.size() + " to " + getResult().size() + ".";
	}

	@Override
	public abstract INestedWordAutomatonSimple<LETTER, STATE> getResult();

	@Override
	public final boolean checkResult(final StateFactory<STATE> stateFactory)
			throws AutomataLibraryException {
		mLogger.info("Start testing correctness of " + operationName());
		final String message;

		if (checkInclusion(mOperand, getResult(), stateFactory)) {
			if (checkInclusion(getResult(), mOperand, stateFactory)) {
				mLogger.info("Finished testing correctness of " +
						operationName());
				return true;
			} else {
				message = "The result recognizes less words than before.";
			}
		} else {
			message = "The result recognizes more words than before.";
		}

		ResultChecker.writeToFileIfPreferred(mServices,
				operationName() + " failed",
				message,
				mOperand);
		return false;
	}

	/**
	 * This method checks language inclusion of the first automaton wrt. the second automaton.
	 * 
	 * @param subset
	 *            automaton describing the subset language
	 * @param superset
	 *            automaton describing the superset language
	 * @param stateFactory
	 *            state factory
	 * @return true iff language is included
	 * @throws AutomataLibraryException
	 *             thrown by inclusion check
	 */
	private final boolean checkInclusion(
			final INestedWordAutomatonSimple<LETTER, STATE> subset,
			final INestedWordAutomatonSimple<LETTER, STATE> superset,
			final StateFactory<STATE> stateFactory)
			throws AutomataLibraryException {
		final boolean result = new IsIncluded<>(mServices, stateFactory, subset, superset).getResult();
		return result;
	}

	/**
	 * This method checks whether the input automaton is a DFA.
	 * 
	 * That means the automaton must be deterministic and must not contain any call and return transitions.
	 * 
	 * @return true iff input automaton is a DFA
	 * @throws AutomataLibraryException
	 *             thrown by determinism check
	 */
	protected final boolean checkForDfa() throws AutomataLibraryException {
		return (checkForDeterminism() && checkForFiniteAutomaton());
	}

	/**
	 * This method checks whether the input automaton is deterministic.
	 * 
	 * @return true iff automaton is deterministic
	 * @throws AutomataLibraryException
	 *             thrown by determinism check
	 */
	protected final boolean checkForDeterminism()
			throws AutomataLibraryException {
		return new IsDeterministic<LETTER, STATE>(mServices, mOperand).checkResult(mOperand.getStateFactory());
	}

	/**
	 * This method checks whether the automaton is a finite automaton. That means it must not contain any call and
	 * return letter.
	 * 
	 * NOTE: Return transitions would not do any harm when no call transitions exist, but they are considered bad
	 * nevertheless. NOTE: The method checks something stronger, namely that the respective alphabets are empty.
	 * 
	 * @return true iff automaton contains no call and return letters
	 */
	protected final boolean checkForFiniteAutomaton() {
		return ((mOperand.getCallAlphabet().size() == 0) &&
				(mOperand.getReturnAlphabet().size() == 0));
	}

	/**
	 * This method throws an exception iff the operation should be terminated.
	 * 
	 * @throws AutomataOperationCanceledException
	 *             thrown to enforce termination.
	 */
	protected final void checkForContinuation()
			throws AutomataLibraryException {
		if (!mServices.getProgressMonitorService().continueProcessing()) {
			throw new AutomataOperationCanceledException(this.getClass());
		}
	}

	/**
	 * This method computes the capacity size for hash sets and hash maps given the expected number of elements to avoid
	 * resizing.
	 * 
	 * @param size
	 *            expected number of elements before resizing
	 * @return the parameter for initializing the hash structure
	 */
	protected final int computeHashCap(final int size) {
		return (int) (size * 1.34 + 1);
	}
}
