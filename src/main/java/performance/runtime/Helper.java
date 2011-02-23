package performance.runtime;

@SuppressWarnings({"UnusedDeclaration"})
public final class Helper {
    private static final ThreadLocal<ThreadHelper> threadHelper = new ThreadLocal<ThreadHelper>() {
        @Override
        protected ThreadHelper initialValue() {
            return new ThreadHelper();
        }
    };

    public static void methodEnter(final String clazz, final String name) {
        threadHelper.get().methodEnter(clazz, name);
    }

    public static void methodNormalExit(final String clazz, final String name) {
        threadHelper.get().methodNormalExit(clazz, name);
    }

    public static void methodExceptionExit(final String clazz, final String name) {
        threadHelper.get().methodExceptionExit(clazz, name);
    }

    public static Object beginExpectation(final String clazz, final String name, final String expression) {
        return threadHelper.get().beginExpectation(clazz, name, expression);
    }

    public static void endExpectation(final Object handle) {
        threadHelper.get().endExpectation(handle);
    }
}
