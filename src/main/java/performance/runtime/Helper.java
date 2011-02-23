package performance.runtime;

@SuppressWarnings({"UnusedDeclaration"})
public final class Helper {

    public static void methodEnter(final String clazz, final String name) {
    }


    public static void methodNormalExit(final String clazz, final String name) {
    }

    public static void methodExceptionExit(final String clazz, final String name) {
    }

    public static Object beginExpectation(final String clazz, final String name, final String expression) {
        System.out.println("%%% Expecting: " + expression + " @ " + clazz + "." + name);
        return "HANDLE OF(" + expression + ")";
    }

    public static void endExpectation(final Object handle) {
        System.out.println("%%% End: " + handle);
    }
}
