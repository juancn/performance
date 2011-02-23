package performance.parser;

import performance.parser.ast.Expr;

public abstract class PrefixParser<T> {
    public abstract Expr parse(PrattParser prattParser, Token<T> token)
            throws ParseException;
}
