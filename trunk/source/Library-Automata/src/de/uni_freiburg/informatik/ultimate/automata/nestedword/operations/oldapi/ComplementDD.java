/*
 * Copyright (C) 2011-2015 Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
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
package de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.oldapi;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryException;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.AutomataOperationCanceledException;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomatonSimple;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.UnaryNwaOperation;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.IsDeterministic;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.IsEmpty;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IBuchiIntersectStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IDeterminizeStateFactory;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IStateFactory;

/**
 * Complements a nested word automaton.
 * 
 * @author Matthias Heizmann (heizmann@informatik.uni-freiburg.de)
 * @param <LETTER>
 *            letter type
 * @param <STATE>
 *            state type
 */
public final class ComplementDD<LETTER, STATE> extends UnaryNwaOperation<LETTER, STATE> {
	private final INestedWordAutomatonSimple<LETTER, STATE> mOperand;
	private INestedWordAutomatonSimple<LETTER, STATE> mDeterminizedOperand;
	private final INestedWordAutomaton<LETTER, STATE> mResult;
	
	/**
	 * Constructor.
	 * 
	 * @param services
	 *            Ultimate services
	 * @param stateFactory
	 *            state factory
	 * @param operand
	 *            operand
	 * @throws AutomataOperationCanceledException
	 *             if operation was canceled
	 */
	public ComplementDD(final AutomataLibraryServices services, final IDeterminizeStateFactory<STATE> stateFactory,
			final INestedWordAutomatonSimple<LETTER, STATE> operand) throws AutomataOperationCanceledException {
		super(services);
		mOperand = operand;
		
		mLogger.info(startMessage());
		final boolean isDeterministic = new IsDeterministic<>(mServices, mOperand).getResult();
		if (!isDeterministic) {
			mDeterminizedOperand = (new DeterminizeDD<>(mServices, stateFactory, mOperand)).getResult();
		} else {
			mDeterminizedOperand = mOperand;
			mLogger.debug("Operand is already deterministic");
		}
		mResult = new ReachableStatesCopy<>(mServices, mDeterminizedOperand, true, true, false, false).getResult();
		mLogger.info(exitMessage());
	}
	
	@Override
	public String operationName() {
		return "ComplementDD";
	}
	
	@Override
	public String exitMessage() {
		return "Finished " + operationName() + ". Result " + mResult.sizeInformation();
	}
	
	@Override
	protected INestedWordAutomatonSimple<LETTER, STATE> getOperand() {
		return mOperand;
	}
	
	@Override
	public INestedWordAutomaton<LETTER, STATE> getResult() {
		return mResult;
	}
	
	@Override
	public boolean checkResult(final IStateFactory<STATE> stateFactory) throws AutomataLibraryException {
		if (mLogger.isInfoEnabled()) {
			mLogger.info("Testing correctness of complement");
		}
		
		boolean correct;
		// TODO Christian 2017-02-16 Cast is temporary workaround until state factory becomes class parameter
		final INestedWordAutomatonSimple<LETTER, STATE> intersectionOperandResult = (new IntersectDD<>(mServices,
				(IBuchiIntersectStateFactory<STATE>) stateFactory, mOperand, mResult, false)).getResult();
		correct = (new IsEmpty<>(mServices, intersectionOperandResult)).getResult();
		
		if (mLogger.isInfoEnabled()) {
			mLogger.info("Finished testing correctness of complement");
		}
		return correct;
	}
}
