package performance.compiler.eval;

public class DynamicValue extends Op {
    private final String expression;

    public DynamicValue(String expression) {
        this.expression = expression;
    }

}
