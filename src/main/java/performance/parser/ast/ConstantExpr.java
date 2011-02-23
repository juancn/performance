package performance.parser.ast;

import performance.parser.Token;

public class ConstantExpr<T>
        extends Expr {
    private Token<T> token;
    public ConstantExpr(Token<T> token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return String.valueOf(token.getText());
    }
}
