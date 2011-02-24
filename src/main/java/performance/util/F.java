package performance.util;

public interface F<S,T> {
    T apply(S x);

    static class Identity<T>
        implements F<T,T>
    {
        @Override
        public T apply(T x) {
            return x;
        }
    }
}
