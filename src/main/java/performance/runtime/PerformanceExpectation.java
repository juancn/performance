package performance.runtime;

import performance.compiler.SimpleGrammar;
import performance.compiler.TokenType;
import performance.compiler.eval.BinaryOpVisitor;
import performance.parser.ParseException;
import performance.parser.ast.BinaryExpr;
import performance.parser.ast.ConstantExpr;
import performance.parser.ast.Expr;
import performance.parser.ast.ExprAdapter;
import performance.compiler.eval.MethodMatch;
import performance.compiler.eval.Op;

import java.util.List;

public class PerformanceExpectation
    implements MethodListener
{
    private final Class ctxClass;
    private final String methodName;
    private final String expression;

    private Op validation;
    private List<MethodMatch> methodMatches;

    public PerformanceExpectation(final Class ctxClass, final String methodName, final String expression)
            throws ParseException
    {
        this.ctxClass = ctxClass;
        this.methodName = methodName;
        this.expression = expression;

        final Expr<TokenType> expr = SimpleGrammar.parse(expression);
        final BinaryOpVisitor visitor = new BinaryOpVisitor();
        expr.visit(visitor);
        validation = visitor.getOpTree();
        methodMatches = visitor.getMethodMatches();
    }


    @Override
    public void methodEnter(Class clazz, String name) {
        for (MethodMatch methodMatch : methodMatches) {
            methodMatch.match(clazz, name);
        }
    }

    @Override
    public void methodNormalExit(Class clazz, String name) {
    }

    @Override
    public void methodExceptionExit(Class clazz, String name) {

    }

    public void validate() {
        if(!validation.booleanVal()) {
            throw new AssertionError("Method '" + ctxClass.getName() + "." + methodName + "' did not fulfil: " + expression + "\n" + methodMatches);
        }
    }


}
