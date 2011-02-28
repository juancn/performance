package performance.parser;

import performance.parser.ast.Expr;
import performance.parser.ast.UnaryExpr;

class DelimitedParser<T>
        extends PrefixParser<T> {
    private final T right;
    private final int stickiness;
    private final boolean keep;

    DelimitedParser(T right, int stickiness, boolean keep) {
        this.right = right;
        this.stickiness = stickiness;
        this.keep = keep;
    }

    @Override
    public Expr<T> parse(PrattParser<T> prattParser, Token<T> token) throws ParseException {
        final Expr<T> subExpression = prattParser.parseExpression(stickiness);
        if(prattParser.consume().getType() != right) {
            throw new ParseException("Sub-expression not closed missing " + right);
        }
        return keep ? new UnaryExpr<T>(token, subExpression) : subExpression;
    }
}
