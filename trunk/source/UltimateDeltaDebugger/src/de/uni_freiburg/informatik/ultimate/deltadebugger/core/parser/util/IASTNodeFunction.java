package de.uni_freiburg.informatik.ultimate.deltadebugger.core.parser.util;

import org.eclipse.cdt.core.dom.ast.IASTASMDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTAlignmentSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTArrayDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.IASTArraySubscriptExpression;
import org.eclipse.cdt.core.dom.ast.IASTAttribute;
import org.eclipse.cdt.core.dom.ast.IASTAttributeOwner;
import org.eclipse.cdt.core.dom.ast.IASTAttributeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTBinaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTBinaryTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTBreakStatement;
import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTCastExpression;
import org.eclipse.cdt.core.dom.ast.IASTComment;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTConditionalExpression;
import org.eclipse.cdt.core.dom.ast.IASTContinueStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarationStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDefaultStatement;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier.IASTEnumerator;
import org.eclipse.cdt.core.dom.ast.IASTEqualsInitializer;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTExpressionList;
import org.eclipse.cdt.core.dom.ast.IASTExpressionStatement;
import org.eclipse.cdt.core.dom.ast.IASTFieldDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTImplicitDestructorName;
import org.eclipse.cdt.core.dom.ast.IASTImplicitDestructorNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTImplicitName;
import org.eclipse.cdt.core.dom.ast.IASTImplicitNameOwner;
import org.eclipse.cdt.core.dom.ast.IASTInitializer;
import org.eclipse.cdt.core.dom.ast.IASTInitializerList;
import org.eclipse.cdt.core.dom.ast.IASTLabelStatement;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNullStatement;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPointer;
import org.eclipse.cdt.core.dom.ast.IASTPointerOperator;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorElseStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorEndifStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorErrorStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorFunctionStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfdefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIfndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorIncludeStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroExpansion;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorObjectStyleMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorPragmaStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorUndefStatement;
import org.eclipse.cdt.core.dom.ast.IASTProblem;
import org.eclipse.cdt.core.dom.ast.IASTProblemDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTProblemExpression;
import org.eclipse.cdt.core.dom.ast.IASTProblemStatement;
import org.eclipse.cdt.core.dom.ast.IASTProblemTypeId;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTToken;
import org.eclipse.cdt.core.dom.ast.IASTTokenList;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IASTTypeId;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTTypeIdInitializerExpression;
import org.eclipse.cdt.core.dom.ast.IASTUnaryExpression;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.core.dom.ast.c.ICASTArrayDesignator;
import org.eclipse.cdt.core.dom.ast.c.ICASTArrayModifier;
import org.eclipse.cdt.core.dom.ast.c.ICASTDesignatedInitializer;
import org.eclipse.cdt.core.dom.ast.c.ICASTDesignator;
import org.eclipse.cdt.core.dom.ast.c.ICASTFieldDesignator;
import org.eclipse.cdt.core.dom.ast.c.ICASTPointer;
import org.eclipse.cdt.core.dom.ast.gnu.IGCCASTAttributeSpecifier;
import org.eclipse.cdt.core.dom.ast.gnu.IGNUASTCompoundStatementExpression;
import org.eclipse.cdt.core.dom.ast.gnu.IGNUASTGotoStatement;
import org.eclipse.cdt.core.dom.ast.gnu.c.ICASTKnRFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.gnu.c.IGCCASTArrayRangeDesignator;

/**
 * Simulates a double double dispatch function for an IASTNode argument.
 *
 * Extend this class and override the corresponding on() overloads to implement specialized functions based on the
 * runtime IASTNode type. Each on() overload defaults to the overload for it's direct super type, e.g.
 * on(IASTBinaryExpression) calls on(IASTExpression) calls on(IASTNode) unless overridden.
 *
 * This code is generated to only support interfaces relevant for C code, i.e. it does not support ICPPAST* interfaces.
 * Certain ICAST* interfaces have been removed as well, because they do not add new methods or cause problems because of
 * multiple inheritance.
 *
 * The main reason for using this class is that the instanceof-mess and/or visitor boilerplate is not part of the code
 * containing actual logic anymore.
 *
 * Note that there are multiple benefits over the original ASTVisitor:
 *
 * * The visitor only supports part of the type hierarchy, e.g. it only has overloads for IASTExpression but not for any
 * subtypes like IASTBinaryExpression.
 *
 * * In those cases where it does support a subtype, the default visit() implementation * will not be overriden if the
 * user only overrides the visit() of the supertype.
 *
 * * The visitor does not support certain types at all, i.e. preprocessor nodes
 *
 * However, it may come with a small performance penalty, if there are only few overridden overloads. Especially if the
 * JIT-compiler fails to remove redundant branches that all end inside on(IASTNode) (I have no idea if it does).
 *
 * The comment of each overload also serves as a quick reference to the expected properties a node may have in it's
 * parent (not duplicated for subtypes).
 *
 * * @param <T> returned type
 */
public interface IASTNodeFunction<T> {

	/**
	 * <pre>
	 * IASTAlignmentSpecifier.getPropertyInParent() values
	 *   {@link IASTDeclSpecifier#ALIGNMENT_SPECIFIER}
	 *
	 * </pre>
	 */
	default T on(final IASTAlignmentSpecifier alignmentSpecifier) {
		return on((IASTNode) alignmentSpecifier);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTDeclarator)}
	 *
	 */
	default T on(final IASTArrayDeclarator arrayDeclarator) {
		return on((IASTDeclarator) arrayDeclarator);
	}

	/**
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(ICASTArrayModifier)}
	 *
	 * IASTArrayModifier.getPropertyInParent() values
	 *   {@link IASTArrayDeclarator#ARRAY_MODIFIER}
	 *
	 * </pre>
	 */
	default T on(final IASTArrayModifier arrayModifier) {
		return on((IASTNode) arrayModifier);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IASTArraySubscriptExpression arraySubscriptExpression) {
		return on((IASTExpression) arraySubscriptExpression);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTDeclaration)}
	 *
	 */
	default T on(final IASTASMDeclaration asmDeclaration) {
		return on((IASTDeclaration) asmDeclaration);
	}

	/**
	 * <pre>
	 * IASTAttribute.getPropertyInParent() values
	 *   {@link IASTAttributeSpecifier#ATTRIBUTE}
	 *
	 * </pre>
	 */
	default T on(final IASTAttribute attribute) {
		return on((IASTNode) attribute);
	}

	/**
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IGCCASTAttributeSpecifier)}
	 *
	 * IASTAttributeSpecifier.getPropertyInParent() values
	 *   {@link IASTAttributeOwner#ATTRIBUTE_SPECIFIER}
	 *
	 * </pre>
	 */
	default T on(final IASTAttributeSpecifier attributeSpecifier) {
		return on((IASTNode) attributeSpecifier);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IASTBinaryExpression binaryExpression) {
		return on((IASTExpression) binaryExpression);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IASTBinaryTypeIdExpression binaryTypeIdExpression) {
		return on((IASTExpression) binaryTypeIdExpression);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTBreakStatement breakStatement) {
		return on((IASTStatement) breakStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTCaseStatement caseStatement) {
		return on((IASTStatement) caseStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IASTCastExpression castExpression) {
		return on((IASTExpression) castExpression);
	}

	/**
	 * <pre>
	 * IASTComment.getPropertyInParent() values
	 *   {@link IASTTranslationUnit#PREPROCESSOR_STATEMENT}
	 *
	 * </pre>
	 */
	default T on(final IASTComment comment) {
		return on((IASTNode) comment);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTDeclSpecifier)}
	 *
	 */
	default T on(final IASTCompositeTypeSpecifier compositeTypeSpecifier) {
		return on((IASTDeclSpecifier) compositeTypeSpecifier);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 * <pre>
	 * IASTCompoundStatement.getPropertyInParent() values
	 *   {@link IGNUASTCompoundStatementExpression#STATEMENT}
	 *
	 * </pre>
	 */
	default T on(final IASTCompoundStatement compoundStatement) {
		return on((IASTStatement) compoundStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IASTConditionalExpression conditionalExpression) {
		return on((IASTExpression) conditionalExpression);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTContinueStatement continueStatement) {
		return on((IASTStatement) continueStatement);
	}

	/**
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IASTASMDeclaration)}
	 *   {@link ASTNodeFunction#on(IASTFunctionDefinition)}
	 *   {@link ASTNodeFunction#on(IASTProblemDeclaration)}
	 *   {@link ASTNodeFunction#on(IASTSimpleDeclaration)}
	 *
	 * IASTDeclaration.getPropertyInParent() values
	 *   {@link IASTCompositeTypeSpecifier#MEMBER_DECLARATION}
	 *   {@link IASTDeclarationStatement#DECLARATION}
	 *   {@link IASTIfStatement#CONDITION}
	 *   {@link IASTTranslationUnit#OWNED_DECLARATION}
	 *   {@link ICASTKnRFunctionDeclarator#FUNCTION_PARAMETER}
	 *
	 * </pre>
	 */
	default T on(final IASTDeclaration declaration) {
		return on((IASTNode) declaration);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTDeclarationStatement declarationStatement) {
		return on((IASTStatement) declarationStatement);
	}

	/**
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IASTArrayDeclarator)}
	 *   {@link ASTNodeFunction#on(IASTFieldDeclarator)}
	 *   {@link ASTNodeFunction#on(IASTFunctionDeclarator)}
	 *
	 * IASTDeclarator.getPropertyInParent() values
	 *   {@link IASTDeclarator#NESTED_DECLARATOR}
	 *   {@link IASTParameterDeclaration#DECLARATOR}
	 *   {@link IASTSimpleDeclaration#DECLARATOR}
	 *   {@link IASTTypeId#ABSTRACT_DECLARATOR}
	 *
	 * </pre>
	 */
	default T on(final IASTDeclarator declarator) {
		return on((IASTNode) declarator);
	}

	/**
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IASTCompositeTypeSpecifier)}
	 *   {@link ASTNodeFunction#on(IASTElaboratedTypeSpecifier)}
	 *   {@link ASTNodeFunction#on(IASTEnumerationSpecifier)}
	 *   {@link ASTNodeFunction#on(IASTNamedTypeSpecifier)}
	 *   {@link ASTNodeFunction#on(IASTSimpleDeclSpecifier)}
	 *
	 * IASTDeclSpecifier.getPropertyInParent() values
	 *   {@link IASTFunctionDefinition#DECL_SPECIFIER}
	 *   {@link IASTParameterDeclaration#DECL_SPECIFIER}
	 *   {@link IASTSimpleDeclaration#DECL_SPECIFIER}
	 *   {@link IASTTypeId#DECL_SPECIFIER}
	 *
	 * </pre>
	 */
	default T on(final IASTDeclSpecifier declSpecifier) {
		return on((IASTNode) declSpecifier);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTDefaultStatement defaultStatement) {
		return on((IASTStatement) defaultStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTDoStatement doStatement) {
		return on((IASTStatement) doStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTDeclSpecifier)}
	 *
	 */
	default T on(final IASTElaboratedTypeSpecifier elaboratedTypeSpecifier) {
		return on((IASTDeclSpecifier) elaboratedTypeSpecifier);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTDeclSpecifier)}
	 *
	 */
	default T on(final IASTEnumerationSpecifier enumerationSpecifier) {
		return on((IASTDeclSpecifier) enumerationSpecifier);
	}

	/**
	 * <pre>
	 * IASTEnumerator.getPropertyInParent() values
	 *   {@link IASTEnumerationSpecifier#ENUMERATOR}
	 *
	 * </pre>
	 */
	default T on(final IASTEnumerator enumerator) {
		return on((IASTNode) enumerator);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTInitializer)}
	 *
	 */
	default T on(final IASTEqualsInitializer equalsInitializer) {
		return on((IASTInitializer) equalsInitializer);
	}

	/**
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IASTArraySubscriptExpression)}
	 *   {@link ASTNodeFunction#on(IASTBinaryExpression)}
	 *   {@link ASTNodeFunction#on(IASTBinaryTypeIdExpression)}
	 *   {@link ASTNodeFunction#on(IASTCastExpression)}
	 *   {@link ASTNodeFunction#on(IASTConditionalExpression)}
	 *   {@link ASTNodeFunction#on(IASTExpressionList)}
	 *   {@link ASTNodeFunction#on(IASTFieldReference)}
	 *   {@link ASTNodeFunction#on(IASTFunctionCallExpression)}
	 *   {@link ASTNodeFunction#on(IASTIdExpression)}
	 *   {@link ASTNodeFunction#on(IASTLiteralExpression)}
	 *   {@link ASTNodeFunction#on(IASTProblemExpression)}
	 *   {@link ASTNodeFunction#on(IASTTypeIdExpression)}
	 *   {@link ASTNodeFunction#on(IASTTypeIdInitializerExpression)}
	 *   {@link ASTNodeFunction#on(IASTUnaryExpression)}
	 *   {@link ASTNodeFunction#on(IGNUASTCompoundStatementExpression)}
	 *
	 * IASTExpression.getPropertyInParent() values
	 *   {@link IASTAlignmentSpecifier#ALIGNMENT_EXPRESSION}
	 *   {@link IASTArrayModifier#CONSTANT_EXPRESSION}
	 *   {@link IASTArraySubscriptExpression#ARRAY}
	 *   {@link IASTArraySubscriptExpression#SUBSCRIPT}
	 *   {@link IASTBinaryExpression#OPERAND_ONE}
	 *   {@link IASTBinaryExpression#OPERAND_TWO}
	 *   {@link IASTCaseStatement#EXPRESSION}
	 *   {@link IASTCastExpression#OPERAND}
	 *   {@link IASTConditionalExpression#LOGICAL_CONDITION}
	 *   {@link IASTConditionalExpression#NEGATIVE_RESULT}
	 *   {@link IASTConditionalExpression#POSITIVE_RESULT}
	 *   {@link IASTDoStatement#CONDITION}
	 *   {@link IASTEnumerator#ENUMERATOR_VALUE}
	 *   {@link IASTEqualsInitializer#INITIALIZER}
	 *   {@link IASTExpressionList#NESTED_EXPRESSION}
	 *   {@link IASTExpressionStatement#EXPRESSION}
	 *   {@link IASTFieldDeclarator#FIELD_SIZE}
	 *   {@link IASTFieldReference#FIELD_OWNER}
	 *   {@link IASTForStatement#CONDITION}
	 *   {@link IASTForStatement#ITERATION}
	 *   {@link IASTFunctionCallExpression#ARGUMENT}
	 *   {@link IASTFunctionCallExpression#FUNCTION_NAME}
	 *   {@link IASTIfStatement#CONDITION}
	 *   {@link IASTInitializerList#NESTED_INITIALIZER}
	 *   {@link IASTReturnStatement#RETURNVALUE}
	 *   {@link IASTSimpleDeclSpecifier#DECLTYPE_EXPRESSION}
	 *   {@link IASTSwitchStatement#CONTROLLER_EXP}
	 *   {@link IASTUnaryExpression#OPERAND}
	 *   {@link IASTWhileStatement#CONDITIONEXPRESSION}
	 *   {@link ICASTArrayDesignator#SUBSCRIPT_EXPRESSION}
	 *   {@link ICASTDesignatedInitializer#OPERAND}
	 *   {@link IGCCASTArrayRangeDesignator#SUBSCRIPT_CEILING_EXPRESSION}
	 *   {@link IGCCASTArrayRangeDesignator#SUBSCRIPT_FLOOR_EXPRESSION}
	 *   {@link IGNUASTGotoStatement#LABEL_NAME}
	 *
	 * </pre>
	 */
	default T on(final IASTExpression expression) {
		return on((IASTNode) expression);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IASTExpressionList expressionList) {
		return on((IASTExpression) expressionList);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTExpressionStatement expressionStatement) {
		return on((IASTStatement) expressionStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTDeclarator)}
	 *
	 */
	default T on(final IASTFieldDeclarator fieldDeclarator) {
		return on((IASTDeclarator) fieldDeclarator);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IASTFieldReference fieldReference) {
		return on((IASTExpression) fieldReference);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTForStatement forStatement) {
		return on((IASTStatement) forStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IASTFunctionCallExpression functionCallExpression) {
		return on((IASTExpression) functionCallExpression);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTDeclarator)}
	 *
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IASTStandardFunctionDeclarator)}
	 *   {@link ASTNodeFunction#on(ICASTKnRFunctionDeclarator)}
	 *
	 * IASTFunctionDeclarator.getPropertyInParent() values
	 *   {@link IASTFunctionDefinition#DECLARATOR}
	 *
	 * </pre>
	 */
	default T on(final IASTFunctionDeclarator functionDeclarator) {
		return on((IASTDeclarator) functionDeclarator);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTDeclaration)}
	 *
	 */
	default T on(final IASTFunctionDefinition functionDefinition) {
		return on((IASTDeclaration) functionDefinition);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTGotoStatement gotoStatement) {
		return on((IASTStatement) gotoStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IASTIdExpression idExpression) {
		return on((IASTExpression) idExpression);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTIfStatement ifStatement) {
		return on((IASTStatement) ifStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTImplicitName)}
	 *
	 * <pre>
	 * IASTImplicitDestructorName.getPropertyInParent() values
	 *   {@link IASTImplicitDestructorNameOwner#IMPLICIT_DESTRUCTOR_NAME}
	 *
	 * </pre>
	 */
	default T on(final IASTImplicitDestructorName implicitDestructorName) {
		return on((IASTImplicitName) implicitDestructorName);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTName)}
	 *
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IASTImplicitDestructorName)}
	 *
	 * IASTImplicitName.getPropertyInParent() values
	 *   {@link IASTImplicitNameOwner#IMPLICIT_NAME}
	 *
	 * </pre>
	 */
	default T on(final IASTImplicitName implicitName) {
		return on((IASTName) implicitName);
	}

	/**
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IASTEqualsInitializer)}
	 *   {@link ASTNodeFunction#on(IASTInitializerList)}
	 *   {@link ASTNodeFunction#on(ICASTDesignatedInitializer)}
	 *
	 * IASTInitializer.getPropertyInParent() values
	 *   {@link IASTDeclarator#INITIALIZER}
	 *   {@link IASTTypeIdInitializerExpression#INITIALIZER}
	 *
	 * </pre>
	 */
	default T on(final IASTInitializer initializer) {
		return on((IASTNode) initializer);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTInitializer)}
	 *
	 * <pre>
	 * IASTInitializerList.getPropertyInParent() values
	 *   {@link IASTEqualsInitializer#INITIALIZER}
	 *   {@link IASTFunctionCallExpression#ARGUMENT}
	 *   {@link IASTInitializerList#NESTED_INITIALIZER}
	 *   {@link IASTReturnStatement#RETURNVALUE}
	 *   {@link ICASTDesignatedInitializer#OPERAND}
	 *
	 * </pre>
	 */
	default T on(final IASTInitializerList initializerList) {
		return on((IASTInitializer) initializerList);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTLabelStatement labelStatement) {
		return on((IASTStatement) labelStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IASTLiteralExpression literalExpression) {
		return on((IASTExpression) literalExpression);
	}

	/**
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IASTImplicitName)}
	 *
	 * IASTName.getPropertyInParent() values
	 *   {@link IASTCompositeTypeSpecifier#TYPE_NAME}
	 *   {@link IASTDeclarator#DECLARATOR_NAME}
	 *   {@link IASTElaboratedTypeSpecifier#TYPE_NAME}
	 *   {@link IASTEnumerationSpecifier#ENUMERATION_NAME}
	 *   {@link IASTEnumerator#ENUMERATOR_NAME}
	 *   {@link IASTFieldReference#FIELD_NAME}
	 *   {@link IASTGotoStatement#NAME}
	 *   {@link IASTIdExpression#ID_NAME}
	 *   {@link IASTLabelStatement#NAME}
	 *   {@link IASTNamedTypeSpecifier#NAME}
	 *   {@link IASTPreprocessorIncludeStatement#INCLUDE_NAME}
	 *   {@link IASTPreprocessorMacroDefinition#MACRO_NAME}
	 *   {@link IASTPreprocessorMacroExpansion#EXPANSION_NAME}
	 *   {@link IASTPreprocessorMacroExpansion#NESTED_EXPANSION_NAME}
	 *   {@link IASTPreprocessorStatement#MACRO_NAME}
	 *   {@link ICASTFieldDesignator#FIELD_NAME}
	 *   {@link ICASTKnRFunctionDeclarator#PARAMETER_NAME}
	 *
	 * </pre>
	 */
	default T on(final IASTName name) {
		return on((IASTNode) name);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTDeclSpecifier)}
	 *
	 */
	default T on(final IASTNamedTypeSpecifier namedTypeSpecifier) {
		return on((IASTDeclSpecifier) namedTypeSpecifier);
	}

	T on(IASTNode node);

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTNullStatement nullStatement) {
		return on((IASTStatement) nullStatement);
	}

	/**
	 * <pre>
	 * IASTParameterDeclaration.getPropertyInParent() values
	 *   {@link IASTStandardFunctionDeclarator#FUNCTION_PARAMETER}
	 *
	 * </pre>
	 */
	default T on(final IASTParameterDeclaration parameterDeclaration) {
		return on((IASTNode) parameterDeclaration);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPointerOperator)}
	 *
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(ICASTPointer)}
	 *
	 * </pre>
	 */
	default T on(final IASTPointer pointer) {
		return on((IASTPointerOperator) pointer);
	}

	/**
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IASTPointer)}
	 *
	 * IASTPointerOperator.getPropertyInParent() values
	 *   {@link IASTDeclarator#POINTER_OPERATOR}
	 *
	 * </pre>
	 */
	default T on(final IASTPointerOperator pointerOperator) {
		return on((IASTNode) pointerOperator);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPreprocessorStatement)}
	 *
	 */
	default T on(final IASTPreprocessorElifStatement preprocessorElifStatement) {
		return on((IASTPreprocessorStatement) preprocessorElifStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPreprocessorStatement)}
	 *
	 */
	default T on(final IASTPreprocessorElseStatement preprocessorElseStatement) {
		return on((IASTPreprocessorStatement) preprocessorElseStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPreprocessorStatement)}
	 *
	 */
	default T on(final IASTPreprocessorEndifStatement preprocessorEndifStatement) {
		return on((IASTPreprocessorStatement) preprocessorEndifStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPreprocessorStatement)}
	 *
	 */
	default T on(final IASTPreprocessorErrorStatement preprocessorErrorStatement) {
		return on((IASTPreprocessorStatement) preprocessorErrorStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPreprocessorMacroDefinition)}
	 *
	 */
	default T on(final IASTPreprocessorFunctionStyleMacroDefinition preprocessorFunctionStyleMacroDefinition) {
		return on((IASTPreprocessorMacroDefinition) preprocessorFunctionStyleMacroDefinition);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPreprocessorStatement)}
	 *
	 */
	default T on(final IASTPreprocessorIfdefStatement preprocessorIfdefStatement) {
		return on((IASTPreprocessorStatement) preprocessorIfdefStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPreprocessorStatement)}
	 *
	 */
	default T on(final IASTPreprocessorIfndefStatement preprocessorIfndefStatement) {
		return on((IASTPreprocessorStatement) preprocessorIfndefStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPreprocessorStatement)}
	 *
	 */
	default T on(final IASTPreprocessorIfStatement preprocessorIfStatement) {
		return on((IASTPreprocessorStatement) preprocessorIfStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPreprocessorStatement)}
	 *
	 */
	default T on(final IASTPreprocessorIncludeStatement preprocessorIncludeStatement) {
		return on((IASTPreprocessorStatement) preprocessorIncludeStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPreprocessorStatement)}
	 *
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IASTPreprocessorFunctionStyleMacroDefinition)}
	 *   {@link ASTNodeFunction#on(IASTPreprocessorObjectStyleMacroDefinition)}
	 *
	 * </pre>
	 */
	default T on(final IASTPreprocessorMacroDefinition preprocessorMacroDefinition) {
		return on((IASTPreprocessorStatement) preprocessorMacroDefinition);
	}

	/**
	 * <pre>
	 * IASTPreprocessorMacroExpansion.getPropertyInParent() values
	 *   {@link IASTTranslationUnit#MACRO_EXPANSION}
	 *
	 * </pre>
	 */
	default T on(final IASTPreprocessorMacroExpansion preprocessorMacroExpansion) {
		return on((IASTNode) preprocessorMacroExpansion);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPreprocessorMacroDefinition)}
	 *
	 */
	default T on(final IASTPreprocessorObjectStyleMacroDefinition preprocessorObjectStyleMacroDefinition) {
		return on((IASTPreprocessorMacroDefinition) preprocessorObjectStyleMacroDefinition);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPreprocessorStatement)}
	 *
	 */
	default T on(final IASTPreprocessorPragmaStatement preprocessorPragmaStatement) {
		return on((IASTPreprocessorStatement) preprocessorPragmaStatement);
	}

	/**
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IASTPreprocessorElifStatement)}
	 *   {@link ASTNodeFunction#on(IASTPreprocessorElseStatement)}
	 *   {@link ASTNodeFunction#on(IASTPreprocessorEndifStatement)}
	 *   {@link ASTNodeFunction#on(IASTPreprocessorErrorStatement)}
	 *   {@link ASTNodeFunction#on(IASTPreprocessorIfStatement)}
	 *   {@link ASTNodeFunction#on(IASTPreprocessorIfdefStatement)}
	 *   {@link ASTNodeFunction#on(IASTPreprocessorIfndefStatement)}
	 *   {@link ASTNodeFunction#on(IASTPreprocessorIncludeStatement)}
	 *   {@link ASTNodeFunction#on(IASTPreprocessorMacroDefinition)}
	 *   {@link ASTNodeFunction#on(IASTPreprocessorPragmaStatement)}
	 *   {@link ASTNodeFunction#on(IASTPreprocessorUndefStatement)}
	 *
	 * IASTPreprocessorStatement.getPropertyInParent() values
	 *   {@link IASTTranslationUnit#PREPROCESSOR_STATEMENT}
	 *
	 * </pre>
	 */
	default T on(final IASTPreprocessorStatement preprocessorStatement) {
		return on((IASTNode) preprocessorStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPreprocessorStatement)}
	 *
	 */
	default T on(final IASTPreprocessorUndefStatement preprocessorUndefStatement) {
		return on((IASTPreprocessorStatement) preprocessorUndefStatement);
	}

	/**
	 * <pre>
	 * IASTProblem.getPropertyInParent() values
	 *   {@link IASTTranslationUnit#SCANNER_PROBLEM}
	 *
	 * </pre>
	 */
	default T on(final IASTProblem problem) {
		return on((IASTNode) problem);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTDeclaration)}
	 *
	 */
	default T on(final IASTProblemDeclaration problemDeclaration) {
		return on((IASTDeclaration) problemDeclaration);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IASTProblemExpression problemExpression) {
		return on((IASTExpression) problemExpression);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTProblemStatement problemStatement) {
		return on((IASTStatement) problemStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTTypeId)}
	 *
	 */
	default T on(final IASTProblemTypeId problemTypeId) {
		return on((IASTTypeId) problemTypeId);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTReturnStatement returnStatement) {
		return on((IASTStatement) returnStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTDeclaration)}
	 *
	 */
	default T on(final IASTSimpleDeclaration simpleDeclaration) {
		return on((IASTDeclaration) simpleDeclaration);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTDeclSpecifier)}
	 *
	 */
	default T on(final IASTSimpleDeclSpecifier simpleDeclSpecifier) {
		return on((IASTDeclSpecifier) simpleDeclSpecifier);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTFunctionDeclarator)}
	 *
	 */
	default T on(final IASTStandardFunctionDeclarator standardFunctionDeclarator) {
		return on((IASTFunctionDeclarator) standardFunctionDeclarator);
	}

	/**
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IASTBreakStatement)}
	 *   {@link ASTNodeFunction#on(IASTCaseStatement)}
	 *   {@link ASTNodeFunction#on(IASTCompoundStatement)}
	 *   {@link ASTNodeFunction#on(IASTContinueStatement)}
	 *   {@link ASTNodeFunction#on(IASTDeclarationStatement)}
	 *   {@link ASTNodeFunction#on(IASTDefaultStatement)}
	 *   {@link ASTNodeFunction#on(IASTDoStatement)}
	 *   {@link ASTNodeFunction#on(IASTExpressionStatement)}
	 *   {@link ASTNodeFunction#on(IASTForStatement)}
	 *   {@link ASTNodeFunction#on(IASTGotoStatement)}
	 *   {@link ASTNodeFunction#on(IASTIfStatement)}
	 *   {@link ASTNodeFunction#on(IASTLabelStatement)}
	 *   {@link ASTNodeFunction#on(IASTNullStatement)}
	 *   {@link ASTNodeFunction#on(IASTProblemStatement)}
	 *   {@link ASTNodeFunction#on(IASTReturnStatement)}
	 *   {@link ASTNodeFunction#on(IASTSwitchStatement)}
	 *   {@link ASTNodeFunction#on(IASTWhileStatement)}
	 *   {@link ASTNodeFunction#on(IGNUASTGotoStatement)}
	 *
	 * IASTStatement.getPropertyInParent() values
	 *   {@link IASTCompoundStatement#NESTED_STATEMENT}
	 *   {@link IASTDoStatement#BODY}
	 *   {@link IASTForStatement#BODY}
	 *   {@link IASTForStatement#INITIALIZER}
	 *   {@link IASTFunctionDefinition#FUNCTION_BODY}
	 *   {@link IASTIfStatement#ELSE}
	 *   {@link IASTIfStatement#THEN}
	 *   {@link IASTLabelStatement#NESTED_STATEMENT}
	 *   {@link IASTSwitchStatement#BODY}
	 *   {@link IASTWhileStatement#BODY}
	 *
	 * </pre>
	 */
	default T on(final IASTStatement statement) {
		return on((IASTNode) statement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTSwitchStatement switchStatement) {
		return on((IASTStatement) switchStatement);
	}

	/**
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IASTTokenList)}
	 *
	 * IASTToken.getPropertyInParent() values
	 *   {@link IASTAttribute#ARGUMENT_CLAUSE}
	 *   {@link IASTTokenList#NESTED_TOKEN}
	 *
	 * </pre>
	 */
	default T on(final IASTToken token) {
		return on((IASTNode) token);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTToken)}
	 *
	 */
	default T on(final IASTTokenList tokenList) {
		return on((IASTToken) tokenList);
	}

	default T on(final IASTTranslationUnit translationUnit) {
		return on((IASTNode) translationUnit);
	}

	/**
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(IASTProblemTypeId)}
	 *
	 * IASTTypeId.getPropertyInParent() values
	 *   {@link IASTAlignmentSpecifier#ALIGNMENT_TYPEID}
	 *   {@link IASTBinaryTypeIdExpression#OPERAND1}
	 *   {@link IASTBinaryTypeIdExpression#OPERAND2}
	 *   {@link IASTCastExpression#TYPE_ID}
	 *   {@link IASTTypeIdExpression#TYPE_ID}
	 *   {@link IASTTypeIdInitializerExpression#TYPE_ID}
	 *
	 * </pre>
	 */
	default T on(final IASTTypeId typeId) {
		return on((IASTNode) typeId);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IASTTypeIdExpression typeIdExpression) {
		return on((IASTExpression) typeIdExpression);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IASTTypeIdInitializerExpression typeIdInitializerExpression) {
		return on((IASTExpression) typeIdInitializerExpression);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IASTUnaryExpression unaryExpression) {
		return on((IASTExpression) unaryExpression);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IASTWhileStatement whileStatement) {
		return on((IASTStatement) whileStatement);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(ICASTDesignator)}
	 *
	 */
	default T on(final ICASTArrayDesignator cArrayDesignator) {
		return on((ICASTDesignator) cArrayDesignator);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTArrayModifier)}
	 *
	 */
	default T on(final ICASTArrayModifier cArrayModifier) {
		return on((IASTArrayModifier) cArrayModifier);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTInitializer)}
	 *
	 * <pre>
	 * ICASTDesignatedInitializer.getPropertyInParent() values
	 *   {@link IASTEqualsInitializer#INITIALIZER}
	 *   {@link IASTFunctionCallExpression#ARGUMENT}
	 *   {@link IASTInitializerList#NESTED_INITIALIZER}
	 *   {@link IASTReturnStatement#RETURNVALUE}
	 *   {@link ICASTDesignatedInitializer#OPERAND}
	 *
	 * </pre>
	 */
	default T on(final ICASTDesignatedInitializer cDesignatedInitializer) {
		return on((IASTInitializer) cDesignatedInitializer);
	}

	/**
	 * <pre>
	 * Overridden by
	 *   {@link ASTNodeFunction#on(ICASTArrayDesignator)}
	 *   {@link ASTNodeFunction#on(ICASTFieldDesignator)}
	 *   {@link ASTNodeFunction#on(IGCCASTArrayRangeDesignator)}
	 *
	 * ICASTDesignator.getPropertyInParent() values
	 *   {@link ICASTDesignatedInitializer#DESIGNATOR}
	 *
	 * </pre>
	 */
	default T on(final ICASTDesignator cDesignator) {
		return on((IASTNode) cDesignator);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(ICASTDesignator)}
	 *
	 */
	default T on(final ICASTFieldDesignator cFieldDesignator) {
		return on((ICASTDesignator) cFieldDesignator);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTFunctionDeclarator)}
	 *
	 */
	default T on(final ICASTKnRFunctionDeclarator cKnRFunctionDeclarator) {
		return on((IASTFunctionDeclarator) cKnRFunctionDeclarator);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTPointer)}
	 *
	 */
	default T on(final ICASTPointer cPointer) {
		return on((IASTPointer) cPointer);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(ICASTDesignator)}
	 *
	 */
	default T on(final IGCCASTArrayRangeDesignator gccArrayRangeDesignator) {
		return on((ICASTDesignator) gccArrayRangeDesignator);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTAttributeSpecifier)}
	 *
	 */
	default T on(final IGCCASTAttributeSpecifier gccAttributeSpecifier) {
		return on((IASTAttributeSpecifier) gccAttributeSpecifier);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTExpression)}
	 *
	 */
	default T on(final IGNUASTCompoundStatementExpression gnuCompoundStatementExpression) {
		return on((IASTExpression) gnuCompoundStatementExpression);
	}

	/**
	 * Overrides {@link ASTNodeFunction#on(IASTStatement)}
	 *
	 */
	default T on(final IGNUASTGotoStatement gnuGotoStatement) {
		return on((IASTStatement) gnuGotoStatement);
	}

}
