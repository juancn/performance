package performance.parser;
/** A lexer */
public interface Lexer<T> {
	/** @return the next token */
    Token<T> next() throws ParseException;
}
