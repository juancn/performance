package performance.util;

import java.util.Comparator;

public class ComparableComparator<T extends Comparable<? super T>> implements Comparator<T> {

    @Override
    public int compare(T o1, T o2) {
        return o1.compareTo(o2);
    }

    @SuppressWarnings({"unchecked"})
    public static <T extends Comparable<? super T>> Comparator<T> instance() {
        return INSTANCE;
    }
    private static final ComparableComparator INSTANCE = new ComparableComparator();
}
