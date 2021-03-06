package de.uni_freiburg.informatik.ultimate.lib.treeautomizer;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.uni_freiburg.informatik.ultimate.automata.tree.IRankedLetter;
import de.uni_freiburg.informatik.ultimate.logic.Term;
import de.uni_freiburg.informatik.ultimate.logic.TermVariable;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.TermTransferrer;
import de.uni_freiburg.informatik.ultimate.modelcheckerutils.smt.managedscript.ManagedScript;

/**
 * This is our internal representation of a Horn clause. A Horn clause consists
 * of - a body with -- n uninterpreted predicate symbols (n >= 0) -- a
 * transition formula - a head with either -- an uninterpreted predicate symbol
 * or -- false - a mapping that assigns each of the argument positions of the
 * uninterpreted predicate a free variable in the transition formula
 *
 * Note that the uninterpreted predicate symbols may only have an arity and a
 * name. If in the formula there was a complex expression in one of the
 * arguments of the corresponding atom, this has to be encoded into the
 * transition formula.
 *
 * @author Alexander Nutz (nutz@informatik.uni-freiburg.de)
 *
 */
public class HornClause implements IRankedLetter {

	private final List<HornClausePredicateSymbol> mBodyPreds;

	private final List<List<TermVariable>> mBodyPredToTermVariables;

	/**
	 * Stores for the predicate symbol in the head at every argument position of
	 * the represented atom, which TermVariable in the transition formula
	 * represents that argument in the represented atom.
	 */
	private final List<TermVariable> mHeadPredTermVariables;
	private final HornClausePredicateSymbol mHeadPredicate;

	private final HCSymbolTable mHornClauseSymbolTable;

	private final Term mFormula;

	private final boolean mHeadIsFalse;

	/**
	 * Constructor for a Horn clause of the form b1 /\ ... /\ bn /\ constraint
	 * --> false. Where b1 .. bn are uninterpreted predicates and constraint is
	 * a Term.
	 *
	 * @param script
	 * @param symbolTable
	 * @param constraint
	 * @param bodyPreds
	 * @param bodyPredToTermVariables
	 */
	public HornClause(final ManagedScript script, final HCSymbolTable symbolTable, final Term constraint,
			final List<HornClausePredicateSymbol> bodyPreds, final List<List<TermVariable>> bodyPredToTermVariables) {
		this(script, symbolTable, constraint, null, Collections.emptyList(), bodyPreds, bodyPredToTermVariables, false);
	}

	public HornClause(final ManagedScript script, final HCSymbolTable symbolTable, final Term constraint,
			final HornClausePredicateSymbol headPred, final List<TermVariable> headVars,
			final List<HornClausePredicateSymbol> bodyPreds, final List<List<TermVariable>> bodyPredToTermVariables) {
		this(script, symbolTable, constraint, headPred, headVars, bodyPreds, bodyPredToTermVariables, false);
		assert headPred != null : "use other constructor for '... -> False' case";
	}

	/**
	 * Constructor for a Horn clause of the form b1 /\ ... /\ bn /\ constraint
	 * --> h. Where b1 .. bn, and h, are uninterpreted predicates and constraint
	 * is a Term.
	 *
	 * @param script
	 *            The script that will be used in TreeAutomizer (not the
	 *            HornClauseParserScript)
	 * @param symbolTable
	 * @param constraint
	 * @param headPred
	 * @param headVars
	 * @param bodyPreds
	 * @param bodyPredToTermVariables
	 * @param dummy
	 *            dummy parameter to allow for an extra constructor
	 */
	private HornClause(final ManagedScript script, final HCSymbolTable symbolTable, final Term constraint,
			final HornClausePredicateSymbol headPred, final List<TermVariable> headVars,
			final List<HornClausePredicateSymbol> bodyPreds, final List<List<TermVariable>> bodyPredToTermVariables,
			final boolean dummy) {

		mHornClauseSymbolTable = symbolTable;

		final TermTransferrer ttf = new TermTransferrer(script.getScript());

		/*
		 * send all the TermVariables through the TermTransferrer
		 */
		mHeadPredTermVariables = headVars.stream().map(var -> (TermVariable) ttf.transform(var))
				.collect(Collectors.toList());
		mBodyPredToTermVariables = bodyPredToTermVariables.stream()
				.map(list -> list.stream().map(var -> (TermVariable) ttf.transform(var)).collect(Collectors.toList()))
				.collect(Collectors.toList());

		// transfer the transition formula to the solver script
		mFormula = ttf.transform(constraint);

		for (final TermVariable fv : mFormula.getFreeVars()) {
			mHornClauseSymbolTable.registerTermVariable(fv);
		}
		mHeadPredTermVariables.forEach(mHornClauseSymbolTable::registerTermVariable);
		mBodyPredToTermVariables.forEach(tvs -> tvs.forEach(mHornClauseSymbolTable::registerTermVariable));

		mHeadIsFalse = headPred == null;
		mHeadPredicate = headPred;
		mBodyPreds = bodyPreds;
	}

	public HornClausePredicateSymbol getHeadPredicate() {
		if (mHeadIsFalse) {
			throw new AssertionError("Check for isHeadFalse() before calling this");
		}
		return mHeadPredicate;
	}

	public boolean isHeadFalse() {
		return mHeadIsFalse;
	}

	public List<HornClausePredicateSymbol> getBodyPredicates() {
		return mBodyPreds;
	}

	public int getNoBodyPredicates() {
		return mBodyPreds.size();
	}

	public TermVariable getPredArgTermVariable(final int predPos, final int argPos) {
		return mBodyPredToTermVariables.get(predPos).get(argPos);
	}

	public List<TermVariable> getTermVariablesForPredPos(final int predPos) {
		return mBodyPredToTermVariables.get(predPos);
	}

	public List<List<TermVariable>> getBodyPredToTermVariables() {
		return Collections.unmodifiableList(mBodyPredToTermVariables);
	}

	public List<TermVariable> getTermVariablesForHeadPred() {
		return mHeadPredTermVariables;
	}

	public String debugString() {

		String cobody = "";

		for (int i = 0; i < mBodyPredToTermVariables.size(); ++i) {
			cobody += " " + mBodyPreds.get(i) + "(";
			cobody += mBodyPredToTermVariables.get(i);
			cobody += ")";
		}
		if (cobody.length() > 0) {
			cobody = "and" + cobody;
		} else {
			cobody = "true";
		}

		final String body = mHeadPredicate != null ? mHeadPredicate.getName() : "false" + mHeadPredTermVariables;
		if (mFormula == null) {
			return "unintialized HornClause";
		}

		return String.format("(%s) ^^ (%s) ~~> (%s)", cobody, mFormula.toString(), body);
	}

	@Override
	public String toString() {
		// String cobody = "";
		//
		// for (final HornClausePredicateSymbol symbol :
		// mBodyPredToTermVariables.keySet()) {
		// cobody += " " + symbol.getName() +
		// mBodyPredToTermVariables.get(symbol);
		// }
		// if (cobody.length() > 0) {
		// cobody = "and" + cobody;
		// } else {
		// cobody = "true";
		// }
		//
		// final String body = mHeadPredicate.getName() +
		// mHeadPredTermVariables;
		// if (mTransitionFormula == null) {
		if (mFormula == null) {
			return "unintialized HornClause";
		}

		// return mTransitionFormula.getFormula().toString();
		return mFormula.toString();
		// return String.format("(%s) ^^ (%s) ~~> (%s) || in : %s || out : %s ",
		// cobody, mTransitionFormula, body,
		// return String.format("(%s) ^^ (%s) ~~> (%s)", cobody,
		// mTransitionFormula.getFormula(), body);
		// return "HornClause TODO: better description"; //TODO
	}

	public HCSymbolTable getHornClauseSymbolTable() {
		return mHornClauseSymbolTable;
	}

	@Override
	public int getRank() {
		// if (mBodyPreds.isEmpty())
		// {//mTransitionFormula.getInVars().isEmpty()) {
		// // Initial state
		// return 1;
		// }
		return mBodyPreds.size();// mTransitionFormula.getInVars().size();
	}

	public Term getFormula() {
		return mFormula;
	}

}
