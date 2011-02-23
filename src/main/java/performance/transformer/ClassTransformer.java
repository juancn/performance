package performance.transformer;

import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

class ClassTransformer extends ClassAdapter {
    private final String className;

    public ClassTransformer(ClassWriter classWriter, String className) {
        super(classWriter);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        final MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
        return new MethodTransformer(className, methodVisitor, access, name, desc);
    }

}
