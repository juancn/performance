package performance.parser;

import performance.parser.ast.Expr;

public abstract class PrefixParser<T> {
    public abstract Expr<T> parse(PrattParser<T> prattParser, Token<T> token)
            throws ParseException;
}
