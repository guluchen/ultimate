package de.uni_freiburg.informatik.ultimate.plugins.source.automatascriptparser.AST;


import de.uni_freiburg.informatik.ultimate.model.location.ILocation;
import de.uni_freiburg.informatik.ultimate.plugins.source.automatascriptparser.AtsASTNode;

/**
 * @author musab@informatik.uni-freiburg.de
 */
public class ConstantExpressionAST extends AtsASTNode {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9065975410268575852L;
	private Object value;
	
	public ConstantExpressionAST(ILocation loc, Integer val) {
		super(loc);
		setType(Integer.class);
		value = val;
	}
	
	public ConstantExpressionAST(ILocation loc, String val) {
		super(loc);
		setType(String.class);
		this.value = val;
	}
	
	public ConstantExpressionAST(ILocation loc, boolean val) {
		super(loc);
		setType(Boolean.class);
		value = val;
	}
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "ConstantExpression [Value : " + value + "]";
	}

	@Override
	public String getAsString() {
		if (value instanceof String) {
			return "\"" + value.toString() + "\"";
		}
		return value.toString();
	}
	
	

}
