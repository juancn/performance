package performance.parser.ast;

import performance.parser.Token;

public class UnaryExpr<T>
        extends Expr<T> {
    private Token<T> token;
    private Expr op;

    public UnaryExpr(Token<T> token, Expr op) {
        this.token = token;
        this.op = op;
    }

    @Override
    public void visit(ExprVisitor<T> visitor) {
        visitor.visit(this);
    }


    @Override
    public String toString() {
        return "{ " + token.getText() + " " + op + " }";
    }
}
