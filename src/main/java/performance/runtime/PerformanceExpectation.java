package performance.runtime;

import performance.compiler.SimpleGrammar;
import performance.compiler.TokenType;
import performance.parser.ParseException;
import performance.parser.ast.BinaryExpr;
import performance.parser.ast.ConstantExpr;
import performance.parser.ast.Expr;
import performance.parser.ast.ExprAdapter;
import performance.parser.ast.ExprVisitor;
import performance.parser.ast.UnaryExpr;

import static performance.compiler.TokenType.*;

public class PerformanceExpectation
    implements MethodListener
{
    private final String className;
    private final String methodName;
    private final String expression;

    public PerformanceExpectation(final String className, final String methodName, final String expression)
            throws ParseException
    {
        this.className = className;
        this.methodName = methodName;
        this.expression = expression;

        Expr<TokenType> expr = SimpleGrammar.parse(expression);
        System.out.println("parse = " + expr);
        expr.visit(new ExprAdapter<TokenType>() {
            @Override
            public void visit(BinaryExpr<TokenType> expr) {
                switch (expr.getToken().getType()) {
                    case LT:
                        expr.getLeft().visit(this);
                        expr.getRight().visit(this);
                        break;
                    case DOT:
                        break;
                }
            }
        });

    }

    @Override
    public void methodEnter(String clazz, String name) {
//        System.out.println("PerformanceExpectation.methodEnter");
    }

    @Override
    public void methodNormalExit(String clazz, String name) {
//        System.out.println("PerformanceExpectation.methodNormalExit");
    }

    @Override
    public void methodExceptionExit(String clazz, String name) {
//        System.out.println("PerformanceExpectation.methodExceptionExit");
    }
}
