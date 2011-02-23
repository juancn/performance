package performance.runtime;

public interface MethodListener {
    void methodEnter(final String clazz, final String name);
    void methodNormalExit(final String clazz, final String name);
    void methodExceptionExit(final String clazz, final String name);
}
