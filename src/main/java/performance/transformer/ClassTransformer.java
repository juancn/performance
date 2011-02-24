package performance.transformer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import performance.annotation.Metric;
import performance.util.MutableArray;

class ClassTransformer extends ClassAdapter {
    private final String className;
    private MutableArray<AnnotationCollector> metrics = new MutableArray<AnnotationCollector>();

    public ClassTransformer(ClassWriter classWriter, String className) {
        super(classWriter);
        this.className = className;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor av = super.visitAnnotation(desc, visible);
        if(METRIC_DESCRIPTOR.equals(desc)) {
            AnnotationCollector annotationCollector = new AnnotationCollector(av, desc, visible);
            metrics.add(annotationCollector);
            av = annotationCollector;
        }
        return av;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        final MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        return new MethodTransformer(className, methodVisitor, access, name, desc);
    }

   private static final String METRIC_DESCRIPTOR = Type.getDescriptor(Metric.class);
}
