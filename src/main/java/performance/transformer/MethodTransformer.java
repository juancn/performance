package performance.transformer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;
import performance.annotation.Expect;
import performance.runtime.Helper;
import performance.util.MutableArray;

class MethodTransformer extends AdviceAdapter {
    private final String className;
    private final String methodName;
    private final MutableArray<ExpectationInfo> expectations = new MutableArray<ExpectationInfo>();

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
            expectations.add(new ExpectationInfo(annotationCollector));
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
        for (ExpectationInfo expectation : expectations) {
            expectation.localVar = newLocal(Type.getType(Object.class));

            visitLdcInsn(Type.getObjectType(className));
            visitLdcInsn(methodName);
            visitLdcInsn(String.valueOf(expectation.annotation.getValue("value")));

            invokeStatic(HELPER, BEGIN_EXPECTATION);
            storeLocal(expectation.localVar);
        }

        visitLdcInsn(Type.getObjectType(className));
        visitLdcInsn(methodName);
        invokeStatic(HELPER, METHOD_ENTER);
    }

    private void onFinally(int opcode) {
        visitLdcInsn(Type.getObjectType(className));
        visitLdcInsn(methodName);

        if (opcode == ATHROW) {
            invokeStatic(HELPER, METHOD_EXCEPTION_EXIT);
        } else {
            invokeStatic(HELPER, METHOD_NORMAL_EXIT);
        }

        for (ExpectationInfo expectation : expectations) {
            loadLocal(expectation.localVar);
            invokeStatic(HELPER, END_EXPECTATION);
        }
    }

    private static class ExpectationInfo {
        final AnnotationCollector annotation;
        int localVar = -1;

        private ExpectationInfo(AnnotationCollector annotation) {
            this.annotation = annotation;
        }
    }

    private static final String EXPECT_DESCRIPTOR = Type.getDescriptor(Expect.class);

    private static final Type HELPER = Type.getType(Helper.class);
    private static final Method BEGIN_EXPECTATION = new Method("beginExpectation", "(Ljava/lang/Class;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;");
    private static final Method END_EXPECTATION = new Method("endExpectation", "(Ljava/lang/Object;)V");
    private static final Method METHOD_ENTER = new Method("methodEnter", "(Ljava/lang/Class;Ljava/lang/String;)V");
    private static final Method METHOD_NORMAL_EXIT = new Method("methodNormalExit","(Ljava/lang/Class;Ljava/lang/String;)V");
    private static final Method METHOD_EXCEPTION_EXIT = new Method("methodExceptionExit","(Ljava/lang/Class;Ljava/lang/String;)V");

}
