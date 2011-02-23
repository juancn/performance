package performance.runtime;

public class PerformanceExpectation
    implements MethodListener
{
    private final String classname;
    private final String methodName;
    private final String expression;

    public PerformanceExpectation(final String classname, final String methodName, final String expression) {
        this.classname = classname;
        this.methodName = methodName;
        this.expression = expression;
    }

    @Override
    public void methodEnter(String clazz, String name) {
        System.out.println("PerformanceExpectation.methodEnter");
    }

    @Override
    public void methodNormalExit(String clazz, String name) {
        System.out.println("PerformanceExpectation.methodNormalExit");
    }

    @Override
    public void methodExceptionExit(String clazz, String name) {
        System.out.println("PerformanceExpectation.methodExceptionExit");
    }
}
