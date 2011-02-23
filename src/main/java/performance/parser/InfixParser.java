package performance.parser;

import performance.parser.ast.BinaryExpr;
import performance.parser.ast.Expr;

public class InfixParser<T> {
    private final int stickiness;

    protected InfixParser(int stickiness) {
        this.stickiness = stickiness;
    }

    public Expr parse(PrattParser prattParser, Expr left, Token<T> token)
            throws ParseException {
        return new BinaryExpr<T>(token, left, prattParser.parseExpression(getStickiness()));
    }

    protected int getStickiness()
    {
        return stickiness;
    }
}
