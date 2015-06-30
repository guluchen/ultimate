package de.uni_freiburg.informatik.ultimate.plugins.analysis.abstractinterpretationv2.domain.sign;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a single integer expression in the {@link SignDomain}.
 * 
 * @author greitsch@informatik.uni-freiburg.de
 *
 */
public final class SignSingletonIntegerExpressionEvaluator extends SignSingletonValueExpressionEvaluator<BigInteger> {

	public SignSingletonIntegerExpressionEvaluator(String value) {
		super(value);
	}

	@Override
	protected final BigInteger instantiate(String value) {
		BigInteger number;
		try {
			number = new BigInteger(value);
		} catch (NumberFormatException e) {
			throw new UnsupportedOperationException("The value \"" + value + "\" cannot be transformed to an integer.");
		}
		return number;
	}

	@Override
    protected int getSignum() {
		return mValue.signum();
    }

	@Override
    public Set<String> getVarIdentifiers() {
		return new HashSet<String>();
    }

}
