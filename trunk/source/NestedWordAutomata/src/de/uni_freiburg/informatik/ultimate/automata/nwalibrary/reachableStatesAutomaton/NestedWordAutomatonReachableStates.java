package de.uni_freiburg.informatik.ultimate.automata.nwalibrary.reachableStatesAutomaton;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

import org.apache.log4j.Logger;

import de.uni_freiburg.informatik.ultimate.automata.Activator;
import de.uni_freiburg.informatik.ultimate.automata.AtsDefinitionPrinter;
import de.uni_freiburg.informatik.ultimate.automata.OperationCanceledException;
import de.uni_freiburg.informatik.ultimate.automata.ResultChecker;
import de.uni_freiburg.informatik.ultimate.automata.Word;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.DoubleDecker;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.IDoubleDeckerAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.INestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.INestedWordAutomatonOldApi;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.INestedWordAutomatonSimple;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.IncomingCallTransition;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.IncomingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.IncomingReturnTransition;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.OutgoingCallTransition;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.OutgoingReturnTransition;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.StateFactory;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.SummaryReturnTransition;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.TransitionConsitenceCheck;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.operationsOldApi.DoubleDeckerVisitor.ReachFinal;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.operationsOldApi.IOpWithDelayedDeadEndRemoval.UpDownEntry;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.reachableStatesAutomaton.NestedWordAutomatonReachableStates.ReachProp;
import de.uni_freiburg.informatik.ultimate.core.api.UltimateServices;

public class NestedWordAutomatonReachableStates<LETTER,STATE> implements INestedWordAutomatonOldApi<LETTER,STATE>, INestedWordAutomaton<LETTER,STATE>, IDoubleDeckerAutomaton<LETTER, STATE> {

	private static Logger s_Logger = UltimateServices.getInstance().getLogger(Activator.PLUGIN_ID);
	
	private final INestedWordAutomatonSimple<LETTER,STATE> m_Operand;
	
	private final Set<LETTER> m_InternalAlphabet;
	private final Set<LETTER> m_CallAlphabet;
	private final Set<LETTER> m_ReturnAlphabet;
	
	protected final StateFactory<STATE> m_StateFactory;
	
	private final Set<STATE> m_initialStates = new HashSet<STATE>();
	private final Set<STATE> m_finalStates = new HashSet<STATE>();
	
	private final Map<STATE,StateContainer<LETTER,STATE>> m_States = new HashMap<STATE,StateContainer<LETTER,STATE>>();
	
	public static enum ReachProp { REACHABLE, NODEADEND_AD, NODEADEND_SD, LIVE };
	
	private final Set<STATE> m_initialStatesAfterDeadEndRemoval = new HashSet<STATE>();
	private final Set<STATE> m_StatesAfterDeadEndRemoval = new HashSet<STATE>();
	

	/**
	 * Set of return transitions LinPREs x HierPREs x LETTERs x SUCCs stored as 
	 * map HierPREs -> LETTERs -> LinPREs -> SUCCs
	 * 
	 */
	private Map<STATE,Map<LETTER,Map<STATE,Set<STATE>>>> m_ReturnSummary =
			new HashMap<STATE,Map<LETTER,Map<STATE,Set<STATE>>>>();

	
	


	private Map<StateContainer<LETTER,STATE>,Set<StateContainer<LETTER,STATE>>> m_Summaries = new HashMap<StateContainer<LETTER,STATE>,Set<StateContainer<LETTER,STATE>>>();

	private Set<LETTER> m_EmptySetOfLetters = new HashSet<LETTER>(0);
	

	
	private void addSummary(StateContainer<LETTER,STATE> callPred, StateContainer<LETTER,STATE> returnSucc) {
		Set<StateContainer<LETTER,STATE>> returnSuccs = m_Summaries.get(callPred);
		if (returnSuccs == null) {
			returnSuccs = new HashSet<StateContainer<LETTER,STATE>>();
			m_Summaries.put(callPred, returnSuccs);
		}
		returnSuccs.add(returnSucc);
	}
	
	public NestedWordAutomatonReachableStates(INestedWordAutomatonSimple<LETTER,STATE> operand) throws OperationCanceledException {
		this.m_Operand = operand;
		m_InternalAlphabet = operand.getInternalAlphabet();
		m_CallAlphabet = operand.getCallAlphabet();
		m_ReturnAlphabet = operand.getReturnAlphabet();
		m_StateFactory = operand.getStateFactory();
		try {
			new ReachableStatesComputation();
			new DeadEndComputation();
			s_Logger.info(stateContainerInformation());
			assert (new TransitionConsitenceCheck<LETTER, STATE>(this)).consistentForAll();

			assert (checkTransitionsReturnedConsistent());

			
		} catch (Error e) {
			String message = "// Problem with  removeUnreachable";
			ResultChecker.writeToFileIfPreferred("FailedremoveUnreachable",
					message, operand);
			throw e;
		} catch (RuntimeException e) {
			String message = "// Problem with  removeUnreachable";
			ResultChecker.writeToFileIfPreferred("FailedremoveUnreachable",
					message, operand);
			throw e;
		}
	}
	
	private String stateContainerInformation() {
		int inMap = 0;
		int outMap = 0;
		for(STATE state : m_States.keySet()) {
			StateContainer<LETTER, STATE> cont = m_States.get(state);
			if (cont instanceof StateContainerFieldAndMap) {
				if (((StateContainerFieldAndMap) cont).mapModeIncoming()) {
					inMap++;
				}
				if (((StateContainerFieldAndMap) cont).mapModeOutgoing()) {
					outMap++;
				}
			}
		}
		return m_States.size() + " StateContainers " + inMap + " in inMapMode" + outMap + " in outMapMode";
	}
	
	public boolean isDeadEnd(STATE state) {
		ReachProp reachProp = m_States.get(state).getReachProp();
		return  reachProp == ReachProp.REACHABLE;
	}
	
	public boolean isInitialAfterDeadEndRemoval(STATE state) {
		if (!m_initialStates.contains(state)) {
			throw new IllegalArgumentException("Not initial state");
		}
		StateContainer<LETTER, STATE> cont = m_States.get(state);
		if (cont.getReachProp() == ReachProp.NODEADEND_AD) {
			assert cont.getDownStates().get(getEmptyStackState()) == ReachProp.REACHABLE;
			return true;
		} else {
			if (cont.getDownStates().get(getEmptyStackState()) == ReachProp.NODEADEND_SD) {
				return true;
			} else {
				assert cont.getDownStates().get(getEmptyStackState()) == ReachProp.REACHABLE;
				return false;
			}
		}
	}
	
	
	@Override
	public boolean accepts(Word<LETTER> word) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return m_States.size();
	}

	@Override
	public Set<LETTER> getAlphabet() {
		return m_InternalAlphabet;
	}

	@Override
	public String sizeInformation() {
		int states = m_States.size();
		return states + " states.";
	}

	@Override
	public Set<LETTER> getInternalAlphabet() {
		return m_InternalAlphabet;
	}

	@Override
	public Set<LETTER> getCallAlphabet() {
		return m_CallAlphabet;
	}

	@Override
	public Set<LETTER> getReturnAlphabet() {
		return m_ReturnAlphabet;
	}

	@Override
	public StateFactory<STATE> getStateFactory() {
		return m_StateFactory;
	}

	@Override
	public Collection<STATE> getStates() {
		return m_States.keySet();
	}

	@Override
	public Collection<STATE> getInitialStates() {
		return Collections.unmodifiableSet(m_initialStates);
	}

	@Override
	public Collection<STATE> getFinalStates() {
		return Collections.unmodifiableSet(m_finalStates);
	}

	@Override
	public boolean isInitial(STATE state) {
		return m_Operand.isInitial(state);
	}

	@Override
	public boolean isFinal(STATE state) {
		return m_Operand.isFinal(state);
	}

	@Override
	public STATE getEmptyStackState() {
		return m_Operand.getEmptyStackState();
	}

	@Override
	public Collection<LETTER> lettersInternal(STATE state) {
		return m_States.get(state).lettersInternal();
	}

	@Override
	public Collection<LETTER> lettersCall(STATE state) {
		return m_States.get(state).lettersCall();
	}

	@Override
	public Collection<LETTER> lettersReturn(STATE state) {
		return m_States.get(state).lettersReturn();
	}

	@Override
	public Collection<LETTER> lettersInternalIncoming(STATE state) {
		return m_States.get(state).lettersInternalIncoming();
	}

	@Override
	public Collection<LETTER> lettersCallIncoming(STATE state) {
		return m_States.get(state).lettersCallIncoming();
	}

	@Override
	public Collection<LETTER> lettersReturnIncoming(STATE state) {
		return m_States.get(state).lettersReturnIncoming();
	}

	@Override
	public Collection<LETTER> lettersReturnSummary(STATE state) {
		if (!m_States.containsKey(state)) {
			throw new IllegalArgumentException("State " + state + " unknown");
		}
		 Map<LETTER, Map<STATE, Set<STATE>>> map = m_ReturnSummary.get(state);
		return map == null ? new ArrayList<LETTER>(0) : map.keySet();
	}

	@Override
	public Iterable<STATE> succInternal(STATE state, LETTER letter) {
		return m_States.get(state).succInternal(letter);
	}

	@Override
	public Iterable<STATE> succCall(STATE state, LETTER letter) {
		return m_States.get(state).succCall(letter);
	}

	@Override
	public Iterable<STATE> hierPred(STATE state, LETTER letter) {
		return m_States.get(state).hierPred(letter);
	}

	@Override
	public Iterable<STATE> succReturn(STATE state, STATE hier, LETTER letter) {
		return m_States.get(state).succReturn(hier, letter);
	}

	@Override
	public Iterable<STATE> predInternal(STATE state, LETTER letter) {
		return m_States.get(state).predInternal(letter);
	}

	@Override
	public Iterable<STATE> predCall(STATE state, LETTER letter) {
		return m_States.get(state).predCall(letter);
	}

	@Override
	public Iterable<STATE> predReturnLin(STATE state, LETTER letter, STATE hier) {
		return m_States.get(state).predReturnLin(letter, hier);
	}

	@Override
	public Iterable<STATE> predReturnHier(STATE state, LETTER letter) {
		return m_States.get(state).predReturnHier(letter);
	}

	@Override
	public boolean finalIsTrap() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isDeterministic() {
		return false;
	}

	@Override
	public boolean isTotal() {
		throw new UnsupportedOperationException();
	}
	
	private void addReturnSummary(STATE pred, STATE hier, LETTER letter, STATE succ) {
		Map<LETTER, Map<STATE, Set<STATE>>> letter2pred2succs = m_ReturnSummary.get(hier);
		if (letter2pred2succs == null) {
			letter2pred2succs = new HashMap<LETTER, Map<STATE, Set<STATE>>>();
			m_ReturnSummary.put(hier, letter2pred2succs);
		}
		Map<STATE, Set<STATE>> pred2succs = letter2pred2succs.get(letter);
		if (pred2succs == null) {
			pred2succs = new HashMap<STATE, Set<STATE>>();
			letter2pred2succs.put(letter, pred2succs);
		}
		Set<STATE> succS = pred2succs.get(pred);
		if (succS == null) {
			succS = new HashSet<STATE>();
			pred2succs.put(pred, succS);
		}
		succS.add(succ);
	}
	
	
	public Collection<LETTER> lettersSummary(STATE hier) {
		Map<LETTER, Map<STATE, Set<STATE>>> map = m_ReturnSummary.get(hier);
		return map == null ? m_EmptySetOfLetters  : map.keySet();
	}

	@Override
	public Iterable<SummaryReturnTransition<LETTER, STATE>> 
						returnSummarySuccessor(LETTER letter, STATE hier) {
		Set<SummaryReturnTransition<LETTER, STATE>> result = 
				new HashSet<SummaryReturnTransition<LETTER, STATE>>();
		Map<LETTER, Map<STATE, Set<STATE>>> letter2pred2succ = 
				m_ReturnSummary.get(hier);
		if (letter2pred2succ == null) {
			return result;
		}
		Map<STATE, Set<STATE>> pred2succ = letter2pred2succ.get(letter);
		if (pred2succ == null) {
			return result;
		}
		for (STATE pred : pred2succ.keySet()) {
			if (pred2succ.get(pred) != null) {
				for (STATE succ : pred2succ.get(pred)) {
				SummaryReturnTransition<LETTER, STATE> srt = 
					new SummaryReturnTransition<LETTER, STATE>(pred, letter, succ);
				result.add(srt);
				}
			}
		}
		return result;
	}
	
	


	public Iterable<SummaryReturnTransition<LETTER, STATE>> returnSummarySuccessor(final STATE hier) {
		return new Iterable<SummaryReturnTransition<LETTER, STATE>>() {
			/**
			 * Iterates over all SummaryReturnTransition of hier.
			 */
			@Override
			public Iterator<SummaryReturnTransition<LETTER, STATE>> iterator() {
				Iterator<SummaryReturnTransition<LETTER, STATE>> iterator = 
						new Iterator<SummaryReturnTransition<LETTER, STATE>>() {
					Iterator<LETTER> m_LetterIterator;
					LETTER m_CurrentLetter;
					Iterator<SummaryReturnTransition<LETTER, STATE>> m_CurrentIterator;
					{
						m_LetterIterator = lettersSummary(hier).iterator();
						nextLetter();
					}

					private void nextLetter() {
						if (m_LetterIterator.hasNext()) {
							do {
								m_CurrentLetter = m_LetterIterator.next();
								m_CurrentIterator = returnSummarySuccessor(
										m_CurrentLetter, hier).iterator();
							} while (!m_CurrentIterator.hasNext()
									&& m_LetterIterator.hasNext());
							if (!m_CurrentIterator.hasNext()) {
								m_CurrentLetter = null;
								m_CurrentIterator = null;
							}
						} else {
							m_CurrentLetter = null;
							m_CurrentIterator = null;
						}
					}

					@Override
					public boolean hasNext() {
						return m_CurrentLetter != null;
					}

					@Override
					public SummaryReturnTransition<LETTER, STATE> next() {
						if (m_CurrentLetter == null) {
							throw new NoSuchElementException();
						} else {
							SummaryReturnTransition<LETTER, STATE> result = 
									m_CurrentIterator.next();
							if (!m_CurrentIterator.hasNext()) {
								nextLetter();
							}
							return result;
						}
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
				return iterator;
			}
		};
	}
	

	@Override
	public Iterable<IncomingInternalTransition<LETTER, STATE>> internalPredecessors(
			LETTER letter, STATE succ) {
		return m_States.get(succ).internalPredecessors(letter);
	}

	@Override
	public Iterable<IncomingInternalTransition<LETTER, STATE>> internalPredecessors(
			STATE succ) {
		return m_States.get(succ).internalPredecessors();
	}

	@Override
	public Iterable<IncomingCallTransition<LETTER, STATE>> callPredecessors(
			LETTER letter, STATE succ) {
		return m_States.get(succ).callPredecessors(letter);
	}

	@Override
	public Iterable<IncomingCallTransition<LETTER, STATE>> callPredecessors(
			STATE succ) {
		return m_States.get(succ).callPredecessors();
	}

	@Override
	public Iterable<OutgoingInternalTransition<LETTER, STATE>> internalSuccessors(
			STATE state, LETTER letter) {
		return m_States.get(state).internalSuccessors(letter);
	}

	@Override
	public Iterable<OutgoingInternalTransition<LETTER, STATE>> internalSuccessors(
			STATE state) {
		return m_States.get(state).internalSuccessors();
	}

	@Override
	public Iterable<OutgoingCallTransition<LETTER, STATE>> callSuccessors(
			STATE state, LETTER letter) {
		return m_States.get(state).callSuccessors(letter);
	}

	@Override
	public Iterable<OutgoingCallTransition<LETTER, STATE>> callSuccessors(
			STATE state) {
		return m_States.get(state).callSuccessors();
	}

	@Override
	public Iterable<IncomingReturnTransition<LETTER, STATE>> returnPredecessors(
			STATE hier, LETTER letter, STATE succ) {
		return m_States.get(succ).returnPredecessors(hier, letter);
	}

	@Override
	public Iterable<IncomingReturnTransition<LETTER, STATE>> returnPredecessors(
			LETTER letter, STATE succ) {
		return m_States.get(succ).returnPredecessors(letter);
	}

	@Override
	public Iterable<IncomingReturnTransition<LETTER, STATE>> returnPredecessors(
			STATE succ) {
		return m_States.get(succ).returnPredecessors();
	}

	@Override
	public Iterable<OutgoingReturnTransition<LETTER, STATE>> returnSucccessors(
			STATE state, STATE hier, LETTER letter) {
		return m_States.get(state).returnSuccessors(hier, letter);
	}

	@Override
	public Iterable<OutgoingReturnTransition<LETTER, STATE>> returnSuccessors(
			STATE state, LETTER letter) {
		return m_States.get(state).returnSuccessors(letter);
	}

	@Override
	public Iterable<OutgoingReturnTransition<LETTER, STATE>> returnSuccessors(
			STATE state) {
		return m_States.get(state).returnSuccessors();
	}

	@Override
	public Iterable<OutgoingReturnTransition<LETTER, STATE>> returnSuccessorsGivenHier(
			STATE state, STATE hier) {
		return m_States.get(state).returnSuccessorsGivenHier(hier);
	}
	
	
	

	public Set<STATE> getDownStates(STATE state) {
		StateContainer<LETTER, STATE> cont = m_States.get(state);
		return cont.getDownStates().keySet();
	}
	
	public boolean isDoubleDecker(STATE up, STATE down) {
		return getDownStates(up).contains(down);
	}
	
	public boolean isDownStateAfterDeadEndRemoval(STATE up, STATE down) {
		StateContainer<LETTER, STATE> cont = m_States.get(up);
		assert (cont.getReachProp() == ReachProp.NODEADEND_AD || cont.getReachProp() == ReachProp.NODEADEND_SD);
		return cont.getDownStates().containsKey(down);
//		if (cont.getReachProp() == ReachProp.NODEADEND_AD) {
//			assert (cont.getDownStates().containsKey(down));
//			return true;
//		} else {
//			assert cont.getReachProp() == ReachProp.NODEADEND_SD;
//			ReachProp reach = cont.getDownStates().get(up);
//			if (reach == ReachProp.NODEADEND_SD) {
//				return true;
//			} else {
//				return false;
//			}
//		}
	}
	

	public Set<STATE> getDownStatesAfterDeadEndRemoval(STATE state) {
		Set<STATE> downStates;
		StateContainer<LETTER, STATE> cont = m_States.get(state);
//		if (cont.getReachProp() == ReachProp.NODEADEND_AD) {
			downStates = cont.getDownStates().keySet();
//		} else {
//			assert cont.getReachProp() == ReachProp.NODEADEND_SD;
//			downStates = new HashSet<STATE>();
//			for (Entry<STATE, ReachProp> down : cont.getDownStates().entrySet()) {
//				if (down.getValue() == ReachProp.NODEADEND_SD) {
//					downStates.add(down.getKey());
//				} else {
//					assert down.getValue() == ReachProp.REACHABLE;
//				}
//			}
//		}
//		for(Entry<LETTER,STATE> entry : m_States.get(up).getCommonEntriesComponent().getEntries()) {
//			STATE entryState = entry.getState();
//			for (IncomingCallTransition<LETTER, STATE> trans : callPredecessors(entryState)) {
//				STATE callPred = trans.getPred();
//				StateContainer<LETTER, STATE> callPredCont = m_States.get(callPred);
//				if (callPredCont.getReachProp() != ReachProp.REACHABLE) {
//					downStates.add(callPred);
//				}
//			}
//			if (m_initialStatesAfterDeadEndRemoval.contains(entryState)) {
//				downStates.add(getEmptyStackState());
//			}
//		}
		return downStates;
	}
	
	
	
	////////////////////////////////////////////////////////////////////////////
	
	private class ReachableStatesComputation {
		private final LinkedList<StateContainer<LETTER,STATE>> m_ForwardWorklist = 
				new LinkedList<StateContainer<LETTER,STATE>>();
		private final LinkedList<StateContainer<LETTER,STATE>> m_DownPropagationWorklist =
				new LinkedList<StateContainer<LETTER,STATE>>();
		
		
//		/**
//		 * Contains states that are in the worklist or processed at the moment.
//		 * Used to avoid insertion of elements to doubleDecker worklist whose
//		 * up state will be processed anyway.
//		 */
//		private final Set<STATE> m_WorklistAndCurrentState = 
//				new HashSet<STATE>();

		ReachableStatesComputation() throws OperationCanceledException {
			addInitialStates(m_Operand.getInitialStates());

			do {
				while (!m_ForwardWorklist.isEmpty()) {
					StateContainer<LETTER,STATE> cont = m_ForwardWorklist.remove(0);
					cont.eraseUnpropagatedDownStates();
					Set<STATE> newDownStatesFormSelfloops = null;
					
					if (candidateForOutgoingReturn(cont.getState())) {
						for (STATE down : cont.getDownStates().keySet()) {
							if (down != getEmptyStackState()) {
								Set<STATE> newDownStates = 
										addReturnsAndSuccessors(cont, down);
								if (newDownStates != null) {
									if (newDownStatesFormSelfloops == null) {
										newDownStatesFormSelfloops = new HashSet<STATE>();
									}
									newDownStatesFormSelfloops.addAll(newDownStates);
								}
							}
						}
					}

					addInternalsAndSuccessors(cont);
					{
						Set<STATE> newDownStates = addCallsAndSuccessors(cont);
						if (newDownStates != null) {
							if (newDownStatesFormSelfloops == null) {
								newDownStatesFormSelfloops = new HashSet<STATE>();
							}
							newDownStatesFormSelfloops.addAll(newDownStates);
						}
					}
					
					if (newDownStatesFormSelfloops != null) {
						assert !newDownStatesFormSelfloops.isEmpty();
						for (STATE down : newDownStatesFormSelfloops) {
							cont.addReachableDownState(down);
						}
						m_DownPropagationWorklist.add(cont);
					}

					if (!UltimateServices.getInstance().continueProcessing()) {
						throw new OperationCanceledException();
					}
				}
				
				while(m_ForwardWorklist.isEmpty() && !m_DownPropagationWorklist.isEmpty()) {
					StateContainer<LETTER,STATE> cont = 
							m_DownPropagationWorklist.remove(0);
					propagateNewDownStates(cont);
				}
				
				
			} while (!m_DownPropagationWorklist.isEmpty() || !m_ForwardWorklist.isEmpty());
			assert (m_ForwardWorklist.isEmpty());
			assert (m_DownPropagationWorklist.isEmpty());
			assert checkTransitionsReturnedConsistent();
		}
		
		
		private void addInitialStates(Iterable<STATE> initialStates) {
			for (STATE state : initialStates) {
				m_initialStates.add(state);
				HashMap<STATE, ReachProp> downStates = new HashMap<STATE,ReachProp>();
				downStates.put(getEmptyStackState(), ReachProp.REACHABLE);
				StateContainer<LETTER,STATE> sc = addState(state, downStates);
				m_States.put(state, sc);
			}
		}
		
		
		
		/**
		 * Construct State Container. Add to CommonEntriesComponent<LETTER,STATE>. Add to
		 * ForwardWorklist.
		 * @param state
		 * @param cec
		 * @return
		 */
		private StateContainer<LETTER, STATE> addState(STATE state, HashMap<STATE,ReachProp> downStates) {
			assert !m_States.containsKey(state);
			if (m_Operand.isFinal(state)) {
				m_finalStates.add(state);
			}
			boolean canHaveOutgoingReturn = candidateForOutgoingReturn(state);
			StateContainer<LETTER,STATE> result = 
					new StateContainerFieldAndMap<LETTER,STATE>(state, downStates, canHaveOutgoingReturn);
			m_States.put(state, result);
			m_ForwardWorklist.add(result);
			return result;
		}
		
		private boolean candidateForOutgoingReturn(STATE state) {
			return !m_Operand.lettersReturn(state).isEmpty();
//			return true;
		}
		

		private <E> Set<E> differenceSet(Set<E> minuend, Set<E> subtrahend) {
			Set<E> result = new HashSet<E>();
			for (E elem : minuend) {
				if (!subtrahend.contains(elem)) {
					result.add(elem);
				}
			}
			return result;
		}
		
		private void addInternalsAndSuccessors(StateContainer<LETTER,STATE> cont) {
			STATE state = cont.getState();
			for (OutgoingInternalTransition<LETTER, STATE> trans : 
											m_Operand.internalSuccessors(state)) {
				STATE succ = trans.getSucc();
				StateContainer<LETTER,STATE> succSC = m_States.get(succ);
				if (succSC == null) {
					succSC = addState(succ, new HashMap(cont.getDownStates()));
				} else {
					addNewDownStates(cont, succSC, cont.getDownStates().keySet());
				}
				assert (!containsCallTransition(state, trans.getLetter(), succ));
				cont.addInternalOutgoing(trans);
				succSC.addInternalIncoming(new IncomingInternalTransition<LETTER, STATE>(state, trans.getLetter()));
			}
		}
		
		



		private Set<STATE> addCallsAndSuccessors(StateContainer<LETTER,STATE> cont) {
			boolean addedSelfloop = false;
			STATE state = cont.getState();
			for (OutgoingCallTransition<LETTER, STATE> trans : 
										m_Operand.callSuccessors(cont.getState())) {
				STATE succ = trans.getSucc();
				StateContainer<LETTER,STATE> succCont = m_States.get(succ);
				HashMap<STATE, ReachProp> succDownStates = new HashMap<STATE,ReachProp>();
				succDownStates.put(cont.getState(), ReachProp.REACHABLE);
				if (succCont == null) {
					succCont = addState(succ, succDownStates);
				} else {
					addNewDownStates(cont, succCont, succDownStates.keySet());
					if (cont == succCont) {
						addedSelfloop = true;
					}
				}
				assert (!containsCallTransition(state, trans.getLetter(), succ));
				cont.addCallOutgoing(trans);
				succCont.addCallIncoming(
						new IncomingCallTransition<LETTER, STATE>(state, trans.getLetter()));
			}
			if (addedSelfloop) {
				HashSet<STATE> newDownStates = new HashSet<STATE>(1);
				newDownStates.add(state);
				return newDownStatesSelfloop(cont, newDownStates);
			} else {
				return null;
			}

		}
		
		

		private Set<STATE> addReturnsAndSuccessors(StateContainer<LETTER,STATE> cont, STATE down) {
			boolean addedSelfloop = false;
			STATE state = cont.getState();
			StateContainer<LETTER,STATE> downCont = null;
			for (OutgoingReturnTransition<LETTER, STATE> trans : 
									m_Operand.returnSuccessorsGivenHier(state,down)) {
				assert (down.equals(trans.getHierPred()));
				if (downCont == null) {
					downCont = m_States.get(down);
				}
				STATE succ = trans.getSucc();
				StateContainer<LETTER,STATE> succCont = m_States.get(succ);
				if (succCont == null) {
					succCont = addState(succ, new HashMap(downCont.getDownStates()));
				} else {
					addNewDownStates(cont, succCont, downCont.getDownStates().keySet());
					if (cont == succCont) {
						addedSelfloop = true;
					}
				}
				assert (!containsReturnTransition(state, down, trans.getLetter(), succ));
				cont.addReturnOutgoing(trans);
				succCont.addReturnIncoming(
						new IncomingReturnTransition<LETTER, STATE>(cont.getState(), down, trans.getLetter()));
				addReturnSummary(state, down, trans.getLetter(), succ);
				addSummary(downCont, succCont);
			}
			if (addedSelfloop) {
				return newDownStatesSelfloop(cont, downCont.getDownStates().keySet());
			} else {
				return null;
			}
		}


		/**
		 * @param cont
		 * @param newDownStatesSelfloop
		 * @param downCont
		 * @return
		 */
		private Set<STATE> newDownStatesSelfloop(StateContainer<LETTER, STATE> cont,
				Set<STATE> propagatedDownStates) {
			Set<STATE> newDownStates = null;
			for (STATE downs : propagatedDownStates) {
				if (!cont.getDownStates().keySet().contains(downs)) {
					if (newDownStates == null) {
						newDownStates = new HashSet<STATE>();
					}
					newDownStates.add(downs);
				}
				
			}
			return newDownStates;
		}

		
		private void addNewDownStates(StateContainer<LETTER, STATE> cont,
				StateContainer<LETTER, STATE> succCont,
				Set<STATE> potentiallyNewDownStates) {
			if (cont == succCont) {
				return;
			} else {
				boolean newDownStateWasPropagated = false;
				for (STATE down : potentiallyNewDownStates) {
					ReachProp oldValue = succCont.addReachableDownState(down);
					if (oldValue == null) {
						newDownStateWasPropagated = true;
					}
				}
				if (newDownStateWasPropagated) {
					m_DownPropagationWorklist.add(succCont);
				}
			}
		}


		private void propagateNewDownStates(StateContainer<LETTER, STATE> cont) {
			boolean newStatesAdded = false;
			Set<STATE> unpropagatedDownStates = cont.getUnpropagatedDownStates();
			if (unpropagatedDownStates  == null) {
				return;
			}
			for (OutgoingInternalTransition<LETTER, STATE> trans : cont.internalSuccessors()) {
				StateContainer<LETTER, STATE> succCont = m_States.get(trans.getSucc());
				addNewDownStates(cont, succCont, unpropagatedDownStates);
			}
			for (SummaryReturnTransition<LETTER, STATE> trans : returnSummarySuccessor(cont.getState())) {
				StateContainer<LETTER, STATE> succCont = m_States.get(trans.getSucc());
				addNewDownStates(cont, succCont, unpropagatedDownStates);
			}
			if(candidateForOutgoingReturn(cont.getState())) {
				HashSet<STATE> newDownStatesFormSelfloops = null;
				for (STATE down : cont.getUnpropagatedDownStates()) {
					if (down != getEmptyStackState()) {
						Set<STATE> newDownStates = 
								addReturnsAndSuccessors(cont, down);
						if (newDownStates != null) {
							if (newDownStatesFormSelfloops == null) {
								newDownStatesFormSelfloops = new HashSet<STATE>();
							}
							newDownStatesFormSelfloops.addAll(newDownStates);
						}
					}
				}
				cont.eraseUnpropagatedDownStates();
				if (newDownStatesFormSelfloops != null) {
					assert !newDownStatesFormSelfloops.isEmpty();
					for (STATE down : newDownStatesFormSelfloops) {
						cont.addReachableDownState(down);
					}
					m_DownPropagationWorklist.add(cont);
				}
			} else {
				cont.eraseUnpropagatedDownStates();
			}
			
		}
	}

		

	
	

////////////////////////////////////////////////////////////////////////////////
	private class DeadEndComputation {
		
		private ArrayDeque<StateContainer<LETTER,STATE>> m_NonReturnBackwardWorklist =
				new ArrayDeque<StateContainer<LETTER,STATE>>();
		private Set<StateContainer<LETTER,STATE>> m_HasIncomingReturn = 
				new HashSet<StateContainer<LETTER,STATE>>();
		private ArrayDeque<StateContainer<LETTER,STATE>> m_PropagationWorklist =
				new ArrayDeque<StateContainer<LETTER,STATE>>();

		DeadEndComputation() {
			for (STATE fin : getFinalStates()) {
				StateContainer<LETTER,STATE> cont = m_States.get(fin);
				assert cont.getReachProp() == ReachProp.REACHABLE;
				cont.setReachProp(ReachProp.NODEADEND_AD);
				m_StatesAfterDeadEndRemoval.add(fin);
				m_NonReturnBackwardWorklist.add(cont);
			}

			while (!m_NonReturnBackwardWorklist.isEmpty()) {
				StateContainer<LETTER,STATE> cont = m_NonReturnBackwardWorklist.removeFirst();
				if (m_initialStates.contains(cont.getState())) {
					m_initialStatesAfterDeadEndRemoval.add(cont.getState());
				}
				
				for (IncomingInternalTransition<LETTER, STATE> inTrans : cont
						.internalPredecessors()) {
					STATE pred = inTrans.getPred();
					StateContainer<LETTER,STATE> predCont = m_States.get(pred);
					if (predCont.getReachProp() != ReachProp.NODEADEND_AD) {
						predCont.setReachProp(ReachProp.NODEADEND_AD);
						m_StatesAfterDeadEndRemoval.add(pred);
						m_NonReturnBackwardWorklist.add(predCont);
					}
				}
				for (IncomingReturnTransition<LETTER, STATE> inTrans : cont
						.returnPredecessors()) {
					STATE hier = inTrans.getHierPred();
					StateContainer<LETTER,STATE> hierCont = m_States.get(hier);
					if (hierCont.getReachProp() != ReachProp.NODEADEND_AD) {
						hierCont.setReachProp(ReachProp.NODEADEND_AD);
						m_StatesAfterDeadEndRemoval.add(hier);
						m_NonReturnBackwardWorklist.add(hierCont);
					}
					m_HasIncomingReturn.add(cont);
				}
				for (IncomingCallTransition<LETTER, STATE> inTrans : cont
						.callPredecessors()) {
					STATE pred = inTrans.getPred();
					StateContainer<LETTER,STATE> predCont = m_States.get(pred);
					if (predCont.getReachProp() != ReachProp.NODEADEND_AD) {
						predCont.setReachProp(ReachProp.NODEADEND_AD);
						m_StatesAfterDeadEndRemoval.add(pred);
						m_NonReturnBackwardWorklist.add(predCont);
					}
				}
			}
			
			for (StateContainer<LETTER,STATE> cont : m_HasIncomingReturn) {
				for (IncomingReturnTransition<LETTER, STATE> inTrans : cont
						.returnPredecessors()) {
					STATE lin = inTrans.getLinPred();
					StateContainer<LETTER,STATE> linCont = m_States.get(lin);
					if (linCont.getReachProp() != ReachProp.NODEADEND_AD) {
						linCont.setReachProp(ReachProp.NODEADEND_SD);
						if (linCont.getUnpropagatedDownStates() == null) {
							assert !m_PropagationWorklist.contains(linCont);
							m_PropagationWorklist.addLast(linCont);
						}
						linCont.addNonDeadEndDownState(inTrans.getHierPred());
					}
				}
			}
			
			while (!m_PropagationWorklist.isEmpty()) {
				StateContainer<LETTER,STATE> cont = m_PropagationWorklist.removeFirst();
				propagateBackward(cont);
			}
			
		}

		private void propagateBackward(StateContainer<LETTER, STATE> cont) {
			Set<STATE> unpropagatedDownStates = cont.getUnpropagatedDownStates();
			cont.eraseUnpropagatedDownStates();
			Set<STATE> newUnpropagatedDownStatesSelfloop = null;
			for (IncomingInternalTransition<LETTER, STATE> inTrans : cont
					.internalPredecessors()) {
				STATE pred = inTrans.getPred();
				StateContainer<LETTER,STATE> predCont = m_States.get(pred);
				if (predCont.getReachProp() != ReachProp.NODEADEND_AD) {
					addNewDownStates(cont, predCont, unpropagatedDownStates);
				}
			}
			for (IncomingReturnTransition<LETTER, STATE> inTrans : cont
					.returnPredecessors()) {
				STATE hier = inTrans.getHierPred();
				StateContainer<LETTER,STATE> hierCont = m_States.get(hier);
				if (hierCont.getReachProp() != ReachProp.NODEADEND_AD) {
					addNewDownStates(cont, hierCont, unpropagatedDownStates);
				}
				STATE lin = inTrans.getLinPred();
				StateContainer<LETTER,STATE> linCont = m_States.get(lin);
				if (linCont.getReachProp() != ReachProp.NODEADEND_AD) {
					if (atLeastOneOccursAsDownState(hierCont, unpropagatedDownStates)) {
						if (linCont == cont) {
							boolean hierAlreadyPropagated = 
									(cont.getDownStates().get(hier) == ReachProp.NODEADEND_SD);
							if (!hierAlreadyPropagated) {
								if (newUnpropagatedDownStatesSelfloop == null) {
									newUnpropagatedDownStatesSelfloop = new HashSet<STATE>();
								}
								newUnpropagatedDownStatesSelfloop.add(hier);
							}
						} else {
							HashSet<STATE> potentiallyNewDownState = new HashSet<STATE>(1);
							potentiallyNewDownState.add(hier);
							addNewDownStates(cont, linCont, potentiallyNewDownState);
						}
					}

				}
			}
			if (newUnpropagatedDownStatesSelfloop != null) {
				for (STATE down : newUnpropagatedDownStatesSelfloop) {
					cont.addNonDeadEndDownState(down);
				}
				assert !m_PropagationWorklist.contains(cont);
				m_PropagationWorklist.add(cont);
			}
		}
		
	
		private boolean atLeastOneOccursAsDownState(
				StateContainer<LETTER, STATE> hierCont,
				Set<STATE> unpropagatedDownStates) {
			for (STATE state : unpropagatedDownStates) {
				if (hierCont.getDownStates().containsKey(state)) {
					return true;
				}
			}
			return false;
		}

		private void addNewDownStates(StateContainer<LETTER, STATE> cont,
				StateContainer<LETTER, STATE> predCont,
				Set<STATE> potentiallyNewDownStates) {
			if (cont == predCont) {
				return ;
			} else {
				boolean isAlreadyInWorklist = 
						(predCont.getUnpropagatedDownStates() != null);
				assert (isAlreadyInWorklist == m_PropagationWorklist.contains(predCont));
				assert (!isAlreadyInWorklist || predCont.getReachProp() == ReachProp.NODEADEND_SD);
				boolean newDownStateWasPropagated = false;
				for (STATE down : potentiallyNewDownStates) {
					ReachProp oldValue = predCont.getDownStates().get(down);
					if (oldValue == ReachProp.REACHABLE) {
						predCont.addNonDeadEndDownState(down);
						newDownStateWasPropagated = true;
					}
				}
				if (newDownStateWasPropagated) {
					if (!isAlreadyInWorklist) {
						assert !m_PropagationWorklist.contains(predCont);
						m_PropagationWorklist.add(predCont);
					}
					if (predCont.getReachProp() != ReachProp.NODEADEND_SD) {
						assert predCont.getReachProp() == ReachProp.REACHABLE;
						predCont.setReachProp(ReachProp.NODEADEND_SD);
						assert !m_StatesAfterDeadEndRemoval.contains(predCont.getState());
						m_StatesAfterDeadEndRemoval.add(predCont.getState());
					}
				}
			}
		}
	}


	
	
	public Iterable<UpDownEntry<STATE>> getRemovedUpDownEntry() {
		
		return new Iterable<UpDownEntry<STATE>>() {

			@Override
			public Iterator<UpDownEntry<STATE>> iterator() {
				return new Iterator<UpDownEntry<STATE>>() {
					private Iterator<STATE> m_UpIterator;
					private STATE m_Up;
					private Iterator<STATE> m_DownIterator;
					private STATE m_Down;
					boolean m_hasNext = true;
					private StateContainer<LETTER, STATE> m_StateContainer;

					{
						m_UpIterator = m_States.keySet().iterator();
						if (m_UpIterator.hasNext()) {
							m_Up = m_UpIterator.next();
							m_StateContainer = m_States.get(m_Up);
							m_DownIterator = m_StateContainer.getDownStates().keySet().iterator();
						} else {
							m_hasNext = false;
						}
						computeNextElement();
						
					}
					
					private void computeNextElement() {
						m_Down = null;
						while (m_Down == null && m_hasNext) {
							if (m_StateContainer.getReachProp() != ReachProp.NODEADEND_AD && m_DownIterator.hasNext()) {
								STATE downCandidate = m_DownIterator.next();
								if (m_StateContainer.getReachProp() == ReachProp.REACHABLE) {
									m_Down = downCandidate;
								} else {
									assert m_StateContainer.getReachProp() == ReachProp.NODEADEND_SD;
									ReachProp reach = m_StateContainer.getDownStates().get(downCandidate);
									if (reach == ReachProp.REACHABLE) {
										m_Down = downCandidate;
									} else {
										assert reach == ReachProp.NODEADEND_SD;
									}
								}
							} else {
								if (m_UpIterator.hasNext()) {
									m_Up = m_UpIterator.next();
									m_StateContainer = m_States.get(m_Up);
									m_DownIterator = m_StateContainer.getDownStates().keySet().iterator();
								} else {
									m_hasNext = false;
								}
							}
							
						}
					}

					@Override
					public boolean hasNext() {
						return m_hasNext;
					}

					@Override
					public UpDownEntry<STATE> next() {
						if (!hasNext()) {
							throw new NoSuchElementException();
						}
						STATE entry;
						Set<STATE> callSuccs = computeState2CallSuccs(m_Down);
						if (callSuccs.size() > 1 ) {
							throw new UnsupportedOperationException("State has more than one call successor");
						} else if (callSuccs.size() == 1 ) {
							entry = callSuccs.iterator().next();
						} else {
							entry = null;
							assert m_Down == getEmptyStackState();
						}
						UpDownEntry<STATE> result  = new UpDownEntry<STATE>(m_Up, m_Down, entry);
						computeNextElement();
						return result;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				
					
				};
				
			}
			
			/**
			 * Compute call successors for a given set of states.
			 */
			private Set<STATE> computeState2CallSuccs(STATE state) {
				Set<STATE> callSuccs = new HashSet<STATE>();
				if (state != getEmptyStackState()) {
					for (LETTER letter : lettersCall(state)) {
						for (STATE succ : succCall(state, letter)) {
							callSuccs.add(succ);
						}
					}
				}
				return callSuccs;
			}
			
			
		};

	}

	

	
	
	
	
	
	
	

	

	

	
	


	
	

	

	
	
	

	
	
	

	
	


	
	

	
	
	
	
	
	
	
	
	
	
	
	
	

	////////////////////////////////////////////////////////////////////////////
	// Methods to check correctness
	
	public boolean containsInternalTransition(STATE state, LETTER letter, STATE succ) {
		return m_States.get(state).containsInternalTransition(letter, succ);
	}
	
	public boolean containsCallTransition(STATE state, LETTER letter, STATE succ) {
		return m_States.get(state).containsCallTransition(letter, succ);
	}
	
	public boolean containsReturnTransition(STATE state, STATE hier, LETTER letter, STATE succ) {
		return m_States.get(state).containsReturnTransition(hier, letter, succ);
	}
	
	protected boolean containsSummaryReturnTransition(STATE lin, STATE hier, LETTER letter, STATE succ) {
		for (SummaryReturnTransition<LETTER, STATE> trans : returnSummarySuccessor(letter, hier)) {
			if (succ.equals(trans.getSucc()) && lin.equals(trans.getLinPred())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkTransitionsReturnedConsistent() {
		boolean result = true;
		for (STATE state : getStates()) {
			for (IncomingInternalTransition<LETTER, STATE> inTrans : internalPredecessors(state)) {
				result &= containsInternalTransition(inTrans.getPred(), inTrans.getLetter(), state);
				assert result;
				StateContainer<LETTER, STATE> cont  = m_States.get(state); 
				result &= cont.lettersInternalIncoming().contains(inTrans.getLetter());
				assert result;
				result &= cont.predInternal(inTrans.getLetter()).contains(inTrans.getPred());
				assert result;
			}
			for (OutgoingInternalTransition<LETTER, STATE> outTrans : internalSuccessors(state)) {
				result &= containsInternalTransition(state, outTrans.getLetter(), outTrans.getSucc());
				assert result;
				StateContainer<LETTER, STATE> cont  = m_States.get(state);
				result &= cont.lettersInternal().contains(outTrans.getLetter());
				assert result;
				result &= cont.succInternal(outTrans.getLetter()).contains(outTrans.getSucc());
				assert result;
			}
			for (IncomingCallTransition<LETTER, STATE> inTrans : callPredecessors(state)) {
				result &= containsCallTransition(inTrans.getPred(), inTrans.getLetter(), state);
				assert result;
				StateContainer<LETTER, STATE> cont  = m_States.get(state);
				result &= cont.lettersCallIncoming().contains(inTrans.getLetter());
				assert result;
				result &= cont.predCall(inTrans.getLetter()).contains(inTrans.getPred());
				assert result;
			}
			for (OutgoingCallTransition<LETTER, STATE> outTrans : callSuccessors(state)) {
				result &= containsCallTransition(state, outTrans.getLetter(), outTrans.getSucc());
				assert result;
				StateContainer<LETTER, STATE> cont  = m_States.get(state);
				result &= cont.lettersCall().contains(outTrans.getLetter());
				assert result;
				result &= cont.succCall(outTrans.getLetter()).contains(outTrans.getSucc());
				assert result;
			}
			for (IncomingReturnTransition<LETTER, STATE> inTrans : returnPredecessors(state)) {
				result &= containsReturnTransition(inTrans.getLinPred(), inTrans.getHierPred(), inTrans.getLetter(), state);
				assert result;
				result &= containsSummaryReturnTransition(inTrans.getLinPred(), inTrans.getHierPred(), inTrans.getLetter(), state);
				assert result;
				StateContainer<LETTER, STATE> cont  = m_States.get(state);
				result &= cont.lettersReturnIncoming().contains(inTrans.getLetter());
				assert result;
				result &= cont.predReturnHier(inTrans.getLetter()).contains(inTrans.getHierPred());
				assert result;
				result &= cont.predReturnLin(inTrans.getLetter(),inTrans.getHierPred()).contains(inTrans.getLinPred());
				assert result;
			}
			for (OutgoingReturnTransition<LETTER, STATE> outTrans : returnSuccessors(state)) {
				result &= containsReturnTransition(state, outTrans.getHierPred(), outTrans.getLetter(), outTrans.getSucc());
				assert result;
				result &= containsSummaryReturnTransition(state, outTrans.getHierPred(), outTrans.getLetter(), outTrans.getSucc());
				assert result;
				StateContainer<LETTER, STATE> cont  = m_States.get(state);
				result &= cont.lettersReturn().contains(outTrans.getLetter());
				assert result;
				result &= cont.hierPred(outTrans.getLetter()).contains(outTrans.getHierPred());
				assert result;
				result &= cont.succReturn(outTrans.getHierPred(),outTrans.getLetter()).contains(outTrans.getSucc());
				assert result;
			}
//			for (LETTER letter : lettersReturnSummary(state)) {
//				for (SummaryReturnTransition<LETTER, STATE> sumTrans : returnSummarySuccessor(letter, state)) {
//				result &= containsReturnTransition(state, sumTrans.getHierPred(), outTrans.getLetter(), outTrans.getSucc());
//				assert result;
//				StateContainer<LETTER, STATE> cont  = m_States.get(state);
//				result &= cont.lettersReturn().contains(outTrans.getLetter());
//				assert result;
//				result &= cont.hierPred(outTrans.getLetter()).contains(outTrans.getHierPred());
//				assert result;
//				result &= cont.succReturn(outTrans.getHierPred(),outTrans.getLetter()).contains(outTrans.getSucc());
//				assert result;
//				}
//			}
			
			
			for (LETTER letter : lettersInternal(state)) {
				for (STATE succ : succInternal(state, letter)) {
					result &= containsInternalTransition(state, letter, succ);
					assert result;
				}
			}
			for (LETTER letter : lettersCall(state)) {
				for (STATE succ : succCall(state, letter)) {
					result &= containsCallTransition(state, letter, succ);
					assert result;
				}
			}
			for (LETTER letter : lettersReturn(state)) {
				for (STATE hier : hierPred(state, letter)) {
					for (STATE succ : succReturn(state, hier, letter)) {
						result &= containsReturnTransition(state, hier, letter, succ);
						assert result;
					}
				}
			}
			for (LETTER letter : lettersInternalIncoming(state)) {
				for (STATE pred : predInternal(state, letter)) {
					result &= containsInternalTransition(pred, letter, state);
					assert result;
				}
			}
			for (LETTER letter : lettersCallIncoming(state)) {
				for (STATE pred : predCall(state, letter)) {
					result &= containsCallTransition(pred, letter, state);
					assert result;
				}
			}
			for (LETTER letter : lettersReturnIncoming(state)) {
				for (STATE hier : predReturnHier(state, letter)) {
					for (STATE lin : predReturnLin(state, letter, hier)) {
						result &= containsReturnTransition(lin, hier, letter, state);
						assert result;
					}
				}
			}
			
		}

		return result;
	}
	
//	private boolean cecSumConsistent() {
//		int sum = 0;
//		for (CommonEntriesComponent<LETTER,STATE> cec : m_AllCECs) {
//			sum += cec.m_Size;
//		}
//		int allStates = m_States.keySet().size();
//		return sum == allStates;
//	}
//	
//	private boolean allStatesAreInTheirCec() {
//		boolean result = true;
//		for (STATE state : m_States.keySet()) {
//			StateContainer<LETTER,STATE> sc = m_States.get(state);
//			CommonEntriesComponent<LETTER,STATE> cec = sc.getCommonEntriesComponent();
//			if (!cec.m_BorderOut.keySet().contains(sc)) {
//				Set<StateContainer<LETTER,STATE>> empty = new HashSet<StateContainer<LETTER,STATE>>();
//				result &= internalOutSummaryOutInCecOrForeigners(sc, empty, cec);
//			}
//		}
//		return result;
//	}
//	
//	private boolean occuringStatesAreConsistent(CommonEntriesComponent<LETTER,STATE> cec) {
//		boolean result = true;
//		Set<STATE> downStates = cec.m_DownStates;
//		Set<Entry<LETTER,STATE>> entries = cec.m_Entries;
//		if (cec.m_Size > 0) {
//			result &= downStatesAreCallPredsOfEntries(downStates, entries);
//		}
//		assert (result);
//		result &= eachStateHasThisCec(cec.getReturnOutCandidates(), cec);
//		assert (result);
//		for (StateContainer<LETTER, STATE> resident : cec.m_BorderOut.keySet()) {
//			Set<StateContainer<LETTER,STATE>> foreignerSCs = cec.m_BorderOut.get(resident);
//			result &= internalOutSummaryOutInCecOrForeigners(resident, foreignerSCs, cec);
//			assert (result);
//		}
//		return result;
//	}
//	
//	
//	private boolean downStatesConsistentwithEntriesDownStates(CommonEntriesComponent<LETTER,STATE> cec) {
//		boolean result = true;
//		Set<STATE> downStates = cec.m_DownStates;
//		Set<Entry<LETTER,STATE>> entries = cec.m_Entries;
//		Set<STATE> downStatesofEntries = new HashSet<STATE>();
//		for (Entry<LETTER,STATE> entry : entries) {
//			downStatesofEntries.addAll(entry.getDownStates().keySet());
//		}
//		result &= isSubset(downStates, downStatesofEntries);
//		assert (result);
//		result &= isSubset(downStatesofEntries, downStates);
//		assert (result);
//		return result;
//	}
//	
//	private boolean internalOutSummaryOutInCecOrForeigners(StateContainer<LETTER, STATE> state, Set<StateContainer<LETTER,STATE>> foreigners, CommonEntriesComponent<LETTER,STATE> cec) {
//		Set<StateContainer<LETTER,STATE>> neighbors = new HashSet<StateContainer<LETTER,STATE>>();
//		
//		for (OutgoingInternalTransition<LETTER, STATE> trans : state.internalSuccessors()) {
//			STATE succ = trans.getSucc();
//			StateContainer<LETTER,STATE> succSc = m_States.get(succ);
//			if (succSc.getCommonEntriesComponent() == cec) {
//				// do nothing
//			} else {
//				neighbors.add(succSc);
//			}
//		}
//		if (m_Summaries.containsKey(state)) {
//			for (StateContainer<LETTER,STATE> succSc : m_Summaries.get(state)) {
//				if (succSc.getCommonEntriesComponent() == cec) {
//					// do nothing
//				} else {
//					neighbors.add(succSc);
//				}
//			}
//		}
//		boolean allNeighborAreForeigners = isSubset(neighbors, foreigners);
//		assert allNeighborAreForeigners;
//		boolean allForeignersAreNeighbor = isSubset(foreigners, neighbors);
//		assert allForeignersAreNeighbor;
//		return allNeighborAreForeigners && allForeignersAreNeighbor;
//	}
//	
//	private boolean eachStateHasThisCec(Set<STATE> states, CommonEntriesComponent<LETTER,STATE> cec) {
//		boolean result = true;
//		for (STATE state : states) {
//			StateContainer<LETTER, STATE> sc = m_States.get(state);
//			if (sc.getCommonEntriesComponent() != cec) {
//				result = false;
//				assert result;
//			}
//		}
//		return result;
//	}
//	
//	private boolean downStatesAreCallPredsOfEntries(Set<STATE> downStates, Set<Entry<LETTER,STATE>> entries) {
//		Set<STATE> callPreds = new HashSet<STATE>();
//		for (Entry<LETTER,STATE> entry : entries) {
//			STATE entryState = entry.getState();
//			if (isInitial(entryState)) {
//				callPreds.add(getEmptyStackState());
//			}
//			for (IncomingCallTransition<LETTER, STATE> trans : callPredecessors(entryState)) {
//				callPreds.add(trans.getPred());
//			}
//		}
//		boolean callPredsIndownStates = isSubset(callPreds, downStates);
//		assert (callPredsIndownStates);
//		boolean downStatesInCallPreds = isSubset(downStates, callPreds);
//		assert (downStatesInCallPreds);
//		return callPredsIndownStates && downStatesInCallPreds;
//	}
//	
//	private boolean isBorderOutConsistent(StateContainer<LETTER,STATE> cont) {
//		CommonEntriesComponent<LETTER, STATE> cec = cont.getCommonEntriesComponent();
//		ArrayList<STATE> preds = new ArrayList<STATE>();
//		for(IncomingInternalTransition<LETTER, STATE> inTrans : internalPredecessors(cont.getState())) {
//			preds.add(inTrans.getPred());
//		}
//		for(IncomingReturnTransition<LETTER, STATE> inTrans  : returnPredecessors(cont.getState())) {
//			preds.add(inTrans.getHierPred());
//		}
//		boolean result = true;
//		for (STATE pred : preds) {
//			StateContainer<LETTER, STATE> predCont = m_States.get(pred);
//			if (predCont.getCommonEntriesComponent() != cec) {
//				if (predCont.getCommonEntriesComponent().m_BorderOut.containsKey(predCont)) {
//					Set<StateContainer<LETTER, STATE>> foreigners = 
//							predCont.getCommonEntriesComponent().m_BorderOut.get(predCont);
//					result &= foreigners.contains(cont); 
//				} else {
//					result = false;
//				}
//				assert result;
//			} else {
//				if (predCont.getCommonEntriesComponent().m_BorderOut.containsKey(predCont)) {
//					Set<StateContainer<LETTER, STATE>> foreigners = 
//							predCont.getCommonEntriesComponent().m_BorderOut.get(predCont);
//					result&= !foreigners.contains(cont);
//					assert result;
//				}
//			}
//		}
//		return result;
//	}
	
	
	
	
	

	////////////////////////////////////////////////////////////////////////////
	// Auxilliary Methods

	public static <E> boolean noElementIsNull(Collection<E> collection) {
		for (E elem : collection) {
			if (elem == null) return false;
		}
		return true;
	}
	
	private static <E> boolean isSubset(Set<E> lhs, Set<E> rhs) {
		for (E elem : lhs) {
			if (!rhs.contains(elem)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return (new AtsDefinitionPrinter<String,String>("nwa", this)).getDefinitionAsString();
	}

}
