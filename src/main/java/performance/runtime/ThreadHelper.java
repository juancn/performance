package performance.runtime;


import performance.parser.ParseException;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ThreadHelper
        implements MethodListener {
    private final Deque<PerformanceExpectation> listeners = new ArrayDeque<PerformanceExpectation>();

    @Override
    public void methodEnter(final Class clazz, final String name) {
        for (PerformanceExpectation listener : listeners) {
            listener.methodEnter(clazz, name);
        }
    }


    @Override
    public void methodNormalExit(final Class clazz, final String name) {
        for (PerformanceExpectation listener : listeners) {
            listener.methodNormalExit(clazz, name);
        }
    }

    @Override
    public void methodExceptionExit(final Class clazz, final String name) {
        for (PerformanceExpectation listener : listeners) {
            listener.methodExceptionExit(clazz, name);
        }
    }

    public Object beginExpectation(final Class clazz, final String name, final String expression) {
        try {
            final PerformanceExpectation expectation = new PerformanceExpectation(clazz, name, expression);
            listeners.add(expectation);
            return expectation;
        } catch (ParseException e) {
            System.err.println("Error parsing: " + expression + " on " + clazz + "." + name);
            return FAILED;
        }
    }

    public void endExpectation(final Object handle) {
        if (handle == listeners.peekLast()) {
            PerformanceExpectation expected = listeners.removeLast();
            expected.validate();
        }
    }

    private static final Object FAILED = new Object();
}
