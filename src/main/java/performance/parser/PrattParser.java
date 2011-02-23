package performance.parser;

import performance.lexer.JavaLexer;
import performance.lexer.TokenType;
import performance.parser.ast.Expr;

public final class PrattParser<T> {
    private final Grammar<T> grammar;
    private final Lexer<T> lexer;
    private Token<T> current;

    public PrattParser(Grammar<T> grammar, Lexer<T> lexer)
            throws LexicalException
    {
        this.grammar = grammar;
        this.lexer = lexer;
        current = lexer.next();
    }

    public Expr parseExpression(int stickiness) throws LexicalException {
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

    private Token<T> current() {
        return current;
    }

    private Token<T> consume() throws LexicalException {
        Token<T> result = current;
        current = lexer.next();
        return result;
    }


    public static void main(String[] args) throws LexicalException {
        Grammar<TokenType> g = new Grammar<TokenType>();
        g.infix(TokenType.PLUS, 50);
        g.infix(TokenType.MINUS, 50);
        g.infix(TokenType.MUL, 60);
        g.infix(TokenType.DIV, 60);
        g.unary(TokenType.MINUS, 70);
        g.constant(TokenType.INT_LITERAL);
        g.constant(TokenType.FLOAT_LITERAL);
        g.constant(TokenType.STRING);

        String text = "2+-5*3-1";
        Lexer l = new JavaLexer(text, 0 , text.length());
        PrattParser<TokenType> prattParser = new PrattParser<TokenType>(g, l);
        Expr expr = prattParser.parseExpression(0);
        System.out.println("expr = " + expr);
    }
}
