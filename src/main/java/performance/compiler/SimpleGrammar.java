package performance.compiler;

import performance.parser.Grammar;
import performance.parser.Lexer;
import performance.parser.ParseException;
import performance.parser.PrattParser;
import performance.parser.PrefixParser;
import performance.parser.Token;
import performance.parser.ast.Expr;
import performance.parser.ast.UnaryExpr;

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

        infix(PLUS, 50);
        infix(MINUS, 50);

        infix(MUL, 60);
        infix(DIV, 60);

        unary(MINUS, 70);
        unary(NOT, 70);

        infix(DOT, 80);

        prefix(LPAREN, new PrefixParser<TokenType>(){
            @Override
            public Expr<TokenType> parse(PrattParser<TokenType> prattParser, Token<TokenType> tokenTypeToken) throws ParseException {
                Expr<TokenType> result = prattParser.parseExpression(0);
                if(prattParser.consume().getType() != RPAREN) {
                    throw new ParseException("Unmatched left parenthesis");
                }
                return result;
            }
        });

        prefix(DOLLAR_LCURLY, new PrefixParser<TokenType>(){
            @Override
            public Expr<TokenType> parse(PrattParser<TokenType> prattParser, Token<TokenType> token) throws ParseException {
                Expr<TokenType> result = new UnaryExpr<TokenType>(token, prattParser.parseExpression(70));
                if(prattParser.consume().getType() != RCURLY) {
                    throw new ParseException("Sub-expression not closed missing '}'");
                }
                return result;
            }
        });

        literal(INT_LITERAL);
        literal(LONG_LITERAL);
        literal(FLOAT_LITERAL);
        literal(DOUBLE_LITERAL);
        literal(STRING);
        literal(ID);
        literal(THIS);
        literal(STATIC);
    }


    public static final SimpleGrammar INSTANCE = new SimpleGrammar();

    public static void main(String[] args) throws ParseException {
        String text = "Test.bah < ${list.size}";
        Expr expr = parse(text);
        System.out.println("expr = " + expr);
    }

    public static Expr<TokenType> parse(final String text) throws ParseException {
        final Lexer<TokenType> lexer = new JavaLexer(text, 0 , text.length());


//        for(Token<TokenType> t = lexer.next(); t.getType() != EOF; t = lexer.next()) {
//            System.out.println("t = " + t);
//        }

        final PrattParser<TokenType> prattParser = new PrattParser<TokenType>(INSTANCE, lexer);
        final Expr<TokenType> expr = prattParser.parseExpression(0);
        if(prattParser.current().getType() != EOF) {
            throw new ParseException("Unexpected token: " + prattParser.current());
        }
        return expr;
    }

}
