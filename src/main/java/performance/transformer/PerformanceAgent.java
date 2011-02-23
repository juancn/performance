package performance.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

@SuppressWarnings({"UnusedDeclaration"})
public class PerformanceAgent
    implements ClassFileTransformer {
    @Override
    public byte[] transform(final ClassLoader loader,
                            final String className,
                            final Class<?> classBeingRedefined,
                            final ProtectionDomain protectionDomain,
                            final byte[] bytecode) throws IllegalClassFormatException
    {
        if(className.startsWith("performance/runtime")) {
            return bytecode;
        }

        byte[] bytes;
        try {
            final ClassReader classReader = new ClassReader(bytecode);
            final ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
            final ClassVisitor visitor = new ClassTransformer(classWriter, className);
            classReader.accept(visitor, ClassReader.EXPAND_FRAMES);
            bytes = classWriter.toByteArray();
        } catch (Exception e) {
            synchronized (System.err) {
                System.err.println("*** ERROR INSTRUMENTING:  " + className);
                e.printStackTrace();
            }
            return bytecode;
        }
        return bytes;
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new PerformanceAgent());
    }

}
