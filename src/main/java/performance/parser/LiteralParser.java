package performance.parser;

import performance.parser.ast.ConstantExpr;
import performance.parser.ast.Expr;

class LiteralParser<T>
        extends PrefixParser<T> {
    public Expr<T> parse(PrattParser<T> prattParser, Token<T> token)
            throws ParseException {
        return new ConstantExpr<T>(token);
    }
}
