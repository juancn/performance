package performance.parser;

import java.util.HashMap;
import java.util.Map;

public final class PrattParser {
    private final Grammar grammar;
    private final Lexer lexer;
    private Token current;

    public PrattParser(Grammar grammar, Lexer lexer)
            throws LexicalException
    {
        this.grammar = grammar;
        this.lexer = lexer;
        current = lexer.next();
    }

    public Expr parseExpression(int stickiness) throws LexicalException {
        Token token = consume();
        final PrefixParser prefix = grammar.getPrefixParser(token);
        //notNull(prefix);
        Expr left = prefix.parse(this, token);

        while (stickiness < grammar.getStickiness(current())) {
            token = consume();

            InfixParser infix = grammar.getInfixParser(token);
            left = infix.parse(this, left, token);
        }

        return left;
    }

    private Token current() {
        return current;
    }

    private Token consume() throws LexicalException {
        Token result = current;
        current = lexer.next();
        return result;
    }

    static abstract class PrefixParser {
        abstract Expr parse(PrattParser prattParser, Token token)
                throws LexicalException;
    }

    static class UnaryParser
        extends PrefixParser {
        private final int stickiness;

        public UnaryParser(int stickiness) {
            this.stickiness = stickiness;
        }

        Expr parse(PrattParser prattParser, Token token)
                throws LexicalException {
            return new UnaryExpr(token, prattParser.parseExpression(stickiness));
        }
    }

    static class LiteralParser
            extends PrefixParser {
        Expr parse(PrattParser prattParser, Token token)
                throws LexicalException {
            return new ConstantExpr(token);
        }
    }

    static class InfixParser {
        private final int stickiness;

        InfixParser(int stickiness) {
            this.stickiness = stickiness;
        }

        Expr parse(PrattParser prattParser, Expr left, Token token)
                throws LexicalException {
            return new BinaryExpr(token, left, prattParser.parseExpression(getStickiness()));
        }

        int getStickiness()
        {
            return stickiness;
        }
    }

    static abstract class Expr {}

    static class BinaryExpr
            extends Expr {
        private Token token;
        private Expr left;
        private Expr right;


        BinaryExpr(Token token, Expr left, Expr right) {
            this.token = token;
            this.left = left;
            this.right = right;
        }

        @Override
        public String toString() {
            return "{ " + token.getText() + " " + left + " " + right + " }";
        }
    }

    static class UnaryExpr
            extends Expr {
        private Token token;
        private Expr op;

        UnaryExpr(Token token, Expr op) {
            this.token = token;
            this.op = op;
        }

        @Override
        public String toString() {
            return "{ " + token.getText() + " " + op + " }";
        }
    }

    static class ConstantExpr
            extends Expr {
        private Token token;
        ConstantExpr(Token token) {
            this.token = token;
        }

        @Override
        public String toString() {
            return String.valueOf(token.getText());
        }
    }

    public static class Grammar {
        private Map<TokenType, PrefixParser> prefixParsers = new HashMap<TokenType, PrefixParser>();
        private Map<TokenType, InfixParser>  infixParsers = new HashMap<TokenType, InfixParser>();

        PrefixParser getPrefixParser(Token token) {
            return prefixParsers.get(token.getType());
        }

        int getStickiness(Token token) {
            InfixParser infixParser = getInfixParser(token);
            return infixParser == null?Integer.MIN_VALUE:infixParser.getStickiness();
        }

        InfixParser getInfixParser(Token token) {
            return infixParsers.get(token.getType());
        }

        public void infix(TokenType ttype, int stickiness)
        {
            infixParsers.put(ttype, new InfixParser(stickiness));
        }

        public void unary(TokenType ttype, int stickiness)
        {
            prefixParsers.put(ttype, new UnaryParser(stickiness));
        }
        public void constant(TokenType ttype)
        {
            prefixParsers.put(ttype, new LiteralParser());
        }
    }


    public static void main(String[] args) throws LexicalException {
        Grammar g = new Grammar();
        g.infix(TokenType.PLUS, 50);
        g.infix(TokenType.MINUS, 50);
        g.infix(TokenType.MUL, 60);
        g.infix(TokenType.DIV, 60);
        g.unary(TokenType.MINUS, 70);
        g.constant(TokenType.INT_LITERAL);
        g.constant(TokenType.FLOAT_LITERAL);
        g.constant(TokenType.STRING);

        String text = "2+-5*3+1";
        Lexer l = new Lexer(text, 0 , text.length());
        PrattParser prattParser = new PrattParser(g, l);
        Expr expr = prattParser.parseExpression(0);
        System.out.println("expr = " + expr);
    }
}
