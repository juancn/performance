package performance.util;

import java.util.Comparator;
import java.util.Iterator;

public class MutableArray<T>
    implements Iterable<T>
{
    private final int capacityIncrement;
    private Object[] elements;
    private int size;

    public MutableArray() {
        this(16,16);
    }

    public MutableArray(int capacity, int capacityIncrement) {
        this.capacityIncrement = capacityIncrement;
        elements = new Object[capacity];
    }

    public void add(T value) {
        ensureCapacity();
        elements[size++] = value;
    }

    public T get(int index) {
        if(index < 0 || index >= size) throw new IndexOutOfBoundsException("index: " + index);
        return cast(elements[index]);
    }

    public T set(int index, T value) {
        if(index < 0 || index >= size) throw new IndexOutOfBoundsException("index: " + index);
        final T old = cast(elements[index]);
        elements[index] = value;
        return old;
    }

    public void insert(int index, T value) {
        if(index < 0 || index > size) throw new IndexOutOfBoundsException("index: " + index);
        ensureCapacity();
        System.arraycopy(elements, index, elements, index+1, size-index);
        elements[index] = value;
        ++size;
    }

    public T remove(int index) {
        if(index < 0 || index >= size) throw new IndexOutOfBoundsException("index: " + index);
        final T old = cast(elements[index]);
        System.arraycopy(elements, index+1, elements, index, size-index);
        --size;
        return old;
    }

    public int size() {
        return size;
    }

    public int binarySearch(T key, Comparator<? super T> comparator) {
        return binarySearch(key, new F.Identity<T>(), comparator);
    }

    public <S> int binarySearch(S key, F<T,S> mapping, Comparator<? super S> comparator) {
        int low = 0, high = size-1;
        while (low <= high) {
            final int mid = (low + high) / 2;
            final S midKey = mapping.apply(get(mid));
            final int cmp = comparator.compare(midKey, key);

            if (cmp < 0) {
                low = mid + 1;
            }  else if (cmp > 0) {
                high = mid - 1;
            }  else {
                return mid;
            }
        }

        return -(low + 1);
    }

    public void sort(Comparator<? super T> comparator) {
        sort(new F.Identity<T>(), comparator);
    }

    public <S> void sort(F<T,S> mapping, Comparator<? super S> comparator) {
        mergeSort(mapping, comparator, elements.clone(), elements, 0, size, 0);
    }

    private <S> void mergeSort(F<T,S> mapping, Comparator<? super S> comparator,
                                  Object[] src, Object[] dest,
                                  final int low,
                                  final int high,
                                  final int off)
    {
        final int length = high - low;
        //Insertion sort for small arrays
        if (length < 7) {
            for (int i = low; i < high; i++) {
                for (int j=i; j>low
                        && comparator.compare(mapping.apply(cast(dest[j - 1])), mapping.apply(cast(dest[j])))>0; j--) {
                    final Object tmp = dest[j];
                    dest[j] = dest[j-1];
                    dest[j-1] = tmp;
                }
            }
            return;
        }

        final int offLow  = low + off;
        final int offHigh = high + off;
        final int mid = (offLow + offHigh) / 2;

        //Recursive sort both halves
        mergeSort(mapping, comparator, dest, src, offLow, mid, -off);
        mergeSort(mapping, comparator, dest, src, mid, offHigh, -off);

        if (comparator.compare(mapping.apply(cast(src[mid - 1])),mapping.apply(cast(src[mid]))) <= 0) {
            System.arraycopy(src, offLow, dest, low, length);
            return;
        }

        //Merge sorted halves
        for(int i = low, p = offLow, q = mid; i < high; i++) {
            if (q >= offHigh || p < mid
                    /* Inlined for performance */
                    && comparator.compare(mapping.apply(cast(src[p])),mapping.apply(cast(src[q]))) <= 0) {
                dest[i] = src[p++];
            } else {
                dest[i] = src[q++];
            }
        }
    }


    @SuppressWarnings({"unchecked"})
    private T cast(Object v) {
        return (T) v;
    }

    private void ensureCapacity() {
        if(size == elements.length) {
            final Object[] expanded = new Object[elements.length+capacityIncrement];
            System.arraycopy(elements, 0, expanded, 0, elements.length);
            elements = expanded;
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            int cursor = -1;
            @Override
            public boolean hasNext() {
                return cursor+1 < size;
            }

            @Override
            public T next() {
                return get(++cursor);
            }

            @Override
            public void remove() {
                MutableArray.this.remove(cursor--);
            }
        };
    }
}
