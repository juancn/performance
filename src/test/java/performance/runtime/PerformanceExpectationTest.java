package performance.runtime;

import org.testng.annotations.Test;
import performance.parser.ParseException;

public class PerformanceExpectationTest {

    @SuppressWarnings({"FieldCanBeLocal"})
    private final int instanceVar = 54321;

    @Test
    public void methodMatchTest() throws ParseException {
        final PerformanceExpectation expectation = expectation("foo == 5", null);
        for (int i = 0; i < 5; i++) {
            expectation.methodEnter(PerformanceExpectationTest.class, "foo");
        }
        expectation.validate();
    }

    @Test(expectedExceptions = {AssertionError.class})
    public void methodMatchFailTest() throws ParseException {
        expectation("foo == 5", null).validate();
    }

    @Test
    public void unnamedStaticArgumentTest() throws ParseException {
        expectation("${0} == 42", null, 42).validate();
    }

    @Test
    public void unnamedInstanceArgumentTest() throws ParseException {
        expectation("${0} == 42", this, 42).validate();
    }

    @Test
    public void namedStaticArgumentTest() throws ParseException {
        expectation("${first} == 42", null, 42).validate();
    }

    @Test
    public void namedInstanceArgumentTest() throws ParseException {
        expectation("${first} == 42", this, 42).validate();
    }

    @Test
    public void staticVarStaticTest() throws ParseException {
        expectation("${static.STATIC_VAR} == " + STATIC_VAR, null).validate();
    }

    @Test
    public void staticVarInstanceTest() throws ParseException {
        expectation("${this.STATIC_VAR} == " + STATIC_VAR, this).validate();
    }

    @Test
    public void instanceVarTest() throws ParseException {
        expectation("${this.instanceVar} == " + instanceVar, this).validate();
    }


    @Test
    public void nakedPublicInstanceMethodTest() throws ParseException {
        expectation("${this.nakedPublicMethod} == " + nakedPublicMethod(), this).validate();
    }

    @Test
    public void nakedPrivateInstanceMethodTest() throws ParseException {
        expectation("${this.nakedPrivateMethod} == " + nakedPrivateMethod(), this).validate();
    }

    @Test
    public void booleanHasInstanceMethodTest() throws ParseException {
        expectation("${this.flair} || 1!=1", this).validate();
    }

    @Test
    public void booleanIsInstanceMethodTest() throws ParseException {
        expectation("${this.attitude} || 1!=1", this).validate();
    }

    @Test
    public void instanceGetterTest() throws ParseException {
        expectation("${this.instanceMethod} == " + getInstanceMethod(), this).validate();
    }

    public double nakedPublicMethod() {
        return instanceVar;
    }

    private double nakedPrivateMethod() {
        return instanceVar;
    }

    public boolean hasFlair() {
        return true;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public boolean isAttitude() {
        return hasFlair();
    }

    public double getInstanceMethod() {
        return 789;
    }

    private static final int STATIC_VAR = 1234;

    private PerformanceExpectation expectation(String expression,Object instance, Object... arguments)
            throws ParseException
    {
        final ExpectationData data = new ExpectationData(-1, "testing", expression);
        int argNo = 0;
        if(instance != null) {
            data.addLocalVar("this", argNo++);
        }
        data.addLocalVar("first", argNo++);
        data.addLocalVar("second", argNo++);
        data.addLocalVar("third", argNo++);
        data.addLocalVar("fourth", argNo++);
        data.addLocalVar("fifth", argNo);
        return new PerformanceExpectation(PerformanceExpectationTest.class, data, instance, arguments);
    }
}
