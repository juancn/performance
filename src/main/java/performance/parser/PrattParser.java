package performance.parser;

import performance.parser.ast.Expr;

/** A Pratt parser, that given a {@link Grammar} and a {@link Lexer} parses an expression */
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

    public Expr<T> parseExpression(int precedence) throws ParseException {
        Token<T> token = consume();
        final PrefixParser<T> prefix = grammar.getPrefixParser(token);
        if(prefix == null) {
            throw new ParseException("Unexpected token: " + token);
        }
        Expr<T> left = prefix.parse(this, token);

        while (precedence < grammar.getPrecedence(current())) {
            token = consume();

            final InfixParser<T> infix = grammar.getInfixParser(token);
            left = infix.parse(this, left, token);
        }

        return left;
    }

    public Token<T> current() {
        return current;
    }

    public Token<T> consume() throws ParseException {
        Token<T> result = current;
        current = lexer.next();
        return result;
    }
}
