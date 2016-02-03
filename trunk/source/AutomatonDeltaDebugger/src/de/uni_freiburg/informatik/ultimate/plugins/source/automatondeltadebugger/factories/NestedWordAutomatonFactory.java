/*
 * Copyright (C) 2015 Christian Schilling <schillic@informatik.uni-freiburg.de>
 * Copyright (C) 2009-2015 University of Freiburg
 * 
 * This file is part of the ULTIMATE Automaton Delta Debugger.
 * 
 * The ULTIMATE Automaton Delta Debugger is free software: you can redistribute
 * it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * The ULTIMATE Automaton Delta Debugger is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE Automaton Delta Debugger. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU GPL version 3 section 7: If you modify the
 * ULTIMATE Automaton Delta Debugger, or any covered work, by linking or
 * combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE Automaton Delta Debugger grant you additional
 * permission to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.plugins.source.automatondeltadebugger.factories;

import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.core.services.model.IUltimateServiceProvider;

/**
 * Factory for {@link NestedWordAutomaton} objects.
 * 
 * @author Christian Schilling <schillic@informatik.uni-freiburg.de>
 */
public class NestedWordAutomatonFactory<LETTER, STATE>
		extends AAutomatonFactory<LETTER, STATE> {
	private final IUltimateServiceProvider mServices;

	public NestedWordAutomatonFactory(
			final INestedWordAutomaton<LETTER, STATE> automaton,
			final IUltimateServiceProvider services) {
		super(automaton);
		this.mServices = services;
	}

	/**
	 * @param automaton automaton interface
	 * @return cast automaton
	 */
	private NestedWordAutomaton<LETTER, STATE>
			getNWA(final INestedWordAutomaton<LETTER, STATE> automaton) {
		return ((NestedWordAutomaton<LETTER, STATE>) automaton);
	}

	@Override
	protected INestedWordAutomaton<LETTER, STATE> createWithAlphabets(
			Set<LETTER> internalAlphabet, Set<LETTER> callAlphabet,
			Set<LETTER> returnAlphabet) {
		return new NestedWordAutomaton<LETTER, STATE>(new AutomataLibraryServices(mServices),
				internalAlphabet, callAlphabet, returnAlphabet,
				mAutomaton.getStateFactory());
	}

	@Override
	public void addState(final INestedWordAutomaton<LETTER, STATE> automaton,
			final STATE state, final boolean isInitial, final boolean isFinal) {
		getNWA(automaton).addState(isInitial, isFinal, state);
	}

	@Override
	public void addInternalTransition(
			final INestedWordAutomaton<LETTER, STATE> automaton,
			final STATE pred, final LETTER letter, final STATE succ) {
		getNWA(automaton).addInternalTransition(pred, letter, succ);
	}

	@Override
	public void addCallTransition(
			final INestedWordAutomaton<LETTER, STATE> automaton,
			final STATE pred, final LETTER letter, final STATE succ) {
		getNWA(automaton).addCallTransition(pred, letter, succ);
	}

	@Override
	public void addReturnTransition(
			final INestedWordAutomaton<LETTER, STATE> automaton,
			final STATE pred, final STATE hier, final LETTER letter,
			final STATE succ) {
		getNWA(automaton).addReturnTransition(pred, hier, letter, succ);
	}
}