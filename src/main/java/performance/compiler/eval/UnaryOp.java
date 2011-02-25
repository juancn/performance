package performance.compiler.eval;

import performance.compiler.TokenType;

public class UnaryOp extends Op {
    private final TokenType operator;
    private final Op first;

    public UnaryOp(TokenType operator, Op first) {
        this.operator = operator;
        this.first = first;
    }

    @Override
    public boolean booleanVal()
    {
        switch(operator) {
            case NOT:
                return !first.booleanVal();
            default:
                return super.booleanVal();
        }
    }

    @Override
    public double doubleVal() {
        switch(operator) {
            case MINUS:
                return -first.doubleVal();
            default:
                return super.doubleVal();
        }
    }
}
