package performance.parser;

import performance.parser.ast.ConstantExpr;
import performance.parser.ast.Expr;

/** A prefix parser that parses literals */
class LiteralParser<T>
        extends PrefixParser<T> {
    @Override
	public Expr<T> parse(PrattParser<T> prattParser, Token<T> token)
            throws ParseException {
        return new ConstantExpr<T>(token);
    }
}
