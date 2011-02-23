package performance.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.FileOutputStream;
import java.io.IOException;
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
        if(className.startsWith("performance")) {
            return bytecode;
        }

        byte[] bytes;
        try {
            final ClassReader classReader = new ClassReader(bytecode);
            final ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS
//                    | ClassWriter.COMPUTE_FRAMES
            );
            final ClassVisitor visitor = new ClassTransformer(classWriter, className);
            classReader.accept(visitor, ClassReader.EXPAND_FRAMES);
            bytes = classWriter.toByteArray();
        } catch (Exception e) {
            System.out.println("*** ERROR PROCESSING:  " + className);
            e.printStackTrace();
            return bytecode;
        }

        if(className.equals("Test")) {
            try {
                FileOutputStream f = new FileOutputStream("/Users/juancn/X.class");
                f.write(bytes);
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        inst.addTransformer(new PerformanceAgent());
    }

}
