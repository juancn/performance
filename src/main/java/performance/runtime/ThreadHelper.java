package performance.runtime;


public final class ThreadHelper
        implements MethodListener {

    @Override
    public void methodEnter(final String clazz, final String name) {
    }


    @Override
    public void methodNormalExit(final String clazz, final String name) {
    }

    @Override
    public void methodExceptionExit(final String clazz, final String name) {
    }

    public Object beginExpectation(final String clazz, final String name, final String expression) {
        System.out.println("%%% Expecting: " + expression + " @ " + clazz + "." + name);
        return "HANDLE OF(" + expression + ")";
    }

    public void endExpectation(final Object handle) {
        System.out.println("%%% End: " + handle);
    }
}
