package performance.compiler.eval;

import performance.compiler.TokenType;

public class BinOp extends Op {
    TokenType operator;
    Op left;
    Op right;

    public BinOp(TokenType operator, Op left, Op right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean booleanVal()
    {
            switch(operator) {
            case LT:
                return left.doubleVal() < right.doubleVal();
            case LE:
                return left.doubleVal() <= right.doubleVal();
            case GT:
                return left.doubleVal() > right.doubleVal();
            case GE:
                return left.doubleVal() >= right.doubleVal();
            case LOR:
                return left.booleanVal() || right.booleanVal();
            case LAND:
                return left.booleanVal() && right.booleanVal();
            default:
                return super.booleanVal();
        }
    }

    @Override
    public double doubleVal() {
        switch(operator) {
            case PLUS:
                return left.doubleVal() + right.doubleVal();
            case MINUS:
                return left.doubleVal() - right.doubleVal();
            case MUL:
                return left.doubleVal() * right.doubleVal();
            case DIV:
                return left.doubleVal() / right.doubleVal();
            default:
                return super.doubleVal();
        }
    }
}
