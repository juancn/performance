package performance.parser;

public interface Lexer<T> {
    Token<T> next() throws ParseException;
}
