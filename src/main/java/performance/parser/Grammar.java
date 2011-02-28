package performance.parser;

import java.util.HashMap;
import java.util.Map;

public class Grammar<T> {
    private Map<T, PrefixParser<T>> prefixParsers = new HashMap<T, PrefixParser<T>>();
    private Map<T, InfixParser<T>>  infixParsers = new HashMap<T, InfixParser<T>>();

    PrefixParser<T> getPrefixParser(Token<T> token) {
        return prefixParsers.get(token.getType());
    }

    int getStickiness(Token<T> token) {
        InfixParser infixParser = getInfixParser(token);
        return infixParser == null?Integer.MIN_VALUE:infixParser.getStickiness();
    }

    InfixParser<T> getInfixParser(Token<T> token) {
        return infixParsers.get(token.getType());
    }

    protected void infix(T ttype, int stickiness)
    {
        infix(ttype, new InfixParser<T>(stickiness));
    }

    protected void infix(T ttype, InfixParser<T> value) {
        infixParsers.put(ttype, value);
    }

    protected void unary(T ttype, int stickiness)
    {
        prefixParsers.put(ttype, new UnaryParser<T>(stickiness));
    }
    protected void literal(T ttype)
    {
        prefix(ttype, new LiteralParser<T>());
    }

    protected void prefix(T ttype, PrefixParser<T> value) {
        prefixParsers.put(ttype, value);
    }

    protected void delimited(T left, T right, int subExpStickiness) {
        prefixParsers.put(left, new DelimitedParser<T>(right, subExpStickiness, true));
    }

    protected void clarifying(T left, T right, int subExpStickiness) {
        prefixParsers.put(left, new DelimitedParser<T>(right, subExpStickiness, false));
    }
}
