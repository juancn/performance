package performance.compiler.eval;

import performance.compiler.TokenType;
import performance.parser.ast.BinaryExpr;
import performance.parser.ast.ConstantExpr;
import performance.parser.ast.ExprAdapter;

public class ValueVisitor
        extends ExprAdapter<TokenType> {
    StringBuilder sb = new StringBuilder();

    @Override
    public void visit(BinaryExpr<TokenType> expr) {
        switch (expr.getToken().getType()) {
            case DOT:
                expr.getLeft().visit(this);
                sb.append('.');
                expr.getRight().visit(this);
                break;
        }
    }

    @Override
    public void visit(ConstantExpr<TokenType> expr) {
        sb.append(expr.getToken().getText());
    }

    @Override
    public String toString() {
        return sb.toString();
    }
}
