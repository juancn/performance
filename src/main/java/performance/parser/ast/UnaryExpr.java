package performance.parser.ast;

import performance.parser.Token;

public class UnaryExpr<T>
        extends Expr<T> {
    private Expr<T> first;

    public UnaryExpr(Token<T> token, Expr<T> first) {
        super(token);
        this.first = first;
    }

    public Expr<T> getFirst() {
        return first;
    }

    @Override
    public void visit(ExprVisitor<T> visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "{ " + token.getText() + " " + first + " }";
    }
}
