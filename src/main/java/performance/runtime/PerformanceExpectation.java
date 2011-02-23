package performance.runtime;

import performance.compiler.SimpleGrammar;
import performance.compiler.TokenType;
import performance.parser.ParseException;
import performance.parser.ast.BinaryExpr;
import performance.parser.ast.ConstantExpr;
import performance.parser.ast.Expr;
import performance.parser.ast.ExprAdapter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class PerformanceExpectation
    implements MethodListener
{
    private final String className;
    private final String methodName;
    private final String expression;

    private Op validation;
    private List<MethodMatch> methodMatches;

    public PerformanceExpectation(final String className, final String methodName, final String expression)
            throws ParseException
    {
        this.className = className;
        this.methodName = methodName;
        this.expression = expression;

        final Expr<TokenType> expr = SimpleGrammar.parse(expression);
        final BinaryOpVisitor visitor = new BinaryOpVisitor();
        expr.visit(visitor);
        validation = visitor.getOpTree();
        methodMatches = visitor.getMethodMatches();
    }

    @Override
    public void methodEnter(String clazz, String name) {
        for (MethodMatch methodMatch : methodMatches) {
            methodMatch.match(clazz, name);
        }
    }

    @Override
    public void methodNormalExit(String clazz, String name) {
    }

    @Override
    public void methodExceptionExit(String clazz, String name) {

    }

    public void validate() {
        if(!validation.booleanVal()) {
            throw new AssertionError("Method '" + className + "." + methodName + "' did not fulfil: " + expression + "\n" + methodMatches);
        }
    }


    private static class BinaryOpVisitor
            extends ExprAdapter<TokenType> {

        private Deque<Op> stack = new ArrayDeque<Op>();
        private List<MethodMatch> methodMatches = new ArrayList<MethodMatch>();

        @Override
        public void visit(BinaryExpr<TokenType> expr) {
            final Expr<TokenType> left = expr.getLeft();
            final Expr<TokenType> right = expr.getRight();

            switch (expr.getToken().getType()) {
                case LT:
                case LE:
                case GT:
                case GE:
                case LOR:
                case LAND:
                    //Visit in reverse so we avoid some vars
                    right.visit(this);
                    left.visit(this);
                    stack.add(new BinOp(expr.getToken().getType(), stack.removeLast(), stack.removeLast()));
                    break;
                case DOT:
                    methodMatch(valueOf(left).toString(), valueOf(right).toString());
                    break;
            }
        }

        private ValueVisitor valueOf(Expr<TokenType> right) {
            final ValueVisitor mtdPattern = new ValueVisitor();
            right.visit(mtdPattern);
            return mtdPattern;
        }

        private void methodMatch(String classPattern1, String mtdPattern1) {
            MethodMatch methodMatch = new MethodMatch(classPattern1, mtdPattern1);
            stack.add(methodMatch);
            methodMatches.add(methodMatch);
        }

        @Override
        public void visit(ConstantExpr<TokenType> expr) {
            switch (expr.getToken().getType()) {
                case INT_LITERAL:
                    stack.add(new IntValue(Integer.parseInt(String.valueOf(expr.getToken().getText()))));
                    break;
                case ID:
                    methodMatch(null, String.valueOf(expr.getToken().getText()));
                    break;
            }
        }

        public Op getOpTree() {
            return stack.peekLast();
        }

        public List<MethodMatch> getMethodMatches() {
            return methodMatches;
        }
    }

    private static class ValueVisitor
            extends ExprAdapter<TokenType> {
        StringBuilder sb = new StringBuilder();

        @Override
        public void visit(BinaryExpr<TokenType> expr) {
            switch (expr.getToken().getType()) {
                case DOT:
                    expr.getLeft().visit(this);
                    sb.append('.');
                    expr.getRight().visit(this);
                    break;
            }
        }

        @Override
        public void visit(ConstantExpr<TokenType> expr) {
            sb.append(expr.getToken().getText());
        }

        @Override
        public String toString() {
            return sb.toString();
        }
    }

    static abstract class Op {
        boolean booleanVal() {
            throw new UnsupportedOperationException();
        }

        int intVal() {
            throw new UnsupportedOperationException();
        }
    }
    static class BinOp extends Op {
        TokenType operator;
        Op left;
        Op right;

        BinOp(TokenType operator, Op left, Op right) {
            this.operator = operator;
            this.left = left;
            this.right = right;
        }

        boolean booleanVal()
        {
            switch(operator) {
                case LT:
                    return left.intVal() < right.intVal();
                case LE:
                    return left.intVal() <= right.intVal();
                case GT:
                    return left.intVal() > right.intVal();
                case GE:
                    return left.intVal() >= right.intVal();
                case LOR:
                    return left.booleanVal() || right.booleanVal();
                case LAND:
                    return left.booleanVal() && right.booleanVal();
                default:
                    return super.booleanVal();
            }
        }
    }

    static class MethodMatch extends Op {
        String classPattern;
        String mtdPattern;

        int count;

        MethodMatch(String classPattern, String mtdPattern) {
            this.classPattern = classPattern;
            this.mtdPattern = mtdPattern;
        }

        @Override
        int intVal() {
            return count;
        }

        void match(String className, String mtdName)
        {
            if(classPattern == null && mtdName.equals(mtdPattern)) {
                ++count;
            }
        }

        @Override
        public String toString() {

            return "#" + (classPattern==null?"<any>":classPattern) + "." + mtdPattern + "=" + count;
        }
    }

    static class IntValue extends Op {
        int value;
        IntValue(int value) {
            this.value = value;
        }

        @Override
        int intVal() {
            return value;
        }
    }
}
