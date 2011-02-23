package performance.runtime;


import performance.parser.ParseException;

import java.util.ArrayDeque;
import java.util.Deque;

public final class ThreadHelper
        implements MethodListener {
    private final Deque<PerformanceExpectation> listeners = new ArrayDeque<PerformanceExpectation>();

    @Override
    public void methodEnter(final String clazz, final String name) {
        for (PerformanceExpectation listener : listeners) {
            listener.methodEnter(clazz, name);
        }
    }


    @Override
    public void methodNormalExit(final String clazz, final String name) {
        for (PerformanceExpectation listener : listeners) {
            listener.methodNormalExit(clazz, name);
        }
    }

    @Override
    public void methodExceptionExit(final String clazz, final String name) {
        for (PerformanceExpectation listener : listeners) {
            listener.methodExceptionExit(clazz, name);
        }
    }

    public Object beginExpectation(final String clazz, final String name, final String expression) {
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
        if (handle != FAILED) {
            final PerformanceExpectation received = (PerformanceExpectation) handle;
            PerformanceExpectation expected = listeners.removeLast();
            assert expected == received;
        }
    }

    private static final Object FAILED = new Object();
}
