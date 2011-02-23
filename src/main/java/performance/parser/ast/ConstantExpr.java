package performance.parser.ast;

import performance.parser.Token;

public class ConstantExpr<T>
        extends Expr<T> {
    private Token<T> token;
    public ConstantExpr(Token<T> token) {
        this.token = token;
    }

    @Override
    public void visit(ExprVisitor<T> visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.valueOf(token.getText());
    }

    public Token<T> getToken() {
        return token;
    }
}
