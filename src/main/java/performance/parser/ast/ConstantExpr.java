package performance.parser.ast;

import performance.parser.Token;

public class ConstantExpr<T>
        extends Expr<T> {
    public ConstantExpr(Token<T> token) {
        super(token);
    }

    @Override
    public void visit(ExprVisitor<T> visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return String.valueOf(token.getText());
    }

}
