package performance.parser;

import performance.parser.ast.Expr;
import performance.parser.ast.UnaryExpr;

class UnaryParser<T>
    extends PrefixParser<T> {
    private final int stickiness;

    public UnaryParser(int stickiness) {
        this.stickiness = stickiness;
    }

    public Expr<T> parse(PrattParser<T> prattParser, Token<T> token)
            throws ParseException {
        return new UnaryExpr<T>(token, prattParser.parseExpression(stickiness));
    }
}
