package performance.parser;

import performance.parser.ast.Expr;

abstract class PrefixParser<T> {
    abstract Expr parse(PrattParser prattParser, Token<T> token)
            throws LexicalException;
}
