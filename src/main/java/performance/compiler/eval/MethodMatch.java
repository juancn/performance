package performance.compiler.eval;

public class MethodMatch extends Op {
    private final String classPattern;
    private final String mtdPattern;
    private int count;

    public MethodMatch(String classPattern, String mtdPattern) {
        this.classPattern = classPattern;
        this.mtdPattern = mtdPattern;
    }

    @Override
    public double doubleVal() {
        return count;
    }

    public void match(Class clazz, String mtdName)
    {
        if(matches(clazz)) {
            if(mtdName.equals(mtdPattern)) {
                ++count;
            }
        }
    }

    @SuppressWarnings({"ConstantConditions"})
    private boolean matches(Class clazz) {
        if(classPattern == null) {
            return true;
        }

        if(clazz == null) {
            return false;
        }

        if(clazz.getSimpleName().equals(classPattern)) {
            return true;
        }

        boolean result = false;
        result |= matches(clazz.getSuperclass());

        if (!result) {
            for (Class iface : clazz.getInterfaces()) {
                result |= matches(iface);
                if(result) break; //short circuit
            }
        }

        return result;
    }

    @Override
    public String toString() {

        return "#" + (classPattern==null?"<any>":classPattern) + "." + mtdPattern + "=" + count;
    }
}
