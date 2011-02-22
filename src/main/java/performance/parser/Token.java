package performance.parser;

public class Token
{

    //~ Instance fields ......................................................................................

    private int          column;
    private int          endOffset;
    private int          line;
    private int          startOffset;
    private CharSequence text;
    private TokenType    ttype;

    //~ Constructors .........................................................................................

    public Token(TokenType ttype, CharSequence text, int startOffset, int endOffset, int line, int column)
    {
        this.ttype = ttype;
        this.text = text;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.line = line;
        this.column = column;
    }

    //~ Methods ..............................................................................................

    public CharSequence getText()
    {
        return ttype == TokenType.EOF ? "EOF" : text.subSequence(startOffset, endOffset);
    }

    public String toString()
    {
        return "[" + ttype + ", " + getText() + " (" + line + ":" + column + ") ]";
    }

    public TokenType getType()
    {
        return ttype;
    }

    public int getLine()
    {
        return line;
    }

    public int getColumn()
    {
        return column;
    }

    //~ Static fields/initializers ...........................................................................


    public static final Token EOF = new Token(TokenType.EOF, "EOF", -1, -1, -1, -1);
}
