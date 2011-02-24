package performance.runtime;

public interface MethodListener {
    void methodEnter(final Class clazz, final String name);
    void methodNormalExit(final Class clazz, final String name);
    void methodExceptionExit(final Class clazz, final String name);
}
