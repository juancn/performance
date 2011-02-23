package performance.transformer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import performance.annotation.Expect;
import performance.runtime.Helper;

import java.util.ArrayList;
import java.util.List;

class MethodTransformer extends AdviceAdapter {
    private final String className;
    private final String methodName;
    private final List<AnnotationCollector> annotations = new ArrayList<AnnotationCollector>();

    //Helper labels
    private final Label startFinally = new Label();


    public MethodTransformer(String className, MethodVisitor mv, int acc, String methodName, String desc) {
        super(mv, acc, methodName, desc);
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor av = super.visitAnnotation(desc, visible);
        if(desc.equals(EXPECT_DESCRIPTOR)) {
            final AnnotationCollector annotationCollector = new AnnotationCollector(av, desc, visible);
            annotations.add(annotationCollector);
            av = annotationCollector;
        }
        return av;
    }


    @Override
    public void visitCode() {
        super.visitCode();
        visitLabel(startFinally);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {

        final Label endFinally = new Label();
        visitTryCatchBlock(startFinally, endFinally, endFinally, null);
        visitLabel(endFinally);
        onFinally(ATHROW);
        visitInsn(ATHROW);
        super.visitMaxs(maxStack, maxLocals);
    }

    @Override
    protected void onMethodExit(int opcode) {
        if(opcode!=ATHROW) {
            onFinally(opcode);
        }
    }


    @Override
    protected void onMethodEnter()
    {
        for (AnnotationCollector annotation : annotations) {
            if(annotation.getDescriptor().equals(EXPECT_DESCRIPTOR)) {

                visitLdcInsn(className.replace('/', '.'));
                visitLdcInsn(methodName);
                visitLdcInsn(String.valueOf(annotation.getValue("value")));
                visitMethodInsn(INVOKESTATIC, HELPER_CLASS, "beginExpectation", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;");
                visitInsn(POP);
            }
        }

        visitLdcInsn(className.replace('/', '.'));
        visitLdcInsn(methodName);
        visitMethodInsn(INVOKESTATIC, HELPER_CLASS, "methodEnter", "(Ljava/lang/String;Ljava/lang/String;)V");
    }

    private void onFinally(int opcode) {
        visitLdcInsn(className.replace('/', '.'));
        visitLdcInsn(methodName);
        String method = opcode == ATHROW? "methodExceptionExit" : "methodNormalExit";
        visitMethodInsn(INVOKESTATIC, HELPER_CLASS, method, "(Ljava/lang/String;Ljava/lang/String;)V");

        for (AnnotationCollector annotation : annotations) {
            if(annotation.getDescriptor().equals(EXPECT_DESCRIPTOR)) {
                visitLdcInsn(className.replace('/', '.'));
                visitLdcInsn(methodName);
                visitLdcInsn(String.valueOf(annotation.getValue("value")));
                visitMethodInsn(INVOKESTATIC, HELPER_CLASS, "endExpectation", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Object;)V");
            }
        }
    }

    private static final String EXPECT_DESCRIPTOR = Type.getDescriptor(Expect.class);
    private static final String HELPER_CLASS = Helper.class.getCanonicalName().replace('.', '/');
}
