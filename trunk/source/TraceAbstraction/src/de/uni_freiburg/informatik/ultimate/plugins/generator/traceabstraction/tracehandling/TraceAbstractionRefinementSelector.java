/*
 * Copyright (C) 2016 Christian Schilling (schillic@informatik.uni-freiburg.de)
 * Copyright (C) 2016 University of Freiburg
 *
 * This file is part of the ULTIMATE TraceAbstraction plug-in.
 *
 * The ULTIMATE TraceAbstraction plug-in is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The ULTIMATE TraceAbstraction plug-in is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the ULTIMATE TraceAbstraction plug-in. If not, see <http://www.gnu.org/licenses/>.
 *
 * Additional permission under GNU GPL version 3 section 7:
 * If you modify the ULTIMATE TraceAbstraction plug-in, or any covered work, by linking
 * or combining it with Eclipse RCP (or a modified version of Eclipse RCP),
 * containing parts covered by the terms of the Eclipse Public License, the
 * licensors of the ULTIMATE TraceAbstraction plug-in grant you additional permission
 * to convey the resulting work.
 */
package de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.tracehandling;

import de.uni_freiburg.informatik.ultimate.automata.IAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.IRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IToolchainStorage;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.SimplificationTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SmtUtils.XnfConversionTechnique;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SolverBuilder;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SolverBuilder.Settings;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.SolverBuilder.SolverMode;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.TermTransferrer;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.BoogieIcfgContainer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.util.RcfgProgramExecution;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.BasicCegarLoop;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.CachingHoareTripleChecker;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.InterpolantConsolidation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singletracecheck.PredicateUnifier;

/**
 * Checks a trace for feasibility and, if infeasible, selects a refinement strategy, i.e., constructs an interpolant
 * automaton.<br>
 * This class is used in the {@link BasicCegarLoop}.
 * <p>
 * TODO add timeout checks?
 *
 * @author Christian Schilling (schillic@informatik.uni-freiburg.de)
 */
public final class TraceAbstractionRefinementSelector
		implements IRefinementSelector<NestedWordAutomaton<CodeBlock, IPredicate>> {
	/* inputs */
	private final ILogger mLogger;
	private final TaCheckAndRefinementPreferences mPrefs;
	
	/* outputs */
	private final PredicateUnifier mPredicateUnifier;
	private final LBool mFeasibility;
	private NestedWordAutomaton<CodeBlock, IPredicate> mInterpolantAutomaton;
	private RcfgProgramExecution mRcfgProgramExecution;
	private final CachingHoareTripleChecker mHoareTripleChecker;
	
	public TraceAbstractionRefinementSelector(final IUltimateServiceProvider services, final ILogger logger,
			final TaCheckAndRefinementPreferences prefs, final IInterpolantAutomatonEvaluator evaluator,
			final PredicateFactory predicateFactory, final BoogieIcfgContainer icfgContainer,
			final SimplificationTechnique simplificationTechnique, final XnfConversionTechnique xnfConversionTechnique,
			final IToolchainStorage toolchainStorage, final TAPreferences taPrefsForInterpolantConsolidation,
			final int iteration, final IRun<CodeBlock, IPredicate, ?> counterexample,
			final IAutomaton<CodeBlock, IPredicate> abstraction) {
		// initialize fields
		mLogger = logger;
		mPrefs = prefs;
		
		mPredicateUnifier = new PredicateUnifier(services, mPrefs.getCfgSmtToolkit().getManagedScript(),
				predicateFactory, icfgContainer.getBoogie2SMT().getBoogie2SmtSymbolTable(), simplificationTechnique,
				xnfConversionTechnique);
		final ManagedScript managedScript = setupManagedScript(services, icfgContainer, toolchainStorage, iteration);
		
		// choose strategy
		final IRefinementStrategy strategy = chooseStrategy(counterexample, abstraction, services, managedScript,
				taPrefsForInterpolantConsolidation);
		
		mFeasibility = executeStrategy(strategy, evaluator);
		if (strategy.getInterpolantGenerator() instanceof InterpolantConsolidation) {
			mHoareTripleChecker =
					((InterpolantConsolidation) strategy.getInterpolantGenerator()).getHoareTripleChecker();
		} else {
			mHoareTripleChecker = null;
		}
	}
	
	@Override
	public LBool getCounterexampleFeasibility() {
		return mFeasibility;
	}
	
	@Override
	public RcfgProgramExecution getRcfgProgramExecution() {
		return mRcfgProgramExecution;
	}
	
	@Override
	public NestedWordAutomaton<CodeBlock, IPredicate> getInfeasibilityProof() {
		return mInterpolantAutomaton;
	}
	
	@Override
	public PredicateUnifier getPredicateUnifier() {
		return mPredicateUnifier;
	}
	
	@Override
	public CachingHoareTripleChecker getHoareTripleChecker() {
		return mHoareTripleChecker;
	}
	
	private IRefinementStrategy chooseStrategy(final IRun<CodeBlock, IPredicate, ?> counterexample,
			final IAutomaton<CodeBlock, IPredicate> abstraction, final IUltimateServiceProvider services,
			final ManagedScript managedScript, final TAPreferences taPrefsForInterpolantConsolidation) {
		// TODO add options in preferences, currently we only try the FixedTraceAbstractionRefinementStrategy
		final IRefinementStrategy strategy =
				new FixedTraceAbstractionRefinementStrategy(mLogger, mPrefs, managedScript, services,
						mPredicateUnifier, counterexample, abstraction, taPrefsForInterpolantConsolidation);
		return strategy;
	}
	
	private LBool executeStrategy(final IRefinementStrategy strategy, final IInterpolantAutomatonEvaluator evaluator) {
		do {
			// check feasibility using the strategy
			final LBool feasibility = strategy.getTraceChecker().isCorrect();
			
			if (feasibility == LBool.UNKNOWN && strategy.hasNext()) {
				// feasibility check failed, try next combination in the strategy
				strategy.next();
				continue;
			}
			
			switch (feasibility) {
				case UNKNOWN:
					if (mLogger.isInfoEnabled()) {
						mLogger.info("Strategy " + strategy.getClass().getSimpleName()
								+ " was unsuccessful and could not determine trace feasibility.");
					}
					mRcfgProgramExecution = strategy.getTraceChecker().getRcfgProgramExecution();
					break;
				case UNSAT:
					final NestedWordAutomaton<CodeBlock, IPredicate> automaton =
							strategy.getInterpolantAutomatonBuilder().getResult();
					
					if (evaluator.accept(automaton)) {
						mInterpolantAutomaton = automaton;
					} else {
						// TODO add code to construct the next automaton
						continue;
					}
					
					break;
				case SAT:
					// feasible counterexample, nothing more to do here
					mRcfgProgramExecution = strategy.getTraceChecker().getRcfgProgramExecution();
					break;
				default:
					throw new IllegalArgumentException("Unknown case: " + feasibility);
			}
			
			return feasibility;
		} while (true);
	}
	
	private ManagedScript setupManagedScript(final IUltimateServiceProvider services,
			final BoogieIcfgContainer icfgContainer, final IToolchainStorage toolchainStorage, final int iteration)
			throws AssertionError {
		final ManagedScript mgdScriptTc;
		if (mPrefs.getUseSeparateSolverForTracechecks()) {
			final String filename = icfgContainer.getFilename() + "_TraceCheck_Iteration" + iteration;
			final SolverMode solverMode = mPrefs.getSolverMode();
			final boolean fakeNonIncrementalSolver = mPrefs.getFakeNonIncrementalSolver();
			final String commandExternalSolver = mPrefs.getCommandExternalSolver();
			final boolean dumpSmtScriptToFile = mPrefs.getDumpSmtScriptToFile();
			final String pathOfDumpedScript = mPrefs.getPathOfDumpedScript();
			final Settings solverSettings = SolverBuilder.constructSolverSettings(filename, solverMode,
					fakeNonIncrementalSolver, commandExternalSolver, dumpSmtScriptToFile, pathOfDumpedScript);
			final Script tcSolver = SolverBuilder.buildAndInitializeSolver(services, toolchainStorage,
					mPrefs.getSolverMode(), solverSettings, false, false, mPrefs.getLogicForExternalSolver(),
					"TraceCheck_Iteration" + iteration);
			mgdScriptTc = new ManagedScript(services, tcSolver);
			final TermTransferrer tt = new TermTransferrer(tcSolver);
			for (final Term axiom : icfgContainer.getBoogie2SMT().getAxioms()) {
				tcSolver.assertTerm(tt.transform(axiom));
			}
		} else {
			mgdScriptTc = mPrefs.getCfgSmtToolkit().getManagedScript();
		}
		return mgdScriptTc;
	}
}