package performance.parser.ast;

import performance.parser.Token;

public abstract class Expr<T> {
    protected Token<T> token;

    public Expr(Token<T> token) {
        this.token = token;
    }

    public abstract void visit(ExprVisitor<T> visitor);

    public Token<T> getToken() {
        return token;
    }
}
