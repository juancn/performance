package performance.parser;

public class LexicalException
        extends Exception
{
    public LexicalException(String message)
    {
        super(message);
    }

    public LexicalException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
