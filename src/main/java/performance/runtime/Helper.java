package performance.runtime;

public final class Helper {

    public static void methodEnter(final String clazz, final String name) {
        System.out.println(clazz + "." + name);
    }
}
