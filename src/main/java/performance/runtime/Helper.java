package performance.runtime;

import performance.util.MutableArray;

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

    private static final MutableArray<ExpectationData> expectations = new MutableArray<ExpectationData>();

    public static void methodEnter(final Class clazz, final String name) {
        if(enabled.get()) {
            try {
                enabled.set(false);
                threadHelper.get().methodEnter(clazz, name);
            } finally {
                enabled.set(true);
            }
        }
    }

    public static void methodNormalExit(final Class clazz, final String name) {
        if(enabled.get()) {
            try {
                enabled.set(false);
                threadHelper.get().methodNormalExit(clazz, name);
            } finally {
                enabled.set(true);
            }
        }
    }

    public static void methodExceptionExit(final Class clazz, final String name) {
        if(enabled.get()) {
            try {
                enabled.set(false);
                threadHelper.get().methodExceptionExit(clazz, name);
            } finally {
                enabled.set(true);
            }
        }
    }

    public static Object beginExpectation(final Class clazz, final Object instance, final Object[] argumentValues, final int handle) {
        return threadHelper.get().beginExpectation(clazz, getExpectationData(handle), instance, argumentValues);
    }

    public static void endExpectation(final Object handle) {
        threadHelper.get().endExpectation(handle);
    }

    public static ExpectationData newExpectationData(final String methodName, final String expression)
    {
        synchronized (expectations) {
            final ExpectationData expectationData = new ExpectationData(expectations.size(), methodName, expression);
            expectations.add(expectationData);
            return expectationData;
        }
    }

    private static ExpectationData getExpectationData(int handle)
    {
        return expectations.get(handle);
    }
}
