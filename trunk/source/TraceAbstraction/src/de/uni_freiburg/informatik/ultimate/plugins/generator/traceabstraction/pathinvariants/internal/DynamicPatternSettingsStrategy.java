package de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.pathinvariants.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.structure.IcfgLocation;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;

public class DynamicPatternSettingsStrategy extends LocationDependentLinearInequalityInvariantPatternStrategy {

	
	protected Map<IcfgLocation, Set<IProgramVar>> mLocations2LiveVariables;
	private Map<IcfgLocation, PatternSetting> mLoc2PatternSetting;
	
	public DynamicPatternSettingsStrategy(int baseDisjuncts, int baseConjuncts, int disjunctsPerRound,
			int conjunctsPerRound, int maxRounds, Set<IProgramVar> allProgramVariables) {
		super(baseDisjuncts, baseConjuncts, disjunctsPerRound, conjunctsPerRound, maxRounds, allProgramVariables);
		mLocations2LiveVariables = new HashMap<>();
		mLoc2PatternSetting = new HashMap<>();
	}
	
	public DynamicPatternSettingsStrategy(int baseDisjuncts, int baseConjuncts, int disjunctsPerRound,
			int conjunctsPerRound, int maxRounds, Set<IProgramVar> allProgramVariables, Map<IcfgLocation, Set<IProgramVar>> loc2LiveVariables) {
		super(baseDisjuncts, baseConjuncts, disjunctsPerRound, conjunctsPerRound, maxRounds, allProgramVariables);
		mLocations2LiveVariables = loc2LiveVariables;
		if (loc2LiveVariables == null) {
			mLocations2LiveVariables = new HashMap<>();
		}
		mLoc2PatternSetting = new HashMap<>();
	}
	
	private Set<IProgramVar> getPatternVariablesInitially (IcfgLocation loc) {
		if (mLocations2LiveVariables.containsKey(loc)) {
			return new HashSet<>(mLocations2LiveVariables.get(loc));
		} else {
			return new HashSet<>(mAllProgramVariables);
		}
	}
	
	private Collection<Collection<AbstractLinearInvariantPattern>> constructInvariantPatternForSetting(IcfgLocation location, PatternSetting ps, Script solver, String prefix) {
		assert super.mLoc2PatternCoefficents != null : "Map mLoc2PatternCoefficents must not be null!";
		Set<Term> patternCoefficients = new HashSet<>();
		// Build invariant pattern
		final Collection<Collection<AbstractLinearInvariantPattern>> disjunction = new ArrayList<>(ps.mNumOfDisjuncts);
		for (int i = 0; i < ps.mNumOfDisjuncts; i++) {
			final Collection<AbstractLinearInvariantPattern> conjunction = new ArrayList<>(
					ps.mNumOfConjuncts);
			for (int j = 0; j < ps.mNumOfConjuncts; j++) {
				final boolean[] invariantPatternCopies;
				invariantPatternCopies = new boolean[] { false };
				for (final boolean strict : invariantPatternCopies) {
					final LinearPatternBase inequality = new LinearPatternBase (
							solver, ps.getPatternVariables(), prefix + "_" + newPrefix(), strict);
					conjunction.add(inequality);
					// Add the coefficients of the inequality to our set of pattern coefficients
					patternCoefficients.addAll(inequality.getCoefficients());
				}
			}
			disjunction.add(conjunction);
		}
		super.mLoc2PatternCoefficents.put(location, patternCoefficients);
		return disjunction;
	}

	@Override
	public Collection<Collection<AbstractLinearInvariantPattern>> getInvariantPatternForLocation(IcfgLocation location,
			int round, Script solver, String prefix) {
		PatternSetting ps;
		if (!mLoc2PatternSetting.containsKey(location)) {
			// Create new setting for this location
			Set<IProgramVar> varsForThisPattern = getPatternVariablesInitially(location);
			ps = new PatternSetting(baseDisjuncts, baseConjuncts, varsForThisPattern);
			mLoc2PatternSetting.put(location, ps);
		} else {
			ps = mLoc2PatternSetting.get(location);
		}
		return constructInvariantPatternForSetting(location, ps, solver, prefix);
	}

	@Override
	public Collection<Collection<AbstractLinearInvariantPattern>> getInvariantPatternForLocation(IcfgLocation location,
			int round, Script solver, String prefix, Set<IProgramVar> varsFromUnsatCore) {
		PatternSetting ps;
		if (!mLoc2PatternSetting.containsKey(location)) {
			// Create new setting for this location
			Set<IProgramVar> varsForThisPattern = getPatternVariablesInitially(location);
			if (varsForThisPattern.containsAll(varsFromUnsatCore)) {
				// If the current set of variables is a superset of the set of variables from the unsat core, then we remove the residual variables.
				varsForThisPattern.retainAll(varsFromUnsatCore);
			}
			ps = new PatternSetting(baseDisjuncts, baseConjuncts, varsForThisPattern);
			mLoc2PatternSetting.put(location, ps);
		} else {
			ps = mLoc2PatternSetting.get(location);
			if (ps.getPatternVariables().containsAll(varsFromUnsatCore)) {
				ps.getPatternVariables().retainAll(varsFromUnsatCore);
			}
			if (mLocations2LiveVariables.containsKey(location)) {
				Set<IProgramVar> liveVars = mLocations2LiveVariables.get(location);
				// Add those variables from unsat core to pattern which are also live.
				for (IProgramVar var : varsFromUnsatCore ) {
					if (liveVars.contains(var)) {
						ps.getPatternVariables().add(var);
					}
				}
			} else {
				ps.getPatternVariables().addAll(varsFromUnsatCore);
			}
		}
		return constructInvariantPatternForSetting(location, ps, solver, prefix);
	}

	@Override
	public Set<IProgramVar> getPatternVariablesForLocation(IcfgLocation location, int round) {
		if (mLoc2PatternSetting.containsKey(location)) {
			return mLoc2PatternSetting.get(location).getPatternVariables();
		} else {
			throw new UnsupportedOperationException("There is no pattern setting for the given location: " + location);
		}
	}

	@Override
	public void changePatternSettingForLocation(IcfgLocation location) {
		if (mLoc2PatternSetting.containsKey(location)) {
			mLoc2PatternSetting.get(location).changeSetting();
		} else {
			throw new UnsupportedOperationException("There is no pattern setting for the given location: " + location);
		}
	}
	
	private class PatternSetting {
		private int mNumOfConjuncts;
		private static final int MAX_NUM_CONJUNCTS = 3;
		private int mNumOfDisjuncts;
		private static final int MAX_NUM_DISJUNCTS = 3;
		private Set<IProgramVar> mPatternVariables;
		
		public PatternSetting(int disjuncts, int conjuncts, Set<IProgramVar> vars) {
			mNumOfConjuncts = conjuncts;
			mNumOfDisjuncts = disjuncts;
			mPatternVariables = new HashSet<IProgramVar>(vars);
		}

		public Set<IProgramVar> getPatternVariables() {
			return mPatternVariables;
		}
		
		/** 
		 * TODO: Heuristic ?
		 */
		public void changeSetting() {
			if (mNumOfConjuncts < MAX_NUM_CONJUNCTS) {
				mNumOfConjuncts++;
			} else {
				if (mNumOfDisjuncts < MAX_NUM_DISJUNCTS) {
					mNumOfDisjuncts++;
					mNumOfConjuncts = 1;
				} else {
					throw new UnsupportedOperationException("Both number of conjuncts and disjuncts reached the maximum limit.");
				}
			}
		}
	}

}