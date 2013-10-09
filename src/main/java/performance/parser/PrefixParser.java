package performance.parser;

import performance.parser.ast.Expr;

/** Base class for prefix parsers */
public abstract class PrefixParser<T> {
    public abstract Expr<T> parse(PrattParser<T> prattParser, Token<T> token)
            throws ParseException;
}
