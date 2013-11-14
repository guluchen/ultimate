/*
 * Copyright (C) 2009-2012 University of Freiburg
 *
 * This file is part of SMTInterpol.
 *
 * SMTInterpol is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SMTInterpol is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SMTInterpol.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.uni_freiburg.informatik.ultimate.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Helper to compute the free variables contained in a term.  This class uses
 * memoization in the terms to compute the free variables efficiently on DAGs.
 * @author hoenicke
 */
public class ComputeFreeVariables extends NonRecursive.TermWalker {
	static final TermVariable[] NOFREEVARS = new TermVariable[0];
	
	public ComputeFreeVariables(Term term) {
		super(term);
	}

	public void walk(NonRecursive walker) {
		if (m_Term.m_freeVars != null)
			return;
		super.walk(walker);
	}

	@Override
	public void walk(NonRecursive walker, final AnnotatedTerm annot) {
		walker.enqueueWalker(new NonRecursive.Walker() {
			public void walk(NonRecursive walker) {
				annot.m_freeVars = annot.getSubterm().m_freeVars;
			}
		});
		walker.enqueueWalker(new ComputeFreeVariables(annot.getSubterm()));
	}
	
	static class AppTermWorker implements NonRecursive.Walker {
		ApplicationTerm term;
		public AppTermWorker(ApplicationTerm term) {
			this.term = term;
		}

		@Override
		public void walk(NonRecursive walker) {
			Term[] params = term.getParameters();
			if (params.length <= 1) {
				if (params.length == 1)
					term.m_freeVars = params[0].m_freeVars;
				else
					term.m_freeVars = ComputeFreeVariables.NOFREEVARS;
			} else {
				int biggestlen = 0;
				int biggestidx = -1;
				for (int i = 0; i < params.length; i++) {
					TermVariable[] free = params[i].m_freeVars;
					if (free.length > biggestlen) {
						biggestlen = free.length;
						biggestidx = i;
					}
				}
				/* return if term is closed */
				if (biggestidx < 0) {
					term.m_freeVars = ComputeFreeVariables.NOFREEVARS;
					return;
				}
				
				List<TermVariable> result = null;
				List<TermVariable> biggestAsList = 
					Arrays.asList(params[biggestidx].getFreeVars());
				for (int i = 0; i < params.length; i++) {
					if (i == biggestidx)
						continue;
					TermVariable[] free = params[i].getFreeVars();
					for (TermVariable tv : free) {
						if (!biggestAsList.contains(tv)) {
							if (result == null) {
								result = new ArrayList<TermVariable>();
								result.addAll(biggestAsList);
							}
							if (!result.contains(tv))
								result.add(tv);
						}
					}
				}
				if (result == null) {
					term.m_freeVars = params[biggestidx].getFreeVars();
				} else {
					term.m_freeVars = result.toArray(new TermVariable[result.size()]);
				}
			}
		}
		
		public String toString() {
			return "AppTermWalker:"+term.toStringDirect();
		}
	}

	@Override
	public void walk(NonRecursive walker, 
			            final ApplicationTerm term) {
		walker.enqueueWalker(new AppTermWorker(term));
		for (Term param : ((ApplicationTerm) m_Term).getParameters())
			walker.enqueueWalker(new ComputeFreeVariables(param));		
	}

	@Override
	public void walk(NonRecursive walker, ConstantTerm term) {
		term.m_freeVars = NOFREEVARS;
	}

	@Override
	public void walk(NonRecursive walker, final LetTerm letTerm) {
		walker.enqueueWalker(new NonRecursive.Walker() {
			public void walk(NonRecursive walker) {
				TermVariable[] vars = letTerm.getVariables();
				Term[] vals = letTerm.getValues();
				HashSet<TermVariable> free = new HashSet<TermVariable>();
				free.addAll(Arrays.asList(letTerm.getSubTerm().getFreeVars()));
				free.removeAll(Arrays.asList(vars));
				for (Term v : vals)
					free.addAll(Arrays.asList(v.getFreeVars()));
				if (!free.isEmpty())
					letTerm.m_freeVars = free.toArray(new TermVariable[free.size()]);
				else
					letTerm.m_freeVars = NOFREEVARS;
			}
		});
		walker.enqueueWalker(new ComputeFreeVariables(letTerm.getSubTerm()));
		for (Term value : letTerm.getValues())
			walker.enqueueWalker(new ComputeFreeVariables(value));
	}

	@Override
	public void walk(NonRecursive walker, final QuantifiedFormula quant) {
		walker.enqueueWalker(new NonRecursive.Walker() {
			public void walk(NonRecursive walker) {
				HashSet<TermVariable> free = new HashSet<TermVariable>();
				free.addAll(Arrays.asList(quant.getSubformula().getFreeVars()));
				free.removeAll(Arrays.asList(quant.getVariables()));
				if (!free.isEmpty())
					quant.m_freeVars = free.toArray(new TermVariable[free.size()]);
				else
					quant.m_freeVars = NOFREEVARS;
			}
		});
		walker.enqueueWalker(new ComputeFreeVariables(quant.getSubformula()));
	}

	@Override
	public void walk(NonRecursive walker, TermVariable term) {
		term.m_freeVars = new TermVariable[] {term};
	}
}