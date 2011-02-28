package performance.compiler;

import org.testng.annotations.Test;
import performance.parser.ParseException;
import performance.parser.ast.Expr;

public class GrammarTest {

    @Test
    public void idTest() throws ParseException {
        check("hi", "hi");
    }
    @Test
    public void intTest() throws ParseException {
        check("10", "10");
    }

    @Test
    public void longTest() throws ParseException {
        check("10L", "10L");
    }

    @Test
    public void floatTest() throws ParseException {
        check("1.5f", "1.5f");
    }
    @Test

    public void doubleTest() throws ParseException {
        check("1.5d", "1.5d");
    }

    @Test
    public void thisTest() throws ParseException {
        check("this", "this");
    }

    @Test
    public void staticTest() throws ParseException {
        check("static", "static");
    }

    @Test
    public void arithmeticTest() throws ParseException {
        check("{ + 1 { * 2 3 } }", "1+2*3");
        check("{ - { / 1 2 } 3 }", "1/2-3");
    }

    @Test
    public void relationalTest() throws ParseException {
        check("{ < { + a 1 } { + b 2 } }", "a+1 < b+2");
        check("{ <= { + a 1 } { + b 2 } }", "a+1 <= b+2");
        check("{ > { + a 1 } { + b 2 } }", "a+1 > b+2");
        check("{ >= { + a 1 } { + b 2 } }", "a+1 >= b+2");
        check("{ == { + a 1 } { + b 2 } }", "a+1 == b+2");
    }

    @Test
    public void parenthesizedTest() throws ParseException {
        check("{ * { + 1 2 } 3 }", "(1+2)*3");
        check("{ / 1 { - 2 3 } }", "1/(2-3)");
    }

    @Test
    public void dynamicTest() throws ParseException {
        check("{ * { ${ { . { . a b } c } } 2 }", "${a.b.c}*2");
    }

    @Test
    public void unaryTest() throws ParseException {
        check("{ - 1 { - 3 } }", "1-(-3)");
        check("{ || { ! a } b }", "!a||b");
    }

    private void check(String expected, String expression) throws ParseException {
        final Expr<TokenType> expr = SimpleGrammar.parse(expression);
        assert expected.equals(expr.toString()) : "unexpected AST: " + expr;
    }
}
