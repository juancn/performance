package performance.parser;

import performance.parser.ast.Expr;

public final class PrattParser<T> {
    private final Grammar<T> grammar;
    private final Lexer<T> lexer;
    private Token<T> current;

    public PrattParser(Grammar<T> grammar, Lexer<T> lexer)
            throws ParseException
    {
        this.grammar = grammar;
        this.lexer = lexer;
        current = lexer.next();
    }

    public Expr parseExpression(int stickiness) throws ParseException {
        Token<T> token = consume();
        final PrefixParser<T> prefix = grammar.getPrefixParser(token);
        Expr left = prefix.parse(this, token);

        while (stickiness < grammar.getStickiness(current())) {
            token = consume();

            InfixParser<T> infix = grammar.getInfixParser(token);
            left = infix.parse(this, left, token);
        }

        return left;
    }

    public Token<T> current() {
        return current;
    }

    private Token<T> consume() throws ParseException {
        Token<T> result = current;
        current = lexer.next();
        return result;
    }
}
