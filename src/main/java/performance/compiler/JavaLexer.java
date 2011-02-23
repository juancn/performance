package performance.compiler;

import performance.parser.Lexer;
import performance.parser.ParseException;
import performance.parser.Token;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;


@SuppressWarnings({"StatementWithEmptyBody"})
public class JavaLexer
        implements Lexer<TokenType>
{
    public CharSequence text;
    private int startOffset;
    public int offset;
    private int endOffset;
    private int column;
    private int line = 1;
    private int lastColumn = 1;

    //Current token
    private int tokenStart;


    public JavaLexer(CharSequence text, int startOffset, int length)
    {
        this.text = text;
        this.startOffset = startOffset;
        this.endOffset = startOffset + length;
        this.offset = startOffset;
    }

    @Override
    public Token<TokenType> next() throws ParseException
    {
        skipWhiteSpace();

        JavaToken token;
        tokenStart = offset;
        char c = read();


        if(c == EOF) {
            token = JavaToken.EOF;
        } else if(isDigit(c,10)) {
            unread();
            token = parseNumber();
        } else if(isIdStart(c)) {
            token = parseId();
        } else if(c == '"') {
            token = parseString();
        } else {
            TokenType ttype = parseMultichar(c);
            if(ttype == null) {
                throw new ParseException("unexpected character: " + c);
            } else {
                token = makeToken(ttype);
            }
        }

        return token;
    }

    private JavaToken parseString() throws ParseException
    {
        char c = read();
        while(c != '"') {
            switch(c) {
            case '\\':
                c = read();
                switch(c) {
                case '\\':
                case '"':
                case '\'':
                case 'b':
                case 't':
                case 'n':
                case 'f':
                case 'r':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                    break;
                default:
                    throw new ParseException("Unrecognized escape sequence");
                }
                break;
            case '\n':
            case '\r':
            case EOF:
                throw new ParseException("Unterminated string constant");
            }
        }
        unread();
        return makeToken(TokenType.STRING);
    }

    private TokenType parseMultichar(char c)
    {
        final TokenType ttype;
        switch(c) {
        case '.': ttype = TokenType.DOT; break;
        case ',': ttype = TokenType.COMMA; break;
        case ';': ttype = TokenType.SEMICOLON; break;
        case ':': ttype = TokenType.COLON; break;
        case '^': ttype = TokenType.BXOR; break;
        case '?': ttype = TokenType.QMARK; break;
        case '[': ttype = TokenType.LBRACKET; break;
        case ']': ttype = TokenType.RBRACKET; break;
        case '{': ttype = TokenType.LCURLY; break;
        case '}': ttype = TokenType.RCURLY; break;
        case '(': ttype = TokenType.LPAREN; break;
        case ')': ttype = TokenType.RPAREN; break;
        case '<':
            switch(read()) {
            case '=': ttype = TokenType.LE; break;
            case '<': ttype = TokenType.LSHIFT; break;
            default: unread();ttype = TokenType.LT;
            }
            break;
        case '>':
            switch(read()) {
            case '=': ttype = TokenType.GE; break;
            case '>': ttype = TokenType.RSHIFT; break;
            default: unread();ttype = TokenType.GT;
            }
            break;
        case '=':
            switch(read()) {
            case '=': ttype = TokenType.EQ; break;
            default: unread();ttype = TokenType.ASSIGN;
            }
            break;
        case '*':
            switch(read()) {
            case '=': ttype = TokenType.MUL_ASSIGN; break;
            default: unread();ttype = TokenType.MUL;
            }
            break;
        case '/':
            switch(read()) {
            case '=': ttype = TokenType.DIV_ASSIGN; break;
            case '/':
                ttype = TokenType.SL_COMMENT;
                for(c = read(); c != '\n' && c != EOF; c = read());
                break;
            case '*':
                ttype = TokenType.ML_COMMENT;
                for(c = read(); c != EOF; c = read()) {
                    if(c == '*') {
                        if(read() == '/') {
                            break;
                        } else {
                            unread();
                        }
                    }
                }
                break;
            default: unread();ttype = TokenType.DIV;
            }
            break;
        case '%':
            switch(read()) {
            case '=': ttype = TokenType.MOD_ASSIGN; break;
            default: unread();ttype = TokenType.MOD;
            }
            break;

        case '&':
            switch(read()) {
            case '&': ttype = TokenType.LAND; break;
            default: unread();ttype = TokenType.BAND;
            }
            break;

        case '|':
            switch(read()) {
            case '|': ttype = TokenType.LOR; break;
            default: unread();ttype = TokenType.BOR;
            }
            break;


        case '+':
            switch(read()) {
            case '=': ttype = TokenType.PLUS_ASSIGN; break;
            case '+': ttype = TokenType.INC; break;
            default: unread();ttype = TokenType.PLUS;
            }
            break;
        case '-':
            switch(read()) {
            case '=': ttype = TokenType.MINUS_ASSIGN; break;
            case '-': ttype = TokenType.DEC; break;
            default: unread();ttype = TokenType.MINUS;
            }
            break;
        case '!':
            switch(read()) {
            case '=': ttype = TokenType.NEQ; break;
            default: unread();ttype = TokenType.NOT;
            }
            break;
        default:
            ttype = null;
        }
        return ttype;
    }

    private JavaToken parseId()
    {
        char c = read();
        while(isIdPart(c)) {
            c = read();
        }
        unread();
        return makeToken(checkReserved());
    }

    private TokenType checkReserved()
    {
        CharSequence id = text.subSequence(tokenStart, offset);
        final TokenType ttype = RESERVED.get(id);
        if(ttype != null) {
            return ttype;
        }
        return TokenType.ID;
    }

    private boolean isIdPart(char c)
    {
        return c != EOF && Character.isJavaIdentifierPart(c);
    }

    private boolean isIdStart(char c)
    {
        return c != EOF && Character.isJavaIdentifierStart(c);
    }

    JavaToken parseNumber()
    {
        TokenType ttype = TokenType.INT_LITERAL;
        char c = read();
        int radix = 10;


        if(c == '0') {
            c = read();
            if(c == 'x' || c == 'X') {
                radix = 16;
                c = read();
            } else {
                radix = 8;
            }
        }

        while(isDigit(c, radix)) {
            c = read();
        }

        if(radix == 10) {
            if(c == '.') {
                c = read();
                while(isDigit(c, radix)) {
                    c = read();
                }
                ttype = TokenType.FLOAT_LITERAL;
            }

            if (c == 'E' || c == 'e') {
                c = read();
                if(c == '+' || c == '-') {
                    c = read();
                }
                while(isDigit(c, radix)) {
                    c = read();
                }
                ttype = TokenType.FLOAT_LITERAL;
            }
        }
        if(ttype == TokenType.INT_LITERAL && (c == 'l' || c == 'L')) {
            ttype = TokenType.LONG_LITERAL;
        } else if(c == 'f' || c == 'F') {
            ttype = TokenType.FLOAT_LITERAL;
        } else if(c == 'd' || c == 'D') {
            ttype = TokenType.DOUBLE_LITERAL;
        } else {
            unread();
        }
        return makeToken(ttype);
    }

    private JavaToken makeToken(TokenType ttype)
    {
        return new JavaToken(ttype, text, tokenStart, offset, line, column - offset + tokenStart);
    }

    private boolean isDigit(char c, int radix)
    {
        return c != EOF && Character.digit(c, radix) != -1;
    }

    void skipWhiteSpace()
    {
        for(char c = read(); c != EOF && Character.isWhitespace(c); c = read());
        unread();
    }

    char read()
    {
        char read;
        if(offset >= endOffset) {
            read = EOF;
            ++offset;
        } else {
            read = text.charAt(offset++);
        }

        if(read == '\n') {
            lastColumn = column;
            column = 1;
            line++;
        } else {
            ++column;
        }
        return read;
    }

    void unread()
    {
        if(--offset < startOffset) {
            throw new IllegalStateException("too many unreads");
        }
        if(--column == 0) {
            column = lastColumn;
            if(line > 1) {
                --line;
            }
        }
    }

    private static final char EOF = '\uFFFF';
    private static Map<CharSequence, TokenType> RESERVED = new TreeMap<CharSequence, TokenType>(new CharSequenceComparator<CharSequence>());

    private static void reserved(CharSequence id, TokenType ttype)
    {
        RESERVED.put(id, ttype);
    }

    static {
        reserved("null", TokenType.NULL);
        reserved("true", TokenType.TRUE);
        reserved("false", TokenType.FALSE);
        reserved("atomic",TokenType.ATOMIC);
        reserved("abstract",TokenType.ABSTRACT);
        reserved("boolean",TokenType.BOOLEAN);
        reserved("break",TokenType.BREAK);
        reserved("byte",TokenType.BYTE);
        reserved("case",TokenType.CASE);
        reserved("catch",TokenType.CATCH);
        reserved("char",TokenType.CHAR);
        reserved("class",TokenType.CLASS);
        reserved("const",TokenType.CONST);
        reserved("continue",TokenType.CONTINUE);
        reserved("default",TokenType.DEFAULT);
        reserved("do",TokenType.DO);
        reserved("double",TokenType.DOUBLE);
        reserved("else",TokenType.ELSE);
        reserved("extends",TokenType.EXTENDS);
        reserved("final",TokenType.FINAL);
        reserved("finally",TokenType.FINALLY);
        reserved("float",TokenType.FLOAT);
        reserved("for",TokenType.FOR);
        reserved("goto",TokenType.GOTO);
        reserved("if",TokenType.IF);
        reserved("implements",TokenType.IMPLEMENTS);
        reserved("import",TokenType.IMPORT);
        reserved("instanceof",TokenType.INSTANCEOF);
        reserved("int",TokenType.INT);
        reserved("interface",TokenType.INTERFACE);
        reserved("long",TokenType.LONG);
        reserved("native",TokenType.NATIVE);
        reserved("new",TokenType.NEW);
        reserved("package",TokenType.PACKAGE);
        reserved("private",TokenType.PRIVATE);
        reserved("protected",TokenType.PROTECTED);
        reserved("public",TokenType.PUBLIC);
        reserved("return",TokenType.RETURN);
        reserved("retry",TokenType.RETRY);
        reserved("short",TokenType.SHORT);
        reserved("static",TokenType.STATIC);
        reserved("strictfp",TokenType.STRICTFP);
        reserved("super",TokenType.SUPER);
        reserved("switch",TokenType.SWITCH);
        reserved("this",TokenType.THIS);
        reserved("throw",TokenType.THROW);
        reserved("throws",TokenType.THROWS);
        reserved("transient",TokenType.TRANSIENT);
        reserved("try",TokenType.TRY);
        reserved("void",TokenType.VOID);
        reserved("volatile",TokenType.VOLATILE);
        reserved("while",TokenType.WHILE);
    }

    private static class CharSequenceComparator<T extends CharSequence>
            implements Comparator<T>
    {

        public int compare(T l, T r)
        {
            int llen = l.length();
            int rlen = r.length();
            int n = Math.min(llen, rlen);
            for(int i = 0; i < n; i++) {
                char c1 = l.charAt(i);
                char c2 = r.charAt(i);
                if (c1 != c2) {
                    return c1 - c2;
                }
            }
            return llen - rlen;
        }
    }


}
