package performance.parser.ast;

public interface ExprVisitor<T> {
    void visit(BinaryExpr<T> expr);
    void visit(ConstantExpr<T> expr);
    void visit(UnaryExpr<T> expr);
}
