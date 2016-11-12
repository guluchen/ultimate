/*
 * Copyright (C) 2015 Jeffery Hsu (a71128@gmail.com)
 * Copyright (C) 2015 University of Freiburg
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
package de.uni_freiburg.informatik.ultimate.automata.nestedword.operations.incrementalinclusion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryException;
import de.uni_freiburg.informatik.ultimate.automata.AutomataLibraryServices;
import de.uni_freiburg.informatik.ultimate.automata.AutomataOperationCanceledException;
import de.uni_freiburg.informatik.ultimate.automata.IOperation;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.INestedWordAutomatonSimple;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.transitions.OutgoingInternalTransition;
import de.uni_freiburg.informatik.ultimate.automata.statefactory.IStateFactory;

/**
 * 
 * This is an implementation of incremental inclusion check based on the Rn Algorithm with force covering.(trace base version)<br/>
 * Unlike IncrementalInclusionCheck3, initial Rn set of each new node will be the expansion of its parent node's Rn set.<br/>
 * We use InclusionViaDIfference to check its correctness.
 * 
 * @author jefferyyjhsu@iis.sinica.edu.tw
 *
 * @param <LETTER>
 * @param <STATE>
 */

public class IncrementalInclusionCheck5_2<LETTER,STATE> extends AbstractIncrementalInclusionCheck<LETTER,STATE> implements IOperation<LETTER, STATE> {
	public int counter_run = 0, counter_total_nodes = 0 ;
	private final int counter;
	private final INestedWordAutomatonSimple<LETTER, STATE> local_mA;
	private final List<INestedWordAutomatonSimple<LETTER, STATE>> local_mB;
	private final ArrayList<INestedWordAutomatonSimple<LETTER,STATE>> local_mB2;
	private final IStateFactory<STATE> localStateFactory;
	private final AutomataLibraryServices localServiceProvider;
	//private int counter;
	//public HashMap<STATE,ArrayList<NodeData<LETTER,STATE>>> completeTree,currentTree,terminalNodes;
	//public HashMap<STATE,HashMap<NodeData<LETTER,STATE>,ArrayList<NodeData<LETTER,STATE>>>> coverage;
	NestedRun<LETTER,STATE> result;
	ArrayList<Leaf<LETTER,STATE>> startingLeafs = null,currentTerminalLeafs = null, completeLeafSet;
	HashSet<Leaf<LETTER,STATE>> bufferedLeaf;
	class Leaf<LET,STA>{
		public HashMap<LETTER,HashSet<Leaf<LET,STA>>> nextLeaf;
		public HashSet<Leaf<LET,STA>> covering,ParentLeafs;
		public Leaf<LET,STA> directParentLeaf,orginLeaf,coveredBy;
		public HashMap<INestedWordAutomatonSimple<LET,STA>,HashSet<STA>> bStates,completeBStates;
		public NestedRun<LET,STA> word;
		public STA aState;
		public Leaf(STA a,NestedRun<LET,STA> wd) {
			coveredBy = null;
			covering = new HashSet<Leaf<LET,STA>>();
			ParentLeafs = new HashSet<Leaf<LET,STA>>();
			nextLeaf = new HashMap<LETTER,HashSet<Leaf<LET,STA>>>();
			bStates = new HashMap<INestedWordAutomatonSimple<LET,STA>,HashSet<STA>>();
			completeBStates = new HashMap<INestedWordAutomatonSimple<LET,STA>,HashSet<STA>>();
			aState = a;
			word = wd;
		}
		public void setOrgin(Leaf<LET,STA> org){
			orginLeaf = org;
		}
		public void setParent(Leaf<LET,STA> par){
			directParentLeaf = par;
			if(par!=null){
				ParentLeafs.addAll(par.ParentLeafs);
				ParentLeafs.add(par);
			}
		}
	}
	/*class NodeData<A,B>{
		public HashMap<INestedWordAutomaton<A,B>,HashSet<B>> bStates;
		public NestedRun<A,B> word;
		private boolean covered;
		public NodeData(){
			bStates = new HashMap<INestedWordAutomaton<A,B>,HashSet<B>>();
			word = null;
			covered = false;
		}
		public NodeData(HashMap<INestedWordAutomaton<A,B>,HashSet<B>> data){
			bStates = data;
			word = null;
			covered = false;
		}
		public NodeData(NestedRun<A,B> data){
			bStates = new HashMap<INestedWordAutomaton<A,B>,HashSet<B>>();
			word = data;
			covered = false;
		}
		public NodeData(HashMap<INestedWordAutomaton<A,B>,HashSet<B>> data1,NestedRun<A,B> data2){
			bStates = data1;
			word = data2;
			covered = false;
		}
	}*/
	@Override
	public void addSubtrahend(INestedWordAutomatonSimple<LETTER, STATE> nwa) throws AutomataLibraryException {
		super.addSubtrahend(nwa);
		mLogger.info(startMessage());
		local_mB.add(nwa);
		local_mB2.add(nwa);
		run2(nwa);
		mLogger.info(exitMessage());
	}
	public IncrementalInclusionCheck5_2(AutomataLibraryServices services, IStateFactory<STATE> sf,
			INestedWordAutomatonSimple<LETTER, STATE> a, List<INestedWordAutomatonSimple<LETTER,STATE>> b) throws AutomataLibraryException{
		super(services,a);
		counter = 0;
		IncrementalInclusionCheck2.abortIfContainsCallOrReturn(a);
		//counter = 0;
		localServiceProvider = services;
		localStateFactory = sf;
		mLogger.info(startMessage());
		completeLeafSet = new ArrayList<Leaf<LETTER,STATE>>();
		local_mA = a;
		local_mB = new ArrayList<INestedWordAutomatonSimple<LETTER, STATE>>();
		local_mB2 = new ArrayList<INestedWordAutomatonSimple<LETTER, STATE>>(b);
		for(final INestedWordAutomatonSimple<LETTER,STATE> bn : b){
			try {
				super.addSubtrahend(bn);
			} catch (final AutomataLibraryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			local_mB.add(bn);
		}
		run();
		mLogger.info(exitMessage());
	}
	@SuppressWarnings("unchecked")
	public void run2(INestedWordAutomatonSimple<LETTER, STATE> nwa) throws AutomataOperationCanceledException{
		if(!local_mA.getAlphabet().containsAll(nwa.getAlphabet())){
			mLogger.info("Alphabet inconsistent");
			return;
		}
		completeBStatesAdder(null,nwa);
		if(result!=null){
			do{
				if (!mServices.getProgressMonitorService().continueProcessing()) {
	                throw new AutomataOperationCanceledException(this.getClass());
				}
				counter_run++;
				if(refine_exceptionRun()||cover()){
					break;
				}
				bufferedLeaf = null;
				for(final LETTER alphabet:local_mA.getAlphabet()){
					if(bufferedLeaf == null){
						bufferedLeaf = new HashSet<Leaf<LETTER,STATE>>();
						bufferedLeaf.addAll(expand(alphabet));
					}
					else{
						bufferedLeaf.addAll(expand(alphabet));
					}
				}	
				currentTerminalLeafs.clear();
				currentTerminalLeafs.addAll(bufferedLeaf);
			}while(true);
		}
	}
	@SuppressWarnings("unchecked")
	public void run() throws AutomataLibraryException{
		result = null;
		for(final INestedWordAutomatonSimple<LETTER,STATE> B:local_mB){
			if(!local_mA.getAlphabet().containsAll(B.getAlphabet())){
				mLogger.info("Alphabet inconsistent");
				return;
			}
		}
		do{
			counter_run++;
			if(currentTerminalLeafs==null){
				currentTerminalLeafs = expand(null);
				startingLeafs = (ArrayList<Leaf<LETTER, STATE>>) currentTerminalLeafs.clone();
				if(refine_exceptionRun()||cover()){
					break;
				}
			}
			else{
				if (!mServices.getProgressMonitorService().continueProcessing()) {
	                throw new AutomataOperationCanceledException(this.getClass());
				}
				bufferedLeaf = null;
				for(final LETTER alphabet:local_mA.getAlphabet()){
					if(bufferedLeaf == null){
						bufferedLeaf = new HashSet<Leaf<LETTER,STATE>>();
						bufferedLeaf.addAll(expand(alphabet));
					}
					else{
						bufferedLeaf.addAll(expand(alphabet));
					}
				}
				currentTerminalLeafs.clear();
				currentTerminalLeafs.addAll(bufferedLeaf);
				if(refine_exceptionRun()||cover()){
					break;
				}
			}
		}while(true);
	}
	@SuppressWarnings("unchecked")
	private ArrayList<Leaf<LETTER,STATE>> expand(LETTER alphabet){
		//mLogger.info(counter++);
		Iterable<OutgoingInternalTransition<LETTER,STATE>>nextStaSet = null;
		final ArrayList<Leaf<LETTER,STATE>> nextTerminal = new ArrayList<Leaf<LETTER,STATE>>();
		HashSet<STATE> newStaSet;
		Leaf<LETTER,STATE> newLeaf = null;
		if(alphabet == null){
			for(final STATE state:local_mA.getInitialStates()){
				newLeaf = new Leaf<LETTER,STATE>(state,new NestedRun<LETTER,STATE>(state));
				newLeaf.setOrgin(newLeaf);
				newLeaf.setParent(null);
				newLeaf.bStates = new HashMap<INestedWordAutomatonSimple<LETTER,STATE>,HashSet<STATE>>();
				newLeaf.completeBStates = new HashMap<INestedWordAutomatonSimple<LETTER,STATE>,HashSet<STATE>>();
				for(final INestedWordAutomatonSimple<LETTER,STATE> bn:local_mB){
					final HashSet<STATE> completeB = new HashSet<STATE>();
					completeB.addAll((Set<STATE>) bn.getInitialStates());
					newLeaf.completeBStates.put(bn, completeB);
				}
				nextTerminal.add(newLeaf);
				counter_total_nodes++;
				completeLeafSet.add(newLeaf);
			}
		}
		else{
			for(final Leaf<LETTER,STATE> oldLeaf:currentTerminalLeafs){
				if (oldLeaf.coveredBy==null){
					for(final OutgoingInternalTransition<LETTER, STATE> ATransition:local_mA.internalSuccessors(oldLeaf.aState, alphabet)){
						final ArrayList<STATE> newStateSequence = (ArrayList<STATE>) oldLeaf.word.getStateSequence().clone();
						newStateSequence.add(ATransition.getSucc());
						newLeaf = new Leaf<LETTER,STATE>(ATransition.getSucc(),new NestedRun<LETTER,STATE>(oldLeaf.word.getWord().concatenate(new NestedWord<LETTER>(alphabet,-2)),newStateSequence));
						newLeaf.setOrgin(oldLeaf.orginLeaf);
						newLeaf.setParent(oldLeaf);
						newLeaf.bStates = new HashMap<INestedWordAutomatonSimple<LETTER,STATE>,HashSet<STATE>>();
						/*if(oldLeaf.bStates.keySet().size()!=0){
						//if(true){
							for(INestedWordAutomatonSimple<LETTER,STATE> bn:oldLeaf.bStates.keySet()){
							//for(INestedWordAutomaton<LETTER,STATE> bn:local_mB){
								newLeaf.bStates.put(bn, new HashSet<STATE>());
								newStaSet = new HashSet<STATE>();
								for(STATE state:oldLeaf.bStates.get(bn)){
									nextStaSet = bn.internalSuccessors(state, alphabet);
									for(OutgoingInternalTransition<LETTER,STATE> newState:nextStaSet){
										newStaSet.add(newState.getSucc());
									}
								}
								newLeaf.bStates.get(bn).addAll((Collection<? extends STATE>) newStaSet.clone());
							}
						}*/
						for(final INestedWordAutomatonSimple<LETTER,STATE> bn:local_mB){
							newLeaf.completeBStates.put(bn, new HashSet<STATE>());
							newStaSet = new HashSet<STATE>();
							for(final STATE state:oldLeaf.completeBStates.get(bn)){
								nextStaSet = bn.internalSuccessors(state, alphabet);
								for(final OutgoingInternalTransition<LETTER,STATE> newState:nextStaSet){
									newStaSet.add(newState.getSucc());
								}
							}
							newLeaf.completeBStates.get(bn).addAll((Collection<? extends STATE>) newStaSet.clone());
						}
						if(!oldLeaf.nextLeaf.keySet().contains(alphabet)){
							oldLeaf.nextLeaf.put(alphabet, new HashSet<Leaf<LETTER,STATE>>());
						}
						oldLeaf.nextLeaf.get(alphabet).add(newLeaf);
						completeLeafSet.add(newLeaf);
						counter_total_nodes++;
						nextTerminal.add(newLeaf);
					}	
				}
				else{
					nextTerminal.add(oldLeaf);
				}
			}
		}
		return nextTerminal;
	}
	@SuppressWarnings("unchecked")
	private boolean cover(){
		HashMap<Leaf<LETTER,STATE>,HashMap<INestedWordAutomatonSimple<LETTER,STATE>,HashSet<STATE>>> tempBStates;
		ArrayList<STATE> newBnStates = null;
		int i;
		boolean newNodeInCompleteTree = false,forceCoveringExecuted = false;
		boolean containsAllbnState = false;
		Leaf<LETTER,STATE> BufCurLeaf2 = null, cursorLeaf = null;
		do{
			newNodeInCompleteTree = false;
			forceCoveringExecuted = false;
			for(final Leaf<LETTER,STATE> curLeaf1 : currentTerminalLeafs){
				tempBStates = new HashMap<Leaf<LETTER,STATE>,HashMap<INestedWordAutomatonSimple<LETTER,STATE>,HashSet<STATE>>>();
				containsAllbnState = false;
				if(curLeaf1.coveredBy == null){
					for(final Leaf<LETTER,STATE> curLeaf2 : completeLeafSet){
						BufCurLeaf2 = curLeaf2;
						if(curLeaf2.coveredBy == null&&curLeaf1!=curLeaf2&&curLeaf1.aState.equals(curLeaf2.aState)){
							tempBStates.put(curLeaf2, new HashMap<INestedWordAutomatonSimple<LETTER,STATE>,HashSet<STATE>>());
							containsAllbnState = true;
							for(final INestedWordAutomatonSimple<LETTER,STATE> bn:curLeaf2.bStates.keySet()){
								tempBStates.get(curLeaf2).put(bn, (HashSet<STATE>) curLeaf2.bStates.get(bn).clone());
								if(curLeaf1.bStates.keySet().contains(bn)){
									tempBStates.get(curLeaf2).get(bn).removeAll(curLeaf1.bStates.get(bn));
									if(tempBStates.get(curLeaf2).get(bn).size()!=0){
										containsAllbnState=false;
									}
								}
								else{
									containsAllbnState = false;
								}
							}
							if(containsAllbnState){
								break;
							}
						}
					}
					if(containsAllbnState){
						curLeaf1.coveredBy = BufCurLeaf2;
						BufCurLeaf2.covering.add(curLeaf1);
					}
					else{
						containsAllbnState = false;
						for(final Leaf<LETTER,STATE> curLeaf2 : tempBStates.keySet()){
							BufCurLeaf2 = curLeaf2;
							containsAllbnState = true;
							for(final INestedWordAutomatonSimple<LETTER,STATE> bn:tempBStates.get(curLeaf2).keySet()){
								if(curLeaf1.completeBStates.keySet().contains(bn)){
									if(!curLeaf1.completeBStates.get(bn).containsAll(tempBStates.get(curLeaf2).get(bn))){
										containsAllbnState=false;
									}
								}
								else{
									containsAllbnState = false;
								}
								if(!containsAllbnState){
									break;
								}
							}
							if(containsAllbnState){
								break;
							}
						}
						if(containsAllbnState){
							for(final INestedWordAutomatonSimple<LETTER,STATE> bn:tempBStates.get(BufCurLeaf2).keySet()){
								if(!curLeaf1.bStates.containsKey(bn)){
									curLeaf1.bStates.put(bn, new HashSet<STATE>());
								}
								for(final STATE newBState:tempBStates.get(BufCurLeaf2).get(bn)){
									newBnStates = NestedRunStates(bn,curLeaf1,newBState);
									i = newBnStates.size()-1;
									cursorLeaf = curLeaf1;
									while(cursorLeaf!=null){
										if(!cursorLeaf.bStates.containsKey(bn)||!cursorLeaf.bStates.get(bn).contains(newBnStates.get(i))){
											if(!cursorLeaf.bStates.containsKey(bn)){
												cursorLeaf.bStates.put(bn,new HashSet<STATE>());
											}
											cursorLeaf.bStates.get(bn).add(newBnStates.get(i));
											for(final Leaf<LETTER,STATE> orgCover:cursorLeaf.covering){
												forceCoveringExecuted = true; 
												orgCover.coveredBy = null;
											}
											cursorLeaf.covering.clear();
										}
										cursorLeaf = cursorLeaf.directParentLeaf;
										i--;
									}
								}
							}
							containsAllbnState = true;
							for(final INestedWordAutomatonSimple<LETTER,STATE> bn:BufCurLeaf2.bStates.keySet()){
								if(curLeaf1.bStates.keySet().contains(bn)){
									if(!curLeaf1.bStates.get(bn).containsAll(BufCurLeaf2.bStates.get(bn))){
										containsAllbnState=false;
									}
								}
								else{
									containsAllbnState = false;
								}
								if(!containsAllbnState){
									break;
								}
							}
							if(containsAllbnState){
								curLeaf1.coveredBy = BufCurLeaf2;
								BufCurLeaf2.covering.add(curLeaf1);
							}
							else{
								newNodeInCompleteTree = true;	
							}
							
						}
						else{
							newNodeInCompleteTree = true;	
						}
					}
				}
			}
		}while(forceCoveringExecuted);
		return !newNodeInCompleteTree;
	}
	private boolean refine_exceptionRun(){
		final HashSet<Leaf<LETTER,STATE>> newEdge = new HashSet<Leaf<LETTER,STATE>>(),toBeRemoved = new HashSet<Leaf<LETTER,STATE>>(), toBeRemovedBuffer = new HashSet<Leaf<LETTER,STATE>>();
		boolean firstRound = true;
		ArrayList<STATE> newBnStates = null;
		int i;
		Leaf<LETTER,STATE> cursorLeaf = null,cursorLeaf2 = null,newEdgeLeaf = null;
		final HashSet<INestedWordAutomatonSimple<LETTER,STATE>> CHKedBn = new HashSet<INestedWordAutomatonSimple<LETTER,STATE>>();
		boolean chkExpandedBn = true;
		result = null;
		boolean foundFinal = false;
		for(final Leaf<LETTER,STATE> curLeaf :currentTerminalLeafs){
			if(local_mA.isFinal(curLeaf.aState)){
				CHKedBn.clear();
				chkExpandedBn = true;
				foundFinal = false;
				for(final INestedWordAutomatonSimple<LETTER,STATE> bn:curLeaf.bStates.keySet()){
					for(final STATE bnState:curLeaf.bStates.get(bn)){
						if(bn.isFinal(bnState)){
							foundFinal = true;
							break;
						}
					}
					if(foundFinal){
						break;
					}
				}
				if(!foundFinal){
					cursorLeaf2=curLeaf.directParentLeaf;
					while(cursorLeaf2!=null){
						for(final INestedWordAutomatonSimple<LETTER,STATE> bn:cursorLeaf2.bStates.keySet()){
							if(!CHKedBn.contains(bn)){
								CHKedBn.add(bn);
								for(final STATE bState:curLeaf.completeBStates.get(bn)){
									if(bn.isFinal(bState)){
										chkExpandedBn = false;
										foundFinal = true;
										newBnStates = NestedRunStates(bn,curLeaf,bState);
										i = newBnStates.size()-1;
										cursorLeaf = curLeaf;
										firstRound = true;
										newEdgeLeaf = null;
										while(cursorLeaf!=null){
											if(!cursorLeaf.bStates.containsKey(bn)||!cursorLeaf.bStates.get(bn).contains(newBnStates.get(i))){
												if(!cursorLeaf.bStates.containsKey(bn)){
													cursorLeaf.bStates.put(bn,new HashSet<STATE>());
												}
												cursorLeaf.bStates.get(bn).add(newBnStates.get(i));
												for(final Leaf<LETTER,STATE> orgCover:cursorLeaf.covering){
													orgCover.coveredBy = null;
												}
												cursorLeaf.covering.clear();
												if(firstRound == false&&CoveringCheck(cursorLeaf)){
													newEdgeLeaf = cursorLeaf;
												}
											}
											else{
												break;
											}
											cursorLeaf = cursorLeaf.directParentLeaf;
											firstRound = false;
											i--;
										}
										if(newEdgeLeaf!=null){
											newEdge.add(newEdgeLeaf);
										}
										break;
									}
								}
							}
						}
						if(foundFinal==true){
							break;
						}
						cursorLeaf2=cursorLeaf2.directParentLeaf;
					}
					if(chkExpandedBn){
						for(final INestedWordAutomatonSimple<LETTER,STATE> bn:local_mB){
							if(!CHKedBn.contains(bn)){
								for(final STATE bState:curLeaf.completeBStates.get(bn)){
									if(bn.isFinal(bState)){
										foundFinal = true;
										newBnStates = NestedRunStates(bn,curLeaf,bState);
										i = newBnStates.size()-1;
										cursorLeaf = curLeaf;
										firstRound = true;
										newEdgeLeaf = null;
										while(cursorLeaf!=null){
											if(!cursorLeaf.bStates.containsKey(bn)||!cursorLeaf.bStates.get(bn).contains(newBnStates.get(i))){
												if(!cursorLeaf.bStates.containsKey(bn)){
													cursorLeaf.bStates.put(bn,new HashSet<STATE>());
												}
												cursorLeaf.bStates.get(bn).add(newBnStates.get(i));
												for(final Leaf<LETTER,STATE> orgCover:cursorLeaf.covering){
													orgCover.coveredBy = null;
												}
												cursorLeaf.covering.clear();
												if(firstRound == false&&CoveringCheck(cursorLeaf)){
													newEdgeLeaf = cursorLeaf;
												}
											}
											else{
												break;
											}
											cursorLeaf = cursorLeaf.directParentLeaf;
											firstRound = false;
											i--;
										}
										if(newEdgeLeaf!=null){
											newEdge.add(newEdgeLeaf);
											
										}
										break;
									}
								}
							}
						}
					}
					if(!foundFinal){
						result = curLeaf.word;
						break;
					}
				}
			}
		}
		if(result==null){
			for(final Leaf<LETTER,STATE> cursorLeaf3:newEdge){
				toBeRemoved.addAll(childrenLeafWalker(cursorLeaf3));
			}
			for(final Leaf<LETTER,STATE> cursorLeaf3:newEdge){
				if(toBeRemoved.contains(cursorLeaf3)){
					toBeRemovedBuffer.add(cursorLeaf3);
				}
			}
			newEdge.removeAll(toBeRemovedBuffer);
			toBeRemovedBuffer.clear();
			for(final Leaf<LETTER,STATE> cursorLeaf3 :currentTerminalLeafs){
				if(toBeRemoved.contains(cursorLeaf3)){
					toBeRemovedBuffer.add(cursorLeaf3);
				}
			}
			for(final Leaf<LETTER,STATE> cursorLeaf3:newEdge){
				cursorLeaf3.nextLeaf.clear();
			}
			for(final Leaf<LETTER,STATE> cursorLeaf3:toBeRemovedBuffer){
				for(final Leaf<LETTER,STATE> cursorLeaf4:cursorLeaf3.covering){
					cursorLeaf4.coveredBy=null;
				}
			}
			for(final Leaf<LETTER,STATE> cursorLeaf3:toBeRemoved){
				for(final Leaf<LETTER,STATE> cursorLeaf4:cursorLeaf3.covering){
					cursorLeaf4.coveredBy=null;
				}
			}
			currentTerminalLeafs.removeAll(toBeRemovedBuffer);
			completeLeafSet.removeAll(toBeRemoved);
			currentTerminalLeafs.addAll(newEdge);
		}
		return result!=null;
	}
	private HashSet<Leaf<LETTER,STATE>> childrenLeafWalker (Leaf<LETTER,STATE> curLeaf){
		final HashSet<Leaf<LETTER,STATE>> leafSet = new HashSet<Leaf<LETTER,STATE>>();
		for(final LETTER alphabet:curLeaf.nextLeaf.keySet()){
			for(final Leaf<LETTER,STATE> childrenLeaf:curLeaf.nextLeaf.get(alphabet)){
				leafSet.add(childrenLeaf);
				leafSet.addAll(childrenLeafWalker(childrenLeaf));
			}
		}
		return leafSet;
	}
	@SuppressWarnings("unchecked")
	private void completeBStatesAdder(Leaf<LETTER,STATE> parent, INestedWordAutomatonSimple<LETTER,STATE> nwa){
		if(parent==null){
			for(final Leaf<LETTER,STATE> curLeaf :startingLeafs){
				curLeaf.completeBStates.put(nwa, new HashSet<STATE>());
				curLeaf.completeBStates.get(nwa).addAll((Collection<? extends STATE>) nwa.getInitialStates());
				if(hasChildrenLeaf(curLeaf)){
					completeBStatesAdder(curLeaf,nwa);
				}
			}
		}
		else{
			Iterable<OutgoingInternalTransition<LETTER,STATE>>nextStaSet = null;
			for(final LETTER alphabet:local_mA.getAlphabet()){
				final HashSet<STATE> newBStaes = new HashSet<STATE>();
				for(final STATE OState : parent.completeBStates.get(nwa)){
					nextStaSet = nwa.internalSuccessors(OState, alphabet);
					for(final OutgoingInternalTransition<LETTER,STATE> newState:nextStaSet){
						newBStaes.add(newState.getSucc());
					}
				}
				if(parent.nextLeaf.get(alphabet)!=null){
					for(final Leaf<LETTER,STATE> childrenLeaf:parent.nextLeaf.get(alphabet)){
						childrenLeaf.completeBStates.put(nwa, (HashSet<STATE>) newBStaes.clone());
						if(hasChildrenLeaf(childrenLeaf)){
							completeBStatesAdder(childrenLeaf,nwa);
						}
					}
				}
			}
		}
	}
	private boolean hasChildrenLeaf(Leaf<LETTER,STATE> parent){
		boolean hasChildren = false;
		for(final LETTER alphabet:parent.nextLeaf.keySet()){
			if(parent.nextLeaf.get(alphabet).size()>0){
				hasChildren = true;
			}
		}
		return hasChildren;
	}
	@SuppressWarnings("unchecked")
	private ArrayList<STATE> NestedRunStates(INestedWordAutomatonSimple<LETTER,STATE> bn,Leaf<LETTER,STATE> targetLeaf,STATE edgeState){
		Iterable<OutgoingInternalTransition<LETTER, STATE>> nextStaSet;
		final ArrayList<STATE> result2 = new ArrayList<STATE>(targetLeaf.word.getLength());
		Leaf<LETTER,STATE> cursorLeaf = targetLeaf;
		final List<LETTER> wordList = targetLeaf.word.getWord().asList();
		boolean breakLoop = false;
		int i;
		for(i=0;i<targetLeaf.word.getLength();i++){
			result2.add(null);
		}
		i = targetLeaf.word.getLength()-1;
		while(i>=0){
			if(i==targetLeaf.word.getLength()-1){
				result2.set(i, edgeState);
			}
			else{
				for(final STATE s: cursorLeaf.completeBStates.get(bn)){
					breakLoop = false;
					nextStaSet = bn.internalSuccessors(s, wordList.get(i));
					for(final OutgoingInternalTransition<LETTER,STATE> newState:nextStaSet){
						if(newState.getSucc().equals(result2.get(i+1))){
							result2.set(i,s);
							breakLoop = true;
							break;
						}
					}
					if(breakLoop){
						break;
					}
				}
			}
			cursorLeaf = cursorLeaf.directParentLeaf;
			i--;
		}
		return result2;
	}
	public boolean CoveringCheck(Leaf<LETTER,STATE> checkingLeaf){
		boolean containsAllbnState = false;
		for(final Leaf<LETTER,STATE> curLeaf2 : completeLeafSet){
			if(curLeaf2.coveredBy == null&&checkingLeaf!=curLeaf2&&checkingLeaf.aState.equals(curLeaf2.aState)&&!curLeaf2.ParentLeafs.contains(checkingLeaf)){
				containsAllbnState = true;
				for(final INestedWordAutomatonSimple<LETTER,STATE> bn:curLeaf2.bStates.keySet()){
					if(checkingLeaf.bStates.keySet().contains(bn)){
						if(!checkingLeaf.bStates.get(bn).containsAll(curLeaf2.bStates.get(bn))){
							containsAllbnState=false;
						}
					}
					else{
						containsAllbnState = false;
					}
					if(!containsAllbnState){
						break;
					}
				}
				if(containsAllbnState){
					break;
				}
			}
		}
		return containsAllbnState;
	}
	@Override
	public NestedRun<LETTER,STATE> getCounterexample(){
		return result;
	}
	@Override
	public String operationName() {
		return "IncrementalInclusionCheck5_2.";
	}
	@Override
	public String startMessage() {
		return "Start " + operationName();
	}
	
	@Override
	public String exitMessage() {
		mLogger.info("total:"+counter_total_nodes+"nodes");
		mLogger.info(completeLeafSet.size()+"nodes in the end");
		mLogger.info("total:"+counter_run+"runs");
		return "Exit " + operationName();
	}
	@Override
	public Boolean getResult(){
		return result == null;
	}
	@Override
	public boolean checkResult(IStateFactory<STATE> stateFactory)
			throws AutomataLibraryException {
		final boolean checkResult = IncrementalInclusionCheck2.compareInclusionCheckResult(
				localServiceProvider, localStateFactory, local_mA, local_mB2, result);
		return checkResult;
//		if(getResult().equals((new IncrementalInclusionCheck2<LETTER, STATE>(localServiceProvider,localStateFactory,local_mA,local_mB2)).getResult())){
//			//if(getResult2().equals((new InclusionViaDifference(localServiceProvider,localStateFactory,).getCounterexample().getLength()==0))){
//				return true;
//			}
//			else{
//				return false;
//			}
	}
}