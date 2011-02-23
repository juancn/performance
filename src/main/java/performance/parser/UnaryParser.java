package performance.parser;

import performance.parser.ast.Expr;
import performance.parser.ast.UnaryExpr;

class UnaryParser<T>
    extends PrefixParser<T> {
    private final int stickiness;

    public UnaryParser(int stickiness) {
        this.stickiness = stickiness;
    }

    Expr parse(PrattParser prattParser, Token<T> token)
            throws LexicalException {
        return new UnaryExpr<T>(token, prattParser.parseExpression(stickiness));
    }
}
