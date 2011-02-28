package performance.compiler.eval;

import performance.compiler.TokenType;
import performance.parser.ast.BinaryExpr;
import performance.parser.ast.ConstantExpr;
import performance.parser.ast.ExprAdapter;
import performance.util.MutableArray;

class ValueVisitor
        extends ExprAdapter<TokenType> {
    private final MutableArray<CharSequence> components = new MutableArray<CharSequence>();

    @Override
    public void visit(BinaryExpr<TokenType> expr) {
        switch (expr.getToken().getType()) {
            case DOT:
                expr.getLeft().visit(this);
                expr.getRight().visit(this);
                break;
        }
    }

    @Override
    public void visit(ConstantExpr<TokenType> expr) {
        components.add(expr.getToken().getText());
    }

    MutableArray<CharSequence> components() {
        return components;
    }

    @Override
    public String toString() {
        return components.join(".");
    }
}
