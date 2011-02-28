package performance.util;

/**
 * Simple interface that represents a function.
 * @param <S> domain of the function
 * @param <T> image of the function
 */
public interface F<S,T> {
    T apply(S x);

    static class Identity<T>
        implements F<T,T>
    {
        @Override
        public T apply(T x) {
            return x;
        }

        @SuppressWarnings({"unchecked"})
        public static <T> Identity<T> instance() {
            return INSTANCE;
        }

        private static final Identity INSTANCE = new Identity();
    }
}
