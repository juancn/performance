package performance.runtime;

import performance.compiler.SimpleGrammar;
import performance.compiler.TokenType;
import performance.compiler.eval.BinaryOpVisitor;
import performance.compiler.eval.DynamicValue;
import performance.parser.ParseException;
import performance.parser.ast.Expr;
import performance.compiler.eval.MethodMatch;
import performance.compiler.eval.Op;

import java.util.List;

public class PerformanceExpectation
    implements MethodListener
{
    private final PerformanceExpectation next;
    private final Class ctxClass;
    private final ExpectationData expectationData;

    private Op validation;
    private List<MethodMatch> methodMatches;
    private List<DynamicValue> dynamicValues;

    public PerformanceExpectation(final PerformanceExpectation next,
                                  final Class ctxClass,
                                  final ExpectationData expectationData,
                                  final Object instance,
                                  final Object[] argumentValues)
            throws ParseException
    {
        this.next = next;
        this.ctxClass = ctxClass;
        this.expectationData = expectationData;

        final Expr<TokenType> expr = SimpleGrammar.parse(expectationData.expression());
        final BinaryOpVisitor visitor = new BinaryOpVisitor();
        expr.visit(visitor);
        validation = visitor.getOpTree();
        methodMatches = visitor.getMethodMatches();
        dynamicValues = visitor.getDynamicValues();
        for (final DynamicValue dynamicValue : dynamicValues) {
            dynamicValue.resolve(ctxClass, instance, argumentValues, expectationData);
        }
    }

    @Override
    public void methodEnter(Class clazz, String name) {
        for (MethodMatch methodMatch : methodMatches) {
            methodMatch.match(clazz, name);
        }
    }

    @Override
    public void methodNormalExit(Class clazz, String name) {
    }

    @Override
    public void methodExceptionExit(Class clazz, String name) {

    }

    public void validate() {
        if(!validation.booleanVal()) {
            final StringBuilder sb = new StringBuilder();
            sb.append("Method '")
                    .append(ctxClass.getName()).append(".").append(expectationData.methodName())
                    .append("' did not fulfil: ")
                    .append(expectationData.expression());
            sb.append("\n\t Matched: ").append(methodMatches);
            sb.append("\n\t Dynamic: ").append(dynamicValues);
            throw new AssertionError(sb.toString());
        }
    }

    public PerformanceExpectation next() {
        return next;
    }

}
