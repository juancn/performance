package performance.parser;

import performance.parser.ast.Expr;
import performance.parser.ast.UnaryExpr;

/** A prefix parser that parses unary expressions */
class UnaryParser<T>
    extends PrefixParser<T> {

	/** Precedence of the expression */
	private final int precedence;

    public UnaryParser(int precedence) {
        this.precedence = precedence;
    }

    public Expr<T> parse(PrattParser<T> prattParser, Token<T> token)
            throws ParseException {
        return new UnaryExpr<T>(token, prattParser.parseExpression(precedence));
    }
}
