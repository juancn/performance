package performance.parser;

import performance.parser.ast.Expr;
import performance.parser.ast.UnaryExpr;

/** Prefix parser that parses a delimited expression */
class DelimitedParser<T>
        extends PrefixParser<T> {

	/** Token type of the right delimiter */
    private final T right;

	/** Precedence of the delimited expression */
    private final int precedence;

	/** Whether or not to keep the delimiting token */
    private final boolean keep;

    DelimitedParser(T right, int precedence, boolean keep) {
        this.right = right;
        this.precedence = precedence;
        this.keep = keep;
    }

    @Override
    public Expr<T> parse(PrattParser<T> prattParser, Token<T> token) throws ParseException {
        final Expr<T> subExpression = prattParser.parseExpression(precedence);
        if(prattParser.consume().getType() != right) {
            throw new ParseException("Sub-expression not closed missing " + right);
        }
        return keep ? new UnaryExpr<T>(token, subExpression) : subExpression;
    }
}
