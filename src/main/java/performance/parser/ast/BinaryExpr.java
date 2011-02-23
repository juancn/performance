package performance.parser.ast;

import performance.parser.Token;

public class BinaryExpr<T>
        extends Expr {
    private Token<T> token;
    private Expr left;
    private Expr right;


    public BinaryExpr(Token<T> token, Expr left, Expr right) {
        this.token = token;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "{ " + token.getText() + " " + left + " " + right + " }";
    }
}
