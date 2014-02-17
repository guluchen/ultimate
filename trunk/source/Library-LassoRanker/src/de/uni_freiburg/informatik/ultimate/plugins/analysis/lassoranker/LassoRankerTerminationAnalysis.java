/*
 * Copyright (C) 2012-2014 University of Freiburg
 *
 * This file is part of the ULTIMATE LassoRanker Library.
 *
 * The ULTIMATE LassoRanker Library is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * The ULTIMATE LassoRanker Library is distributed in the hope that it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE LassoRanker Library. If not,
 * see <http://www.gnu.org/licenses/>.
 * 
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE LassoRanker Library, or any covered work, by
 * linking or combining it with Eclipse RCP (or a modified version of
 * Eclipse RCP), containing parts covered by the terms of the Eclipse Public
 * License, the licensors of the ULTIMATE LassoRanker Library grant you
 * additional permission to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import de.uni_freiburg.informatik.ultimate.core.api.UltimateServices;
import de.uni_freiburg.informatik.ultimate.logic.LoggingScript;
import de.uni_freiburg.informatik.ultimate.logic.SMTLIBException;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.model.boogie.BoogieVar;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.Boogie2SMT;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker.exceptions.TermException;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker.preprocessors.DNF;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker.preprocessors.PreProcessor;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker.preprocessors.RemoveNegation;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker.preprocessors.RewriteBooleans;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker.preprocessors.RewriteDivision;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker.preprocessors.RewriteEquality;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker.preprocessors.RewriteIte;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker.preprocessors.RewriteStrictInequalities;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker.preprocessors.RewriteTrueFalse;
import de.uni_freiburg.informatik.ultimate.plugins.analysis.lassoranker.templates.RankingFunctionTemplate;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.TransFormula;


/**
 * This is the class that controls LassoRanker's (non-)termination argument
 * synthesis.
 * 
 * Tools that use LassoRanker as a library probably want to use this class
 * as an interface for invoking LassoRanker. This class can also be derived
 * for a more fine-grained control over the synthesis process.
 * 
 * @author Jan Leike
 */
public class LassoRankerTerminationAnalysis {
	private static Logger s_Logger =
			UltimateServices.getInstance().getLogger(Activator.s_PLUGIN_ID);
	
	/**
	 * Stem formula of the linear lasso program
	 */
	private TransFormula m_stem_transition;
	
	/**
	 * Loop formula of the linear lasso program
	 */
	private TransFormula m_loop_transition;
		
	/**
	 * Stem formula of the linear lasso program as linear inequalities in DNF
	 */
	private LinearTransition m_stem;
	
	/**
	 * Loop formula of the linear lasso program as linear inequalities in DNF
	 */
	private LinearTransition m_loop;
	
	/**
	 * SMT script that created the transition formulae
	 */
	protected Script m_old_script;
	
	/**
	 * New SMT script created by this class, used by the templates
	 */
	protected Script m_script;
	
	/**
	 * Auxiliary variables generated by the preprocessors
	 */
	protected Collection<TermVariable> m_auxVars;
	
	/**
	 * Number of supporting invariants generated by the last termination
	 * analysis
	 */
	private int m_numSIs = 0;
	
	/**
	 * Number of Motzkin's Theorem applications in the last termination
	 * analysis
	 */
	private int m_numMotzkin = 0;
	
	/**
	 * The current preferences
	 */
	protected Preferences m_preferences;
	
	/**
	 * The boogie2smt object that created the TransFormulas
	 */
	private Boogie2SMT m_boogie2smt;
	
	/**
	 * Constructor for the LassoRanker interface. Calling this invokes the
	 * preprocessor on the stem and loop formula.
	 * 
	 * If the stem is null, the stem has to be added separately by calling
	 * addStem().
	 * 
	 * @param script the SMT script used to construct the transition formulae
	 * @param boogie2smt the boogie2smt object that created the TransFormulas
	 * @param stem a transition formula corresponding to the lasso's stem
	 * @param loop a transition formula corresponding to the lasso's loop
	 * @param preferences configuration options for this plugin
	 * @throws TermException if preprocessing fails
	 */
	public LassoRankerTerminationAnalysis(Script script, Boogie2SMT boogie2smt,
			TransFormula stem, TransFormula loop, Preferences preferences)
					throws TermException {
		m_preferences = preferences;
		checkPreferences(preferences);
		m_boogie2smt = boogie2smt;
		
		m_old_script = script;
		m_script = SMTSolver.newScript(preferences.externalSolver,
				preferences.smt_solver_command,
				preferences.annotate_terms);
		if (preferences.dumpSmtSolverScript) {
			try {
				m_script = new LoggingScript(m_script,
						preferences.fileNameOfDumpedScript, true);
			} catch (FileNotFoundException e) {
				throw new AssertionError(e);
			}
		}
		
		m_auxVars = new HashSet<TermVariable>();
		
		m_stem_transition = stem;
		if (stem != null) {
			m_stem_transition = matchInVars(boogie2smt, m_stem_transition);
			s_Logger.debug("Stem transition:\n" + m_stem_transition);
			m_stem = preprocess(m_stem_transition);
			s_Logger.debug("Preprocessed stem:\n" + m_stem);
		} else {
			m_stem = null;
		}
		
		assert(loop != null);
		m_loop_transition = matchInVars(boogie2smt, loop);
		s_Logger.debug("Loop transition:\n" + m_loop_transition);
		m_loop = preprocess(m_loop_transition);
		s_Logger.debug("Preprocessed loop:\n" + m_loop);
	}
	
	/**
	 * Constructor for the LassoRanker interface. Calling this invokes the
	 * preprocessor on the stem and loop formula.
	 *  
	 * This constructor may only be supplied a loop transition, a stem has to
	 * be added later by calling addStem().
	 * 
	 * @param script the SMT script used to construct the transition formulae
	 * @param boogie2smt the boogie2smt object that created the TransFormulas
	 * @param loop a transition formula corresponding to the lasso's loop
	 * @param preferences configuration options for this plugin
	 * @throws TermException if preprocessing fails
	 */
	public LassoRankerTerminationAnalysis(Script script, Boogie2SMT boogie2smt,
			TransFormula loop, Preferences preferences) throws TermException {
		this(script, boogie2smt, null, loop, preferences);
	}
	

	/**
	 * Create a new TransForumla that has an inVar for all outVars.
	 * 
	 * This is required to prevent a problem that was reported by Matthias
	 * in Madrid.bpl. This problem occurs when there are outVars that do not
	 * have a corresponding inVar. Supporting invariant generation then becomes
	 * unsound for the inductiveness property.
	 * 
	 * @param formula input
	 * @return copy of the input formula with more inVars
	 */
	static TransFormula matchInVars(Boogie2SMT boogie2smt,
			TransFormula formula) {
		Map<BoogieVar, TermVariable> inVars =
				new HashMap<BoogieVar, TermVariable>(formula.getInVars());
		for (Map.Entry<BoogieVar, TermVariable> entry :
				formula.getOutVars().entrySet()) {
			if (!inVars.containsKey(entry.getKey())) {
				TermVariable inVar = TransFormula.getFreshVariable(boogie2smt,
						entry.getKey(), entry.getValue().getSort());
				inVars.put(entry.getKey(), inVar);
			}
		}
		return new TransFormula(
				formula.getFormula(),
				inVars,
				new HashMap<BoogieVar, TermVariable>(formula.getOutVars()),
				new HashSet<TermVariable>(formula.getAuxVars()),
				new HashSet<TermVariable>(formula.getBranchEncoders()),
				formula.isInfeasible(),
				formula.getClosedFormula(),
				true
		);
	}
	
	/**
	 * Verify that the preferences are set self-consistent and sensible
	 * Issues a bunch of logger infos and warnings.
	 */
	protected void checkPreferences(Preferences preferences) {
		assert preferences.num_strict_invariants >= 0;
		assert preferences.num_non_strict_invariants >= 0;
		assert preferences.termination_check_nonlinear
				|| preferences.only_nondecreasing_invariants
				: "Use nondecreasing invariants with a linear SMT query.";
		if (preferences.num_strict_invariants == 0 &&
				preferences.num_non_strict_invariants == 0) {
			s_Logger.warn("Generation of supporting invariants is disabled.");
		}
	}
	
	/**
	 * @param avm 
	 * @return an array of all preprocessors that should be called before
	 *         termination analysis
	 */
	protected PreProcessor[] getPreProcessors(AuxVarManager avm) {
		return new PreProcessor[] {
				new RewriteDivision(avm),
				new RewriteBooleans(avm),
				new RewriteIte(),
				new RewriteTrueFalse(),
				new RewriteEquality(),
				new DNF(),
				new RemoveNegation(),
				new RewriteStrictInequalities()
		};
	}
	
	/**
	 * Add a stem transition to the lasso program.
	 * Calling this invokes the preprocessor on the stem transition.
	 * 
	 * @param stem a transition formula corresponding to the lasso's stem
	 * @throws TermException 
	 */
	public void addStem(TransFormula stem_transition) throws TermException {
		if (m_stem != null) {
			s_Logger.warn("Adding a stem to a lasso that already had one.");
		}
		m_stem_transition = matchInVars(m_boogie2smt, stem_transition);
		s_Logger.debug("Adding stem transition:\n" + stem_transition);
		m_stem = preprocess(stem_transition);
		s_Logger.debug("Preprocessed stem:\n" + m_stem);
	}
	
	/**
	 * Preprocess the stem or loop transition. This applies the preprocessor
	 * classes and transforms the formula into a list of inequalities in DNF.
	 * 
	 * The list of preprocessors is given by this.getPreProcessors().
	 * 
	 * @see PreProcessor
	 * @throws TermException
	 */
	protected LinearTransition preprocess(TransFormula transition)
			throws TermException {
		s_Logger.info("Starting preprocessing step...");
		
		Term trans_term = transition.getFormula();
		
		AuxVarManager avm = new AuxVarManager(m_old_script);
		
		// Apply preprocessors
		for (PreProcessor preprocessor : this.getPreProcessors(avm)) {
			trans_term = preprocessor.process(m_old_script, trans_term);
		}
		m_auxVars = avm.getAuxVars();
		
		s_Logger.debug(SMTPrettyPrinter.print(trans_term));
		
		LinearTransition linear_trans = LinearTransition.fromTerm(trans_term);
		if (!m_preferences.enable_disjunction
				&& !linear_trans.isConjunctive()) {
			throw new UnsupportedOperationException(
					"Support for non-conjunctive lasso programs " +
					"is disabled.");
		}
		
		return linear_trans;
	}
	
	/**
	 * @return the number of variables occurring in the preprocessed loop
	 *         transition
	 */
	public int getLoopVarNum() {
		return m_loop.getVariables().size();
	}
	
	/**
	 * @return the number of variables occurring in the preprocessed stem
	 *         transition
	 */
	public int getStemVarNum() {
		if (m_stem != null) {
			return m_stem.getVariables().size();
		} else {
			return 0;
		}
	}
	
	/**
	 * @return the number of disjuncts in the loop transition's DNF after
	 *         preprocessing
	 */
	public int getLoopDisjuncts() {
		return m_loop.getNumPolyhedra();
	}
	
	/**
	 * @return the number of disjuncts in the stem transition's DNF after
	 *         preprocessing
	 */
	public int getStemDisjuncts() {
		if (m_stem != null) {
			return m_stem.getNumPolyhedra();
		} else {
			return 1;
		}
	}
	
	/**
	 * @return the number of supporting invariants generated by the last
	 * termination analysis
	 */
	public int getNumSIs() {
		return m_numSIs;
	}
	
	/**
	 * @return the number of Motzkin's Theorem applications in the last
	 * termination analysis
	 */
	public int getNumMotzkin() {
		return m_numMotzkin;
	}
	
	/**
	 * @return various statistics as a neatly formatted string
	 */
	public String getStatistics() {
		StringBuilder sb = new StringBuilder();
		sb.append("Number of variables in the stem: ");
		sb.append(getStemVarNum());
		sb.append("  Number of variables in the loop: ");
		sb.append(getLoopVarNum());
		sb.append("  Number of disjunctions in the stem: ");
		sb.append(getStemDisjuncts());
		sb.append("  Number of disjunctions in the loop: ");
		sb.append(getLoopDisjuncts());
		sb.append("  Number of supporting invariants: ");
		sb.append(getNumSIs());
		sb.append("  Number of Motzkin applications: ");
		sb.append(getNumMotzkin());
		return sb.toString();
	}
	
	/**
	 * Try to find a non-termination argument for the lasso program.
	 * 
	 * @return the non-termination argument or null of none is found
	 */
	public NonTerminationArgument checkNonTermination() {
		s_Logger.info("Checking for non-termination...");
		
		NonTerminationArgumentSynthesizer synthesizer =
				new NonTerminationArgumentSynthesizer(
						!m_preferences.nontermination_check_nonlinear,
						m_script,
						m_stem,
						m_loop,
						m_stem_transition,
						m_loop_transition
				);
		boolean nonterminating = synthesizer.checkForNonTermination();
		if (nonterminating) {
			s_Logger.info("Proved non-termination.");
			s_Logger.info(synthesizer.getArgument());
		}
		SMTSolver.resetScript(m_script, m_preferences.annotate_terms);
		return nonterminating ? synthesizer.getArgument() : null;
	}
	
	/**
	 * Try to find a termination argument for the lasso program specified by
	 * the given ranking function template.
	 * 
	 * @param template the ranking function template
	 * @return the non-termination argument or null of none is found
	 */
	public TerminationArgument tryTemplate(RankingFunctionTemplate template)
			throws SMTLIBException, TermException {
		// ignore stem
		s_Logger.info("Using template '" + template.getName()
				+ "'.");
		s_Logger.info("Template has degree " + template.getDegree() + ".");
		s_Logger.debug(template);
		
		TerminationArgumentSynthesizer synthesizer =
				new TerminationArgumentSynthesizer(m_script, m_stem_transition,
				m_loop_transition, m_stem, m_loop, m_preferences);
		final LBool constraintSat = synthesizer.synthesize(template);
		m_numSIs = synthesizer.getNumSIs();
		m_numMotzkin = synthesizer.getNumMotzkin();
		
		s_Logger.info(benchmarkScriptMessage(constraintSat, template));
		if (constraintSat == LBool.SAT) {
			s_Logger.info("Proved termination.");
			TerminationArgument arg = synthesizer.getArgument();
			s_Logger.info(arg);
			Term[] lexTerm = arg.getRankingFunction().asLexTerm(m_old_script);
			for (Term t : lexTerm) {
				s_Logger.debug(SMTPrettyPrinter.print(t));
			}
		} 		
		SMTSolver.resetScript(m_script, m_preferences.annotate_terms);
		return constraintSat == LBool.SAT ? synthesizer.getArgument() : null;
	}
	
	private String benchmarkScriptMessage(LBool constraintSat,
			RankingFunctionTemplate template) {
		StringBuilder sb = new StringBuilder();
		sb.append("BenchmarkResult: ");
		sb.append(constraintSat);
		sb.append(" for template ");
		sb.append(template.getName());
		sb.append(" with degree ");
		sb.append(template.getDegree());
		sb.append(". ");
		sb.append(getStatistics());
		return sb.toString();
	}
	
	/**
	 * Perform cleanup actions
	 */
	public void cleanUp() {
		m_script.exit();
		m_script = null;
	}
	
	public void finalize() {
		if (m_script != null) {
			m_script.exit();
			m_script = null;
		}
	}
}
