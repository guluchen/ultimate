/**
 * 
 */
package de.uni_freiburg.informatik.ultimate.plugins.spaceex.icfg;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.uni_freiburg.informatik.ultimate.logic.ApplicationTerm;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.IIcfgSymbolTable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.ILocalProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramConst;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramNonOldVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.variables.IProgramVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.plugins.spaceex.automata.hybridsystem.HybridAutomaton;

/**
 * Class that implements an IIcfgSymbolTable
 * 
 * @author Julian Loeffler (loefflju@informatik.uni-freiburg.de)
 *
 */
public class HybridIcfgSymbolTable implements IIcfgSymbolTable {
	
	private final Set<ILocalProgramVar> mLocals = new HashSet<>();
	private final Map<TermVariable, ILocalProgramVar> mTVtoProgVar;
	private final ManagedScript mScript;
	
	/**
	 * Constructor
	 * 
	 * @param script
	 * @param automaton
	 */
	public HybridIcfgSymbolTable(ManagedScript script, HybridAutomaton automaton, String procedure,
			HybridVariableManager variableManager) {
		mScript = script;
		mTVtoProgVar = new HashMap<>();
		final Set<String> variables = automaton.getGlobalParameters();
		variables.addAll(automaton.getGlobalConstants());
		variables.addAll(automaton.getLocalConstants());
		variables.addAll(automaton.getLocalParameters());
		for (final String var : variables) {
			// Termvariables for the transformula.
			final TermVariable inVar = script.constructFreshTermVariable(var, script.getScript().sort("Real"));
			final TermVariable outVar = script.constructFreshTermVariable(var, script.getScript().sort("Real"));
			// IProgramVar for the transformula.
			final HybridProgramVar progVar = variableManager.constructProgramVar(var, procedure);
			variableManager.addInVarTermVariable(var, inVar);
			variableManager.addOutVarTermVariable(var, outVar);
			variableManager.addProgramVar(var, progVar);
			mTVtoProgVar.put(inVar, progVar);
			mTVtoProgVar.put(outVar, progVar);
			mLocals.add(progVar);
		}
		
	}
	
	@Override
	public Set<ILocalProgramVar> getLocals(final String procedurename) {
		return mLocals;
	}
	
	@Override
	public Set<IProgramNonOldVar> getGlobals() {
		return Collections.emptySet();
	}
	
	@Override
	public Set<IProgramConst> getConstants() {
		return Collections.emptySet();
	}
	
	@Override
	public IProgramVar getProgramVar(TermVariable tv) {
		return mTVtoProgVar.get(tv);
	}
	
	@Override
	public IProgramConst getProgramConst(ApplicationTerm at) {
		// TODO Auto-generated method stub
		return null;
	}
	
}