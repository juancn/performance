package performance.runtime;

import performance.compiler.SimpleGrammar;
import performance.compiler.TokenType;
import static performance.compiler.TokenType.*;
import performance.parser.ParseException;
import performance.parser.Token;
import performance.parser.ast.BinaryExpr;
import performance.parser.ast.ConstantExpr;
import performance.parser.ast.Expr;
import performance.parser.ast.ExprAdapter;

public class PerformanceExpectation
    implements MethodListener
{
    private final String className;
    private final String methodName;
    private final String expression;

    //TODO: hack
    private String checkFor;
    private int upperBound;
    private int count;

    public PerformanceExpectation(final String className, final String methodName, final String expression)
            throws ParseException
    {
        this.className = className;
        this.methodName = methodName;
        this.expression = expression;

        Expr<TokenType> expr = SimpleGrammar.parse(expression);
        System.out.println("parse = " + expr);
        expr.visit(new ExprAdapter<TokenType>() {
            public ConstantExpr<TokenType> lastConstant;

            @Override
            public void visit(BinaryExpr<TokenType> expr) {
                switch (expr.getToken().getType()) {
                    case LT:
                        //TODO: Puajjj!
                        expr.getLeft().visit(this);
                        if (lastConstant != null) {
                            Token<TokenType> left = lastConstant.getToken();
                            lastConstant = null;

                            expr.getRight().visit(this);
                            if (lastConstant != null) {
                                Token<TokenType> right = lastConstant.getToken();
                                if(left.getType() == ID && right.getType() == INT_LITERAL) {
                                    checkFor = String.valueOf(left.getText());
                                    upperBound = Integer.parseInt(String.valueOf(right.getText()));
                                }
                            }
                        }
                        break;
                }
            }

            @Override
            public void visit(ConstantExpr<TokenType> expr) {
                lastConstant = expr;
            }
        });

        System.out.println("checkFor = " + checkFor);
        System.out.println("upperBound = " + upperBound);

    }

    @Override
    public void methodEnter(String clazz, String name) {
        if(name.equals(checkFor)) {
            ++count;
        }
    }

    @Override
    public void methodNormalExit(String clazz, String name) {
    }

    @Override
    public void methodExceptionExit(String clazz, String name) {

    }

    public void validate() {
        if(checkFor != null && count >= upperBound) {
            throw new AssertionError("Method '" + checkFor + "' was called " + count +" times. Maximum was: " + upperBound);
        }
    }
}
