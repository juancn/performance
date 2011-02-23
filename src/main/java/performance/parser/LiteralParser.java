package performance.parser;

import performance.parser.ast.ConstantExpr;
import performance.parser.ast.Expr;

class LiteralParser<T>
        extends PrefixParser<T> {
    Expr parse(PrattParser prattParser, Token<T> token)
            throws LexicalException {
        return new ConstantExpr<T>(token);
    }
}
