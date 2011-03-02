package performance.runtime;


import performance.parser.ParseException;

public final class ThreadHelper
        implements MethodListener {

    private PerformanceExpectation first;

    @Override
    public void methodEnter(final Class clazz, final String name) {
        for(PerformanceExpectation listener = first; listener != null; listener = listener.next()) {
            listener.methodEnter(clazz, name);
        }
    }

    @Override
    public void methodNormalExit(final Class clazz, final String name) {
        for(PerformanceExpectation listener = first; listener != null; listener = listener.next()) {
            listener.methodNormalExit(clazz, name);
        }
    }

    @Override
    public void methodExceptionExit(final Class clazz, final String name) {
        for(PerformanceExpectation listener = first; listener != null; listener = listener.next()) {
            listener.methodExceptionExit(clazz, name);
        }
    }

    public Object beginExpectation(final Class ctxClass, final ExpectationData expectationData,
                                   final Object instance, final Object[] argumentValues) {
        try {
            first = new PerformanceExpectation(first, ctxClass, expectationData, instance, argumentValues);
            return first;
        } catch (ParseException e) {
            synchronized (System.err) {
                System.err.println("Error parsing: " + expectationData.expression() + " on " + ctxClass + "." + expectationData.methodName());
                e.printStackTrace();
            }
            return FAILED;
        }
    }

    public void endExpectation(final Object handle) {
        if (handle == first) {
            final PerformanceExpectation expected = first;
            first = expected.next();
            expected.validate();
        }
    }

    private static final Object FAILED = new Object();
}
