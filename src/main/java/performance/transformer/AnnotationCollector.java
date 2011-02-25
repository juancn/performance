package performance.transformer;

import org.objectweb.asm.AnnotationVisitor;
import performance.util.ComparableComparator;
import performance.util.F;
import performance.util.MutableArray;

class AnnotationCollector implements AnnotationVisitor {
    private final AnnotationVisitor av;
    private final String descriptor;
    private final boolean visible;
    private final MutableArray<Value> values = new MutableArray<Value>();
    private int lastSize;

    public AnnotationCollector(AnnotationVisitor av, String descriptor, boolean visible) {
        this.av = av;
        this.descriptor = descriptor;
        this.visible = visible;
    }

    @Override
    public void visit(String name, Object value) {
        values.add(new Value(name, value));
        av.visit(name, value);
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        av.visitEnum(name, desc, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        return av.visitAnnotation(name, desc);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return av.visitArray(name);
    }

    @Override
    public void visitEnd() {
        av.visitEnd();
    }

    public Object getValue(final String name) {
        if(lastSize != values.size()) {
            lastSize = values.size();
            values.sort(KEY_FROM_VALUE, ComparableComparator.<String>instance());
        }
        final int index = values.binarySearch(name, KEY_FROM_VALUE, ComparableComparator.<String>instance());
        return values.get(index).value;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public boolean isVisible() {
        return visible;
    }

    private static class Value
    {
        final String key;
        final Object value;

        private Value(String key, Object value) {
            this.key = key;
            this.value = value;
        }
    }

    private static final F<Value,String> KEY_FROM_VALUE = new F<Value, String>() {
        @Override
        public String apply(Value x) {
            return x.key;
        }
    };
}
