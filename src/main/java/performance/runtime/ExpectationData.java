package performance.runtime;

import performance.util.ComparableComparator;
import performance.util.F;
import performance.util.MutableArray;

/**
 * To keep the instrumentation in a single pass, method metadata is stored in an
 * instance of this class. This class contains the expression source and a summary
 * of the local variable table (names and indexes). This information is used
 * to resolve symbolic references to argument names.
 */
public class ExpectationData {
    private final int handle;
    private final String methodName;
    private final String expression;
    private final MutableArray<LocalVar> localVars;
    private int lastSize;

    public ExpectationData(int handle, String methodName, String expression) {
        this.handle = handle;
        this.methodName = methodName;
        this.expression = expression;
        this.localVars= new MutableArray<LocalVar>();
    }

    public int handle() {
        return handle;
    }

    public String methodName() {
        return methodName;
    }

    public String expression() {
        return expression;
    }


    public void addLocalVar(String name, int index)
    {
        localVars.add(new LocalVar(name, index));
    }

    public int localVarIndexOf(String name)
    {
        checkSorted();
        final int arrayIdx = localVars.binarySearch(name, VAR_NAME_MAPPING, ComparableComparator.<String>instance());
        return arrayIdx>0?localVars.get(arrayIdx).index:-1;
    }

    private void checkSorted() {
        if(lastSize != localVars.size()) {
            lastSize = localVars.size();
            localVars.sort(VAR_NAME_MAPPING, ComparableComparator.<String>instance());
        }
    }

    @Override
    public String toString() {
        checkSorted();
        return "{handle: " + handle + ", methodName: " + methodName + ", expression: '"
                + expression + "', locals: " + localVars + "}";
    }

    private static class LocalVar {
        final String name;
        final int index;

        private LocalVar(String name, int index) {
            this.name = name;
            this.index = index;
        }

        @Override
        public String toString() {
            return index + ":" + name;
        }
    }

    private static final F<LocalVar,String> VAR_NAME_MAPPING = new F<LocalVar, String>() {
        @Override
        public String apply(LocalVar x) {
            return x.name;
        }
    };
}
