package performance.compiler.eval;

import performance.compiler.TokenType;
import performance.parser.ast.BinaryExpr;
import performance.parser.ast.ConstantExpr;
import performance.parser.ast.Expr;
import performance.parser.ast.ExprAdapter;
import performance.parser.ast.UnaryExpr;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class BinaryOpVisitor
        extends ExprAdapter<TokenType> {

    private Deque<Op> stack = new ArrayDeque<Op>();
    private List<MethodMatch> methodMatches = new ArrayList<MethodMatch>();
    private List<DynamicValue> dynamicValues = new ArrayList<DynamicValue>();

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
            case PLUS:
            case MINUS:
            case MUL:
            case DIV:
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

    @Override
    public void visit(UnaryExpr<TokenType> expr) {
        switch (expr.getToken().getType()) {
            case MINUS:
            case NOT:
                expr.getFirst().visit(this);
                stack.add(new UnaryOp(expr.getToken().getType(), stack.removeLast()));
                break;
            case DOLLAR_LCURLY: //${....}
                final DynamicValue dynamicValue = new DynamicValue(valueOf(expr.getFirst()).components());
                dynamicValues.add(dynamicValue);
                stack.add(dynamicValue);
                break;
        }
    }

    @Override
    public void visit(ConstantExpr<TokenType> expr) {
        switch (expr.getToken().getType()) {
            case DOUBLE_LITERAL:
            case FLOAT_LITERAL:
            case LONG_LITERAL:
            case INT_LITERAL:
                stack.add(new NumberOp(Double.parseDouble(String.valueOf(expr.getToken().getText()))));
                break;
            case ID:
                methodMatch(null, String.valueOf(expr.getToken().getText()));
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

    public Op getOpTree() {
        return stack.peekLast();
    }

    public List<MethodMatch> getMethodMatches() {
        return methodMatches;
    }

    public List<DynamicValue> getDynamicValues() {
        return dynamicValues;
    }
}
