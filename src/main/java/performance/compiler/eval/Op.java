package performance.compiler.eval;

public abstract class Op {
    public boolean booleanVal() {
        return false; //False is fine, since a false result ends in an AssertionError
    }

    public double doubleVal() {
        throw new UnsupportedOperationException();
    }
}
