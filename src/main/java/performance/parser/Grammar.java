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

    public void infix(T ttype, int stickiness)
    {
        infix(ttype, new InfixParser<T>(stickiness));
    }

    private void infix(T ttype, InfixParser<T> value) {
        infixParsers.put(ttype, value);
    }

    public void unary(T ttype, int stickiness)
    {
        prefixParsers.put(ttype, new UnaryParser<T>(stickiness));
    }
    public void constant(T ttype)
    {
        prefix(ttype, new LiteralParser<T>());
    }

    private void prefix(T ttype, LiteralParser<T> value) {
        prefixParsers.put(ttype, value);
    }
}
