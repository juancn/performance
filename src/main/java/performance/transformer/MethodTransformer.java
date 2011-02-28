package performance.transformer;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.Method;
import performance.annotation.Expect;
import performance.runtime.ExpectationData;
import performance.runtime.Helper;

class MethodTransformer extends AdviceAdapter {
    private final String className;
    private final String methodName;
    private final Label startFinally = new Label();

    private AnnotationCollector expectationAnnotation;
    private int localVar;
    private ExpectationData expectationData;

    public MethodTransformer(String className, MethodVisitor mv, int acc, String methodName, String desc) {
        super(mv, acc, methodName, desc);
        this.className = className;
        this.methodName = methodName;
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        if (expectationData != null) {
            expectationData.addLocalVar(name, index);
        }
        super.visitLocalVariable(name, desc, signature, start, end, index);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        if(desc.equals(EXPECT_DESCRIPTOR)) {
            expectationAnnotation = new AnnotationCollector(super.visitAnnotation(desc, visible), desc, visible);
            return expectationAnnotation;
        }
        return super.visitAnnotation(desc, visible);
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
        if(expectationAnnotation != null) {
            localVar = newLocal(OBJECT);
            expectationData = Helper.newExpectationData(methodName, String.valueOf(expectationAnnotation.getValue("value")));

            visitLdcInsn(Type.getObjectType(className));
            loadThisOrNull();
            loadArgArray();
            visitLdcInsn(expectationData.handle());

            invokeStatic(HELPER, BEGIN_EXPECTATION);
            storeLocal(localVar);
        }

        visitLdcInsn(Type.getObjectType(className));
        visitLdcInsn(methodName);
        invokeStatic(HELPER, METHOD_ENTER);
    }

    private void loadThisOrNull() {
        if ((methodAccess & ACC_STATIC) != 0) {
            visitInsn(ACONST_NULL);
        } else {
            loadThis();
        }
    }

    private void onFinally(int opcode) {
        visitLdcInsn(Type.getObjectType(className));
        visitLdcInsn(methodName);

        if (opcode == ATHROW) {
            invokeStatic(HELPER, METHOD_EXCEPTION_EXIT);
        } else {
            invokeStatic(HELPER, METHOD_NORMAL_EXIT);
        }

        if(expectationAnnotation != null) {
            loadLocal(localVar);
            invokeStatic(HELPER, END_EXPECTATION);
        }
    }


    private static final String EXPECT_DESCRIPTOR = Type.getDescriptor(Expect.class);

    private static final Type OBJECT = Type.getType(Object.class);
    private static final Type HELPER = Type.getType(Helper.class);

    private static final Method BEGIN_EXPECTATION = new Method("beginExpectation", "(Ljava/lang/Class;Ljava/lang/Object;[Ljava/lang/Object;I)Ljava/lang/Object;");
    private static final Method END_EXPECTATION = new Method("endExpectation", "(Ljava/lang/Object;)V");
    private static final Method METHOD_ENTER = new Method("methodEnter", "(Ljava/lang/Class;Ljava/lang/String;)V");
    private static final Method METHOD_NORMAL_EXIT = new Method("methodNormalExit","(Ljava/lang/Class;Ljava/lang/String;)V");
    private static final Method METHOD_EXCEPTION_EXIT = new Method("methodExceptionExit","(Ljava/lang/Class;Ljava/lang/String;)V");

}
