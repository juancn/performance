package performance.compiler;

import performance.parser.Grammar;
import performance.parser.Lexer;
import performance.parser.ParseException;
import performance.parser.PrattParser;
import performance.parser.PrefixParser;
import performance.parser.Token;
import performance.parser.ast.Expr;

public final class SimpleGrammar
    extends Grammar<TokenType> {
    private SimpleGrammar() {
        infix(TokenType.LAND, 30);
        infix(TokenType.LOR, 30);

        infix(TokenType.LT, 40);
        infix(TokenType.GT, 40);
        infix(TokenType.LE, 40);
        infix(TokenType.GE, 40);

        infix(TokenType.PLUS, 50);
        infix(TokenType.MINUS, 50);

        infix(TokenType.MUL, 60);
        infix(TokenType.DIV, 60);

        unary(TokenType.MINUS, 70);

        infix(TokenType.DOT, 80);

        prefix(TokenType.LPAREN, new PrefixParser<TokenType>(){
            @Override
            public Expr<TokenType> parse(PrattParser<TokenType> prattParser, Token<TokenType> tokenTypeToken) throws ParseException {
                Expr<TokenType> result = prattParser.parseExpression(0);
                if(prattParser.current().getType() != TokenType.RPAREN) {
                    throw new ParseException("Unmatched right parenthesis");
                }
                return result;
            }
        });

        constant(TokenType.INT_LITERAL);
        constant(TokenType.LONG_LITERAL);
        constant(TokenType.FLOAT_LITERAL);
        constant(TokenType.DOUBLE_LITERAL);
        constant(TokenType.STRING);
        constant(TokenType.ID);
    }


    public static final SimpleGrammar INSTANCE = new SimpleGrammar();

    public static void main(String[] args) throws ParseException {
        String text = "java.lang.String.toString < 5 || a < b";
        Expr expr = parse(text);
        System.out.println("expr = " + expr);
    }

    public static Expr<TokenType> parse(final String text) throws ParseException {
        final Lexer<TokenType> lexer = new JavaLexer(text, 0 , text.length());
        final PrattParser<TokenType> prattParser = new PrattParser<TokenType>(INSTANCE, lexer);
        return prattParser.parseExpression(0);
    }

}
