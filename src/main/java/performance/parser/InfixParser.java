package performance.parser;

import performance.parser.ast.BinaryExpr;
import performance.parser.ast.Expr;

/** Base class for infix parsers */
public class InfixParser<T> {
    private final int precedence;

    protected InfixParser(int precedence) {
        this.precedence = precedence;
    }

    public Expr<T> parse(PrattParser<T> prattParser, Expr<T> left, Token<T> token)
            throws ParseException {
        return new BinaryExpr<T>(token, left, prattParser.parseExpression(getPrecedence()));
    }

    protected int getPrecedence()
    {
        return precedence;
    }
}
