package performance.parser;

public interface Token<T> {
    T getType();
    CharSequence getText();
}
