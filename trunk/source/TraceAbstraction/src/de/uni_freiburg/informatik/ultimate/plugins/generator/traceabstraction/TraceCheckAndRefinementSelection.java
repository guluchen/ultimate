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
package de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction;

import java.util.List;
import java.util.TreeMap;

import de.uni_freiburg.informatik.ultimate.automata.AutomataOperationCanceledException;
import de.uni_freiburg.informatik.ultimate.automata.IAutomaton;
import de.uni_freiburg.informatik.ultimate.automata.IRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedRun;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWord;
import de.uni_freiburg.informatik.ultimate.automata.nestedword.NestedWordAutomaton;
import de.uni_freiburg.informatik.ultimate.core.model.services.ILogger;
import de.uni_freiburg.informatik.ultimate.core.model.services.IToolchainStorage;
import de.uni_freiburg.informatik.ultimate.core.model.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.logic.Script;
import de.uni_freiburg.informatik.ultimate.logic.Script.LBool;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.cfg.CfgSmtToolkit;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.hoaretriple.IHoareTripleChecker;
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
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.interpolantautomata.builders.IInterpolantAutomatonBuilder;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.interpolantautomata.builders.InterpolantAutomatonBuilderFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.PredicateFactory;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TaCheckAndRefinementPreferences;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TaCheckAndRefinementPreferences.TaCheckAndRefinementSettingPolicy;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singleTraceCheck.IInterpolantGenerator;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singleTraceCheck.InterpolantConsolidation;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singleTraceCheck.InterpolatingTraceChecker;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singleTraceCheck.InterpolatingTraceCheckerCraig;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singleTraceCheck.InterpolatingTraceCheckerPathInvariantsWithFallback;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singleTraceCheck.PredicateUnifier;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.singleTraceCheck.TraceCheckerSpWp;
import de.uni_freiburg.informatik.ultimate.util.ToolchainCanceledException;

/**
 * Checks a trace for feasibility and, if infeasible, selects a refinement strategy, i.e., constructs an interpolant
 * automaton.<br>
 * This class is used in the {@link BasicCegarLoop}.
 * 
 * @author Christian Schilling (schillic@informatik.uni-freiburg.de)
 */
public final class TraceCheckAndRefinementSelection {
	/* inputs */
	
	/**
	 * Logger.
	 */
	private final ILogger mLogger;
	/**
	 * Ultimate services.
	 */
	private final IUltimateServiceProvider mServices;
	/**
	 * Trace abstraction preferences.
	 */
	private final List<TaCheckAndRefinementPreferences> mPrefsList;
	/**
	 * Intermediate layer to encapsulate communication with SMT solvers.
	 */
	private final CfgSmtToolkit mCsToolkit;
	/**
	 * Predicate factory.
	 */
	private final PredicateFactory mPredicateFactory;
	/**
	 * Node of a recursive control flow graph which stores additional information about the program.
	 */
	private final BoogieIcfgContainer mIcfgContainer;
	
	private final SimplificationTechnique mSimplificationTechnique;
	private final XnfConversionTechnique mXnfConversionTechnique;
	private final IToolchainStorage mToolchainStorage;
	private final CegarLoopStatisticsGenerator mCegarLoopBenchmark;
	private final InterpolantAutomatonBuilderFactory mInterpolantAutomatonBuilderFactory;
	/**
	 * Current Iteration of the CEGAR loop.
	 */
	private final int mIteration;
	
	// TODO Christian 2016-11-11: Matthias wants to get rid of this
	private final TAPreferences mTaPrefsForInterpolantConsolidation;
	
	/* intermediate */
	
	private TaCheckAndRefinementPreferences mPrefs;
	/**
	 * Interpolant automaton evaluator.
	 */
	private final IInterpolantAutomatonEvaluator mEvaluator;
	
	/* outputs */
	
	/**
	 * Feasibility status that was determined for the counterexample.
	 */
	private LBool mFeasibility;
	/**
	 * Interpolant automaton that was constructed.
	 */
	private NestedWordAutomaton<CodeBlock, IPredicate> mInterpolantAutomaton;
	/**
	 * Interpolant generator that was used.
	 */
	private IInterpolantGenerator mInterpolantGenerator;
	/**
	 * Interpolating trace checker.
	 */
	private InterpolatingTraceChecker mInterpolatingTraceChecker;
	/**
	 * Predicate unifier.
	 */
	private final PredicateUnifier mPredicateUnifier;
	/**
	 * Hoare triple checker that was used.
	 */
	private IHoareTripleChecker mHoareTripleChecker;
	
	public TraceCheckAndRefinementSelection(final IUltimateServiceProvider services, final ILogger logger,
			final List<TaCheckAndRefinementPreferences> prefsList,
			final TaCheckAndRefinementSettingPolicy settingsPolicy, final IInterpolantAutomatonEvaluator evaluator,
			final CfgSmtToolkit cfgSmtToolkit, final PredicateFactory predicateFactory,
			final BoogieIcfgContainer icfgContainer, final SimplificationTechnique simplificationTechnique,
			final XnfConversionTechnique xnfConversionTechnique, final IToolchainStorage toolchainStorage,
			final CegarLoopStatisticsGenerator cegarLoopBenchmark,
			final InterpolantAutomatonBuilderFactory interpolantAutomatonBuilderFactory,
			final TAPreferences taPrefsForInterpolantConsolidation, final int iteration,
			final IRun<CodeBlock, IPredicate, ?> counterexample,
			final IAutomaton<CodeBlock, IPredicate> abstraction) throws AutomataOperationCanceledException {
		// initialize fields
		mServices = services;
		mLogger = logger;
		mPrefsList = prefsList;
		mCsToolkit = cfgSmtToolkit;
		mPredicateFactory = predicateFactory;
		mIcfgContainer = icfgContainer;
		mSimplificationTechnique = simplificationTechnique;
		mXnfConversionTechnique = xnfConversionTechnique;
		mToolchainStorage = toolchainStorage;
		mCegarLoopBenchmark = cegarLoopBenchmark;
		mInterpolantAutomatonBuilderFactory = interpolantAutomatonBuilderFactory;
		mIteration = iteration;
		mTaPrefsForInterpolantConsolidation = taPrefsForInterpolantConsolidation;
		
		mPredicateUnifier = new PredicateUnifier(mServices, mCsToolkit.getManagedScript(),
				mPredicateFactory, mIcfgContainer.getBoogie2SMT().getBoogie2SmtSymbolTable(),
				mSimplificationTechnique, mXnfConversionTechnique);
		
		mEvaluator = evaluator;
		
		execute(settingsPolicy, counterexample, abstraction);
	}
	
	public LBool getCounterexampleFeasibility() {
		return mFeasibility;
	}
	
	public NestedWordAutomaton<CodeBlock, IPredicate> getInterpolantAutomaton() {
		if (mInterpolantAutomaton == null) {
			throw new UnsupportedOperationException("There is no interpolant automaton.");
		}
		return mInterpolantAutomaton;
	}
	
	public IInterpolantGenerator getInterpolantGenerator() {
		return mInterpolantGenerator;
	}
	
	public InterpolatingTraceChecker getInterpolatingTraceChecker() {
		return mInterpolatingTraceChecker;
	}
	
	public PredicateUnifier getPredicateUnifier() {
		return mPredicateUnifier;
	}
	
	public IHoareTripleChecker getHoareTripleChecker() {
		if (mHoareTripleChecker == null) {
			throw new UnsupportedOperationException("There is no Hoare triple checker.");
		}
		return mHoareTripleChecker;
	}
	
	private void execute(final TaCheckAndRefinementSettingPolicy settingsPolicy,
			final IRun<CodeBlock, IPredicate, ?> counterexample, final IAutomaton<CodeBlock, IPredicate> abstraction)
			throws AutomataOperationCanceledException {
		switch (settingsPolicy) {
			case SEQUENTIAL:
				executeSequential(counterexample, abstraction);
				break;
			default:
				throw new IllegalArgumentException("Unknown settings policy: " + settingsPolicy);
		}
	}
	
	private void executeSequential(final IRun<CodeBlock, IPredicate, ?> counterexample,
			final IAutomaton<CodeBlock, IPredicate> abstraction) throws AutomataOperationCanceledException {
		// run through all preferences
		for (final TaCheckAndRefinementPreferences prefs : mPrefsList) {
			mPrefs = prefs;
			
			// check counterexample feasibility
			try {
				checkCounterexampleFeasibility(counterexample);
			} catch (final ToolchainCanceledException e) {
				throw e;
			} catch (final Exception e) {
				mLogger.info("Error during feasibility check, trying next one.");
				continue;
			}
			
			// construct interpolant automaton depending on feasibility
			if (mFeasibility == LBool.UNSAT) {
				constructInterpolantAutomaton(counterexample, abstraction);
			}
			
			return;
		}
	}
	
	private void checkCounterexampleFeasibility(final IRun<CodeBlock, IPredicate, ?> counterexample) {
		constructInterpolatingTraceChecker(counterexample);
		
		// check feasibility
		final LBool feasibility = mInterpolatingTraceChecker.isCorrect();
		mFeasibility = feasibility;
		
		if (feasibility != LBool.UNSAT) {
			// no infeasibility found, CEGAR loop is about to terminate
			return;
		}
		
		// mTraceCheckerBenchmark.aggregateBenchmarkData(interpolatingTraceChecker.getTraceCheckerBenchmark());
		final IInterpolantGenerator interpolantGenerator = constructInterpolantGenerator(counterexample);
		mInterpolantGenerator = interpolantGenerator;
		
		// TODO set Hoare triple check and use it in BasicCegarLoop
	}
	
	private void constructInterpolatingTraceChecker(final IRun<CodeBlock, IPredicate, ?> counterexample)
			throws AssertionError {
		final ManagedScript mgdScriptTc = setupManagedScript();
		
		mInterpolatingTraceChecker = constructTraceChecker(counterexample, mgdScriptTc);
		
		if (mInterpolatingTraceChecker.getToolchainCanceledExpection() != null) {
			throw mInterpolatingTraceChecker.getToolchainCanceledExpection();
		} else if (mPrefs.getUseSeparateSolverForTracechecks()) {
			mgdScriptTc.getScript().exit();
		}
	}
	
	private ManagedScript setupManagedScript() throws AssertionError {
		final ManagedScript mgdScriptTc;
		if (mPrefs.getUseSeparateSolverForTracechecks()) {
			final String filename = mIcfgContainer.getFilename() + "_TraceCheck_Iteration" + mIteration;
			final SolverMode solverMode = mPrefs.getSolverMode();
			final boolean fakeNonIncrementalSolver = mPrefs.getFakeNonIncrementalSolver();
			final String commandExternalSolver = mPrefs.getCommandExternalSolver();
			final boolean dumpSmtScriptToFile = mPrefs.getDumpSmtScriptToFile();
			final String pathOfDumpedScript = mPrefs.getPathOfDumpedScript();
			final Settings solverSettings = SolverBuilder.constructSolverSettings(filename, solverMode,
					fakeNonIncrementalSolver, commandExternalSolver, dumpSmtScriptToFile, pathOfDumpedScript);
			final Script tcSolver = SolverBuilder.buildAndInitializeSolver(mServices, mToolchainStorage,
					mPrefs.getSolverMode(), solverSettings, false, false, mPrefs.getLogicForExternalSolver(),
					"TraceCheck_Iteration" + mIteration);
			mgdScriptTc = new ManagedScript(mServices, tcSolver);
			final TermTransferrer tt = new TermTransferrer(tcSolver);
			for (final Term axiom : mIcfgContainer.getBoogie2SMT().getAxioms()) {
				tcSolver.assertTerm(tt.transform(axiom));
			}
		} else {
			mgdScriptTc = mCsToolkit.getManagedScript();
		}
		return mgdScriptTc;
	}
	
	private InterpolatingTraceChecker constructTraceChecker(final IRun<CodeBlock, IPredicate, ?> counterexample,
			final ManagedScript mgdScriptTc) {
		final IPredicate truePredicate = mPredicateUnifier.getTruePredicate();
		final IPredicate falsePredicate = mPredicateUnifier.getFalsePredicate();
		
		final InterpolatingTraceChecker interpolatingTraceChecker;
		switch (mPrefs.getInterpolationTechnique()) {
			case Craig_NestedInterpolation:
			case Craig_TreeInterpolation:
				interpolatingTraceChecker = new InterpolatingTraceCheckerCraig(truePredicate, falsePredicate,
						new TreeMap<Integer, IPredicate>(), NestedWord.nestedWord(counterexample.getWord()),
						mCsToolkit, mPrefs.getAssertCodeBlocksIncrementally(),
						mServices, true, mPredicateUnifier, mPrefs.getInterpolationTechnique(), mgdScriptTc,
						true, mXnfConversionTechnique, mSimplificationTechnique, counterexample.getStateSequence(),
						false);
				break;
			case ForwardPredicates:
			case BackwardPredicates:
			case FPandBP:
				interpolatingTraceChecker = new TraceCheckerSpWp(truePredicate, falsePredicate,
						new TreeMap<Integer, IPredicate>(), NestedWord.nestedWord(counterexample.getWord()), mCsToolkit,
						mPrefs.getAssertCodeBlocksIncrementally(), mPrefs.getUnsatCores(), mPrefs.getUseLiveVariables(),
						mServices, true, mPredicateUnifier, mPrefs.getInterpolationTechnique(), mgdScriptTc,
						mXnfConversionTechnique, mSimplificationTechnique, counterexample.getStateSequence());
				
				break;
			case PathInvariants:
				final boolean useNonlinearConstraints = mPrefs.getUseNonlinearConstraints();
				final boolean useVarsFromUnsatCore = mPrefs.getUseVarsFromUnsatCore();
				final boolean dumpSmtScriptToFile = mPrefs.getDumpSmtScriptToFile();
				final String pathOfDumpedScript = mPrefs.getPathOfDumpedScript();
				final String baseNameOfDumpedScript =
						"InVarSynth_" + mIcfgContainer.getFilename() + "_Iteration" + mIteration;
				final String solverCommand;
				if (useNonlinearConstraints) {
					// solverCommand = "yices-smt2 --incremental";
					// solverCommand = "/home/matthias/ultimate/barcelogic/barcelogic-NIRA -tlimit 5";
					solverCommand = "z3 -smt2 -in SMTLIB2_COMPLIANT=true -t:42000";
					// solverCommand = "z3 -smt2 -in SMTLIB2_COMPLIANT=true -t:1000";
				} else {
					// solverCommand = "yices-smt2 --incremental";
					solverCommand = "z3 -smt2 -in SMTLIB2_COMPLIANT=true -t:42000";
				}
				final boolean fakeNonIncrementalSolver = false;
				final Settings settings = new Settings(fakeNonIncrementalSolver, true, solverCommand, -1, null,
						dumpSmtScriptToFile, pathOfDumpedScript, baseNameOfDumpedScript);
				interpolatingTraceChecker =
						new InterpolatingTraceCheckerPathInvariantsWithFallback(truePredicate, falsePredicate,
								new TreeMap<Integer, IPredicate>(), (NestedRun<CodeBlock, IPredicate>) counterexample,
								mCsToolkit, mPrefs.getAssertCodeBlocksIncrementally(), mServices,
								mToolchainStorage, true, mPredicateUnifier, useNonlinearConstraints,
								useVarsFromUnsatCore,
								settings, mXnfConversionTechnique,
								mSimplificationTechnique, mIcfgContainer.getBoogie2SMT().getAxioms());
				break;
			default:
				throw new UnsupportedOperationException("unsupported interpolation");
		}
		mCegarLoopBenchmark.addTraceCheckerData(interpolatingTraceChecker.getTraceCheckerBenchmark());
		return interpolatingTraceChecker;
	}
	
	private IInterpolantGenerator constructInterpolantGenerator(final IRun<CodeBlock, IPredicate, ?> counterexample)
			throws AssertionError {
		final IInterpolantGenerator interpolantGenerator;
		if (mPrefs.getUseInterpolantConsolidation()) {
			try {
				interpolantGenerator = consolidateInterpolants(counterexample, mInterpolatingTraceChecker);
			} catch (final AutomataOperationCanceledException e) {
				// Timeout
				e.printStackTrace();
				throw new AssertionError("react on timeout, not yet implemented");
			}
		} else {
			interpolantGenerator = mInterpolatingTraceChecker;
		}
		return interpolantGenerator;
	}
	
	private IInterpolantGenerator consolidateInterpolants(final IRun<CodeBlock, IPredicate, ?> counterexample,
			final InterpolatingTraceChecker interpolatingTraceChecker) throws AutomataOperationCanceledException {
		final IInterpolantGenerator interpolantGenerator;
		final InterpolantConsolidation interpConsoli =
				new InterpolantConsolidation(mPredicateUnifier.getTruePredicate(),
						mPredicateUnifier.getFalsePredicate(), new TreeMap<Integer, IPredicate>(),
						NestedWord.nestedWord(counterexample.getWord()), mCsToolkit, mCsToolkit.getModifiableGlobals(),
						mServices, mLogger, mPredicateUnifier, interpolatingTraceChecker,
						mTaPrefsForInterpolantConsolidation);
		// Add benchmark data of interpolant consolidation
		mCegarLoopBenchmark.addInterpolationConsolidationData(interpConsoli.getInterpolantConsolidationBenchmarks());
		interpolantGenerator = interpConsoli;
		return interpolantGenerator;
	}
	
	private void constructInterpolantAutomaton(final IRun<CodeBlock, IPredicate, ?> counterexample,
			final IAutomaton<CodeBlock, IPredicate> abstraction) throws AutomataOperationCanceledException {
		if (mFeasibility != LBool.UNSAT) {
			throw new UnsupportedOperationException(
					"Constructing an interpolant automaton requires infeasible counterexample.");
		}
		
		// TODO add several strategies here
		mInterpolantAutomaton = constructInterpolantAutomatonDefault(counterexample, abstraction);
	}
	
	private NestedWordAutomaton<CodeBlock, IPredicate> constructInterpolantAutomatonDefault(
			final IRun<CodeBlock, IPredicate, ?> counterexample, final IAutomaton<CodeBlock, IPredicate> abstraction)
			throws AutomataOperationCanceledException {
		final IInterpolantAutomatonBuilder<CodeBlock, IPredicate> builder =
				mInterpolantAutomatonBuilderFactory.createBuilder(abstraction, mInterpolantGenerator, counterexample);
		return builder.getResult();
	}
	
	/**
	 * Evaluator for interpolant automata.
	 * 
	 * @author Christian Schilling (schillic@informatik.uni-freiburg.de)
	 */
	@FunctionalInterface
	public interface IInterpolantAutomatonEvaluator {
		/**
		 * @param interpolantAutomaton
		 *            Interpolant automaton.
		 * @return {@code true} iff automaton should be accepted
		 */
		boolean accept(NestedWordAutomaton<CodeBlock, IPredicate> interpolantAutomaton);
	}
}
