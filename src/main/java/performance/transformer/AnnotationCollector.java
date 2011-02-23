package performance.transformer;

import org.objectweb.asm.AnnotationVisitor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class AnnotationCollector implements AnnotationVisitor {
    private final AnnotationVisitor av;
    private final String descriptor;
    private final boolean visible;
    private Map<String, Object> values = new HashMap<String, Object>();

    public AnnotationCollector(AnnotationVisitor av, String descriptor, boolean visible) {
        this.av = av;
        this.descriptor = descriptor;
        this.visible = visible;
    }

    @Override
    public void visit(String name, Object value) {
        values.put(name, value);
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
        return values.get(name);
    }

    public String getDescriptor() {
        return descriptor;
    }

    public boolean isVisible() {
        return visible;
    }
}
