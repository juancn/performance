package performance.parser;

import performance.parser.ast.BinaryExpr;
import performance.parser.ast.Expr;

class InfixParser<T> {
    private final int stickiness;

    InfixParser(int stickiness) {
        this.stickiness = stickiness;
    }

    Expr parse(PrattParser prattParser, Expr left, Token<T> token)
            throws LexicalException {
        return new BinaryExpr<T>(token, left, prattParser.parseExpression(getStickiness()));
    }

    int getStickiness()
    {
        return stickiness;
    }
}
