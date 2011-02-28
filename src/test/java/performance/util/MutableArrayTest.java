package performance.util;

import org.testng.annotations.Test;

import java.util.Comparator;
import java.util.Iterator;

public class MutableArrayTest {
    @Test
    public void basicTest()
    {
        final MutableArray<String> array = numberArray(15);
        for (int i = 0; i < 15; i++) {
            assert Integer.parseInt(array.get(i)) == i : "Unexpected element at " + i;
        }
    }

    @Test
    public void iteratorTest()
    {
        final MutableArray<String> array = numberArray(15);
        int index = 0;
        final Iterator<String> it = array.iterator();
        while (it.hasNext()) {
            String s = it.next();
            assert Integer.parseInt(s) == index : "Unexpected element at " + index;
            it.remove();
            index++;
        }
        assert array.size() == 0 : "Array should be empty";
    }

    @Test
    public void setTest()
    {
        final MutableArray<String> array = numberArray(15);
        for (int i = 0; i < 15; i++) {
            array.set(i, String.valueOf(2*i));
        }
        for (int i = 0; i < 15; i++) {
            assert Integer.parseInt(array.get(i)) == (2*i) : "Unexpected element at " + i;
        }
    }

    @Test
    public void insertTest()
    {
        final MutableArray<String> array = new MutableArray<String>();
        for (int i = 0; i < 15; i++) {
            array.insert(i, String.valueOf(i));
        }

        for (int i = 0; i < 15; i++) {
            assert Integer.parseInt(array.get(i)) == i : "Unexpected element at " + i;
        }
        array.insert(0,String.valueOf(-1));
        assert Integer.parseInt(array.get(0)) == -1 : "Unexpected element at " + 0;
        assert Integer.parseInt(array.get(1)) == 0 : "Unexpected element at " + 0;

    }

    @Test
    public void sortTest()
    {
        final MutableArray<String> array = numberArray(15);
        array.sort(ComparableComparator.<String>instance());
        assert "[0,1,10,11,12,13,14,2,3,4,5,6,7,8,9]".equals(array.toString()) : "Unexpected sort order";
    }

    @Test
    public void binarySearchTest()
    {
        final MutableArray<String> array = numberArray(15);
        final Comparator<String> comparator = ComparableComparator.<String>instance();
        array.sort(comparator);
        final int index = array.binarySearch("6", comparator);
        assert "6".equals(array.get(index)) : "Unexpected element";
    }

    @Test
    public void mappedSortTest()
    {
        final MutableArray<String> array = numberArray(15);
        //Sort first alphabetically then by numeric value
        array.sort(ComparableComparator.<String>instance());
        array.sort(STRING_TO_INT_MAPPING, ComparableComparator.<Integer>instance());
        assert "[0,1,2,3,4,5,6,7,8,9,10,11,12,13,14]".equals(array.toString()) : "Unexpected sort order";
    }

    @Test
    public void mappedBinarySearchTest()
    {
        final MutableArray<String> array = numberArray(15); //Array is sorted numerically
        final int index = array.binarySearch(6,
                STRING_TO_INT_MAPPING,
                ComparableComparator.<Integer>instance());
        assert "6".equals(array.get(index)) : "Unexpected element";
    }

    private MutableArray<String> numberArray(int size) {
        final MutableArray<String> array = new MutableArray<String>(3,3); //Small number to force resizes

        for (int i = 0; i < size; i++) {
            array.add(String.valueOf(i));
        }
        assert array.size() == size : "Unexpected array size: " + array.size();
        return array;
    }

    private static final F<String,Integer> STRING_TO_INT_MAPPING = new F<String, Integer>(){
        @Override
        public Integer apply(String x) {
            return Integer.parseInt(x);
        }
    };

}
