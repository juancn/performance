package performance.parser.ast;

public abstract class Expr<T> {
    public abstract void visit(ExprVisitor<T> visitor);
}
