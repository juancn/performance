package performance.runtime;


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
        final PerformanceExpectation expectation = new PerformanceExpectation(clazz, name, expression);
        listeners.add(expectation);
        return expectation;
    }

    public void endExpectation(final Object handle) {
        final PerformanceExpectation received = (PerformanceExpectation) handle;
        PerformanceExpectation expected = listeners.removeLast();
        assert expected == received;
    }
}
