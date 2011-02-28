package performance.compiler;

import performance.parser.Grammar;
import performance.parser.Lexer;
import performance.parser.ParseException;
import performance.parser.PrattParser;
import performance.parser.ast.Expr;

import static performance.compiler.TokenType.*;

public final class SimpleGrammar
    extends Grammar<TokenType> {
    private SimpleGrammar() {
        infix(LAND, 30);
        infix(LOR, 30);

        infix(LT, 40);
        infix(GT, 40);
        infix(LE, 40);
        infix(GE, 40);
        infix(EQ, 40);
        infix(NEQ, 40);

        infix(PLUS, 50);
        infix(MINUS, 50);

        infix(MUL, 60);
        infix(DIV, 60);

        unary(MINUS, 70);
        unary(NOT, 70);

        infix(DOT, 80);

        clarifying(LPAREN, RPAREN, 0);
        delimited(DOLLAR_LCURLY, RCURLY, 70);

        literal(INT_LITERAL);
        literal(LONG_LITERAL);
        literal(FLOAT_LITERAL);
        literal(DOUBLE_LITERAL);
        literal(ID);
        literal(THIS);
        literal(STATIC);
    }

    public static Expr<TokenType> parse(final String text) throws ParseException {
        final Lexer<TokenType> lexer = new JavaLexer(text, 0 , text.length());
        final PrattParser<TokenType> prattParser = new PrattParser<TokenType>(INSTANCE, lexer);
        final Expr<TokenType> expr = prattParser.parseExpression(0);
        if(prattParser.current().getType() != EOF) {
            throw new ParseException("Unexpected token: " + prattParser.current());
        }
        return expr;
    }

    private static final SimpleGrammar INSTANCE = new SimpleGrammar();
}
