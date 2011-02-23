package performance.parser.ast;

public class ExprAdapter<T>
    implements ExprVisitor<T> {
    @Override
    public void visit(BinaryExpr<T> expr) {}

    @Override
    public void visit(ConstantExpr<T> expr) {}

    @Override
    public void visit(UnaryExpr<T> expr) {}
}
