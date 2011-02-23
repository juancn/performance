package performance.parser.ast;

import performance.parser.Token;

public class BinaryExpr<T>
        extends Expr<T> {
    private Expr<T> left;
    private Expr<T> right;

    public BinaryExpr(Token<T> token, Expr<T> left, Expr<T> right) {
        super(token);
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return "{ " + token.getText() + " " + left + " " + right + " }";
    }

    @Override
    public void visit(ExprVisitor<T> visitor) {
        visitor.visit(this);
    }


    public Expr<T> getLeft() {
        return left;
    }

    public Expr<T> getRight() {
        return right;
    }
}
