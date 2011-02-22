package performance.transformer;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

@SuppressWarnings({"UnusedDeclaration"})
public class ClassTransformer
    implements ClassFileTransformer {
    @Override
    public byte[] transform(final ClassLoader loader,
                            final String className,
                            final Class<?> classBeingRedefined,
                            final ProtectionDomain protectionDomain,
                            final byte[] bytecode) throws IllegalClassFormatException
    {
        final ClassReader classReader = new ClassReader(bytecode);
        final ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        final ClassVisitor visitor = new ClassAdapter(classWriter) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                final MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
                return new EnteringAdapter(className, methodVisitor, access, name, desc);
            }
        };
        classReader.accept(visitor, 0);
        return classWriter.toByteArray();
    }

    static class EnteringAdapter extends AdviceAdapter {
        private final String className;
        private final String methodName;

        public EnteringAdapter(String className, MethodVisitor mv, int acc, String methodName, String desc) {
            super(mv, acc, methodName, desc);
            this.className = className;
            this.methodName = methodName;
        }

        protected void onMethodEnter()
        {
            visitLdcInsn(methodName);
            visitLdcInsn(className);
            visitMethodInsn(INVOKESTATIC, "performance/runtime/Helper", "methodEnter", "(Ljava/lang/String;Ljava/lang/String;)V");
        }
    }
}
