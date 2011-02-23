package performance.runtime;

@SuppressWarnings({"UnusedDeclaration"})
public final class Helper {
    private static final ThreadLocal<Boolean> enabled = new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return Boolean.TRUE;
        }
    };

    private static final ThreadLocal<ThreadHelper> threadHelper = new ThreadLocal<ThreadHelper>() {
        @Override
        protected ThreadHelper initialValue() {
            return new ThreadHelper();
        }
    };

    public static void methodEnter(final String clazz, final String name) {
        if(enabled.get()) {
            try {
                enabled.set(false);
                threadHelper.get().methodEnter(clazz, name);
            } finally {
                enabled.set(true);
            }
        }
    }

    public static void methodNormalExit(final String clazz, final String name) {
        if(enabled.get()) {
            try {
                enabled.set(false);
                threadHelper.get().methodNormalExit(clazz, name);
            } finally {
                enabled.set(true);
            }
        }
    }

    public static void methodExceptionExit(final String clazz, final String name) {
        if(enabled.get()) {
            try {
                enabled.set(false);
                threadHelper.get().methodExceptionExit(clazz, name);
            } finally {
                enabled.set(true);
            }
        }
    }

    public static Object beginExpectation(final String clazz, final String name, final String expression) {
        return threadHelper.get().beginExpectation(clazz, name, expression);
    }

    public static void endExpectation(final Object handle) {
        threadHelper.get().endExpectation(handle);
    }
}
