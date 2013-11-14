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

import java.math.BigDecimal;
import java.util.ArrayDeque;

/**
 * A term representing constants.  The Java-type of the constant depends on the
 * SMTLIB-type and the origin of the term.  If this term has numeral sort and
 * stems from model evaluation, the Java-type of the constant will be
 * {@link Rational}.  If it comes from user input it typically is BigInteger for
 * numerals or BigDecimal for decimals (note that you can also use Rational with
 * the API, but the parser won't do that).
 * 
 * A constant term is created by the 
 * {@link Script#numeral(java.math.BigInteger)},
 * {@link Script#decimal(BigDecimal)},
 * {@link Script#binary(String)},
 * {@link Script#hexadecimal(String)}, and
 * {@link Script#string(String)}.
 * 
 * Also {@link Rational#toTerm(Sort)} creates a constant term.
 * 
 * @author hoenicke, Juergen Christ
 */
public class ConstantTerm extends Term {
	/*
	 * The value of this term.  For numeral terms this is a BigInteger,
	 * for decimal terms a BigDecimal and for string terms this is a
	 * QuotedObject.  For terms returned by our model, we use Rational for all
	 * numeric sorts.
	 */
	private Object m_Value;
	private Sort m_Sort;
	
	ConstantTerm(Object value, Sort sort, int hash) {
		super(hash);
		this.m_Value = value;
		this.m_Sort = sort;
	}
	
	/**
	 * Gets the constant value.
	 * If this term has numeral sort and stems from model evaluation, 
	 * the Java-type of the constant will be {@link Rational}.  If it comes 
	 * from user input it typically is BigInteger for numerals or 
	 * BigDecimal for decimals (note that you can also use Rational with
	 * the API, but the parser won't do that). For string literals this
	 * is a {@link QuotedObject} containing a string.  Bit vector constants
	 * are represented by BigInteger.
	 * @return the value.
	 */
	public Object getValue() {
		return m_Value;
	}

	public Sort getSort() {
		return m_Sort;
	}
	
	public String toString() {
		if (m_Value instanceof BigDecimal) {
			BigDecimal decimal = (BigDecimal)m_Value; 
			String str = decimal.toPlainString();
			return str;
		}
		if (m_Value instanceof Rational)
			return getTheory().rational((Rational) m_Value, getSort()).
				toStringDirect();
		return m_Value.toString();
	}
	
	public String toStringDirect() {
		return toString();
	}

	public static final int hashConstant(Object value, Sort sort) {
		return value.hashCode() ^ sort.hashCode();
	}

	@Override
	public void toStringHelper(ArrayDeque<Object> m_Todo) {
		m_Todo.add(toString());
	}
}
