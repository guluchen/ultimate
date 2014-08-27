package de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import de.uni_freiburg.informatik.ultimate.access.IUnmanagedObserver;
import de.uni_freiburg.informatik.ultimate.access.WalkerOptions;
import de.uni_freiburg.informatik.ultimate.automata.ExampleNWAFactory;
import de.uni_freiburg.informatik.ultimate.automata.nwalibrary.buchiNwa.NestedLassoRun;
import de.uni_freiburg.informatik.ultimate.core.preferences.UltimatePreferenceStore;
import de.uni_freiburg.informatik.ultimate.core.services.IToolchainStorage;
import de.uni_freiburg.informatik.ultimate.core.services.IUltimateServiceProvider;
import de.uni_freiburg.informatik.ultimate.lassoranker.nontermination.NonTerminationArgument;
import de.uni_freiburg.informatik.ultimate.logic.Rational;
import de.uni_freiburg.informatik.ultimate.model.IElement;
import de.uni_freiburg.informatik.ultimate.model.ITranslator;
import de.uni_freiburg.informatik.ultimate.model.boogie.ast.Expression;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.Boogie2SMT;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.boogie.Term2Expression;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.predicates.IPredicate;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.BuchiCegarLoop.Result;
import de.uni_freiburg.informatik.ultimate.plugins.generator.buchiautomizer.preferences.PreferenceInitializer;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.RcfgProgramExecution;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.CodeBlock;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.ProgramPoint;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.RcfgElement;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.RootAnnot;
import de.uni_freiburg.informatik.ultimate.plugins.generator.rcfgbuilder.cfg.RootNode;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.ISLPredicate;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.predicates.SmtManager;
import de.uni_freiburg.informatik.ultimate.plugins.generator.traceabstraction.preferences.TAPreferences;
import de.uni_freiburg.informatik.ultimate.result.BenchmarkResult;
import de.uni_freiburg.informatik.ultimate.result.GenericResult;
import de.uni_freiburg.informatik.ultimate.result.GenericResultAtElement;
import de.uni_freiburg.informatik.ultimate.result.NonTerminationArgumentResult;
import de.uni_freiburg.informatik.ultimate.result.IProgramExecution.ProgramState;
import de.uni_freiburg.informatik.ultimate.result.IResult;
import de.uni_freiburg.informatik.ultimate.result.IResultWithSeverity.Severity;
import de.uni_freiburg.informatik.ultimate.result.NonterminatingLassoResult;
import de.uni_freiburg.informatik.ultimate.result.TerminationAnalysisResult;
import de.uni_freiburg.informatik.ultimate.result.TerminationAnalysisResult.TERMINATION;
import de.uni_freiburg.informatik.ultimate.result.TimeoutResultAtElement;

/**
 * Auto-Generated Stub for the plug-in's Observer
 */
public class BuchiAutomizerObserver implements IUnmanagedObserver {

	private IElement m_graphroot;
	private SmtManager smtManager;
	private TAPreferences m_Pref;

	private RootAnnot rootAnnot;
	private final IUltimateServiceProvider mServices;
	private final IToolchainStorage mStorage;

	public BuchiAutomizerObserver(IUltimateServiceProvider services, IToolchainStorage storage) {
		mServices = services;
		mStorage = storage;
	}

	@Override
	public boolean process(IElement root) {
		if (!(root instanceof RootNode)) {
			return false;
		}
		//TODO: Now you can get instances of your library classes for the current toolchain like this: 
		//NWA is nevertheless very broken, as its static initialization prevents parallelism 
		//Surprisingly, this call lazily initializes the static fields of NWA Lib and, like magic, the toolchain works ...
		mServices.getServiceInstance(ExampleNWAFactory.class);

		rootAnnot = ((RootNode) root).getRootAnnot();
		TAPreferences taPrefs = new TAPreferences();
		m_graphroot = root;

		smtManager = new SmtManager(rootAnnot.getBoogie2SMT(), rootAnnot.getModGlobVarManager(), mServices);

		m_Pref = taPrefs;

		BuchiCegarLoop bcl = new BuchiCegarLoop((RootNode) root, smtManager, m_Pref, mServices, mStorage);
		Result result = bcl.iterate();
		BuchiCegarLoopBenchmarkGenerator benchGen = bcl.getBenchmarkGenerator();
		benchGen.stop(BuchiCegarLoopBenchmark.s_OverallTime);

		// s_Logger.info(MessageFormat.format("Statistics: Counterexamples: {0} infeasible"
		// +
		// "  {1} rank without si  {2} rank only with si",
		// bcl.m_Infeasible, bcl.m_RankWithoutSi, bcl.m_RankWithSi));

		IResult benchDecomp = new BenchmarkResult<Double>(Activator.s_PLUGIN_ID,
				"Constructed decomposition of program", bcl.getMDBenchmark());
		reportResult(benchDecomp);
		// s_Logger.info("BenchmarkResult: " + benchDecomp.getShortDescription()
		// + ": " + benchDecomp.getLongDescription());
		boolean constructTermcompProof = (new UltimatePreferenceStore(Activator.s_PLUGIN_ID))
				.getBoolean(PreferenceInitializer.LABEL_TermcompProof);
		if (constructTermcompProof) {
			IResult termcompProof = new BenchmarkResult<Double>(Activator.s_PLUGIN_ID,
					"Constructed termination proof in form of nested word automata", bcl.getTermcompProofBenchmark());
			reportResult(termcompProof);
		}

		TimingBenchmark timingBenchmark = new TimingBenchmark(benchGen);
		IResult benchTiming = new BenchmarkResult(Activator.s_PLUGIN_ID, "Timing statistics", timingBenchmark);
		reportResult(benchTiming);
		// s_Logger.info("BenchmarkResult: " + benchTiming.getShortDescription()
		// + ": " + benchTiming.getLongDescription());

		if (result == Result.TERMINATING) {
//			String shortDescr = "Termination proven";
			String longDescr = "Buchi Automizer proved that your program is terminating";
			IResult reportRes = new TerminationAnalysisResult(Activator.s_PLUGIN_ID, TERMINATION.TERMINATING, longDescr); 
					//new GenericResult(Activator.s_PLUGIN_ID, shortDescr, longDescr, Severity.INFO);
			reportResult(reportRes);
			// s_Logger.info(shortDescr + ": " + longDescr);
		} else if (result == Result.UNKNOWN) {
			NestedLassoRun<CodeBlock, IPredicate> counterexample = bcl.getCounterexample();
			IPredicate hondaPredicate = counterexample.getLoop().getStateAtPosition(0);
			ProgramPoint honda = ((ISLPredicate) hondaPredicate).getProgramPoint();
			String shortDescr = "Unable to decide termination";
			StringBuilder longDescr = new StringBuilder();
			longDescr.append("Buchi Automizer is unable to decide termination for the following lasso. ");
			longDescr.append(System.getProperty("line.separator"));
			longDescr.append("Stem: ");
			longDescr.append(counterexample.getStem().getWord());
			longDescr.append(System.getProperty("line.separator"));
			longDescr.append("Loop: ");
			longDescr.append(counterexample.getLoop().getWord());
			IResult reportRes = new TerminationAnalysisResult(Activator.s_PLUGIN_ID, TERMINATION.UNKNOWN, longDescr.toString());
					//new GenericResultAtElement<RcfgElement>(honda, Activator.s_PLUGIN_ID, mServices
					//.getBacktranslationService().getTranslatorSequence(), shortDescr, longDescr.toString(),
					//Severity.ERROR);
			reportResult(reportRes);
			// s_Logger.info(shortDescr + ": " + longDescr);
		} else if (result == Result.TIMEOUT) {
			ProgramPoint position = rootAnnot.getEntryNodes().values().iterator().next();
			String longDescr = "Timeout while trying to prove termination";
			IResult reportRes = new TimeoutResultAtElement<RcfgElement>(position, Activator.s_PLUGIN_ID, mServices
					.getBacktranslationService().getTranslatorSequence(), longDescr);
			reportResult(reportRes);
		} else if (result == Result.NONTERMINATING) {
//			String shortDescr = "Nontermination possible";
			String longDescr = "Buchi Automizer proved that your program is nonterminating for some inputs";
			IResult reportRes =  new TerminationAnalysisResult(Activator.s_PLUGIN_ID, TERMINATION.NONTERMINATING, longDescr);
					// new GenericResult(Activator.s_PLUGIN_ID, shortDescr, longDescr, Severity.ERROR);
			reportResult(reportRes);
			// s_Logger.info(shortDescr + ": " + longDescr);
			NestedLassoRun<CodeBlock, IPredicate> counterexample = bcl.getCounterexample();
			IPredicate hondaPredicate = counterexample.getLoop().getStateAtPosition(0);
			ProgramPoint honda = ((ISLPredicate) hondaPredicate).getProgramPoint();
			NonTerminationArgument nta = bcl.getNonTerminationArgument();
			reportNonTerminationResult(nta, rootAnnot.getBoogie2SMT(), honda);

			Map<Integer, ProgramState<Expression>> partialProgramStateMapping = Collections.emptyMap();

			RcfgProgramExecution stemPE = new RcfgProgramExecution(counterexample.getStem().getWord().lettersAsList(),
					partialProgramStateMapping, new Map[counterexample.getStem().getLength()]);
			RcfgProgramExecution loopPE = new RcfgProgramExecution(counterexample.getLoop().getWord().lettersAsList(),
					partialProgramStateMapping, new Map[counterexample.getLoop().getLength()]);
			IResult ntreportRes = new NonterminatingLassoResult<RcfgElement>(honda, Activator.s_PLUGIN_ID, mServices
					.getBacktranslationService().getTranslatorSequence(), stemPE, loopPE, honda.getPayload()
					.getLocation());
			reportResult(ntreportRes);
			// s_Logger.info(ntreportRes.getShortDescription());
			// s_Logger.info(ntreportRes.getLongDescription());
		} else {
			throw new AssertionError();
		}
		return false;
	}

	/**
	 * Report a nontermination argument back to Ultimate's toolchain
	 * 
	 * @param arg
	 */
	private void reportNonTerminationResult(NonTerminationArgument arg, Boogie2SMT boogie2smt, RcfgElement honda) {
		// TODO: translate also the rational coefficients to Expressions?
		// m_RootAnnot.getBoogie2Smt().translate(term)
		Term2Expression term2expression = boogie2smt.getTerm2Expression();

		List<Map<Expression, Rational>> initHondaRay = NonTerminationArgument.rank2Boogie(term2expression,
				arg.getStateInit(), arg.getStateHonda(), arg.getRay());

		NonTerminationArgumentResult<RcfgElement> result = new NonTerminationArgumentResult<RcfgElement>(honda,
				Activator.s_PLUGIN_NAME, initHondaRay.get(0), initHondaRay.get(1), initHondaRay.get(2),
				arg.getLambda(), getTranslatorSequence());
		reportResult(result);
	}

	/**
	 * @return the current translator sequence for building results
	 */
	private List<ITranslator<?, ?, ?, ?>> getTranslatorSequence() {
		// getTranslatorSequence() is marked deprecated, but an alternative
		// has yet to arise
		List<ITranslator<?, ?, ?, ?>> translator_sequence = mServices.getBacktranslationService()
				.getTranslatorSequence();
		return translator_sequence;
	}

	void reportResult(IResult res) {
		mServices.getResultService().reportResult(Activator.s_PLUGIN_ID, res);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	@Override
	public WalkerOptions getWalkerOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean performedChanges() {
		// TODO Auto-generated method stub
		return false;
	}

	public IElement getModel() {
		return m_graphroot;
	}

	// public static TransFormula sequentialComposition(int serialNumber,
	// Boogie2SMT boogie2smt, TransFormula... transFormulas) {
	// TransFormula result = transFormulas[0];
	// for (int i=1; i<transFormulas.length; i++) {
	// result = TransFormula.sequentialComposition(result, transFormulas[i],
	// boogie2smt, serialNumber++);
	// }
	// return result;
	// }

}
