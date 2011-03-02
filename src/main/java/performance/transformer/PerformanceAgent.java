package performance.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;

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
        if(className.startsWith("performance/runtime")
                || className.startsWith("sun/reflect/")
                || className.equals("java/lang/ThreadLocal")
                /* Check the class version. Don't instrument older classes. */
                || (bytecode[6] == 0 && bytecode[7] < 49)) {
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

    public static void premain(String agentArgs, Instrumentation inst)
            throws ClassNotFoundException, UnmodifiableClassException, IOException {
        System.out.println("*** Performance Agent Started ***");
        long start = System.currentTimeMillis();
        if(inst.isRetransformClassesSupported()) {
            inst.addTransformer(new PerformanceAgent(), true);
            for (Class aClass : inst.getAllLoadedClasses()) {
                if(inst.isModifiableClass(aClass) && !aClass.isInterface()
                        && include(aClass)) {
                    inst.retransformClasses(aClass);
                }
            }
        } else {
            inst.addTransformer(new PerformanceAgent(), false);
            System.err.println("*** WARNING: JVM does not support retransforming. Classes already loaded will not be instrumented.");
        }

        long end = System.currentTimeMillis();
        System.out.println("\t startup took: " + (end-start) + "ms.");
    }

    private static boolean include(Class aClass) {
        final String name = aClass.getName();
        return name.startsWith("java.io.")
                || name.startsWith("java.util.")
                || included.contains(name);
    }
    private static Set<String> included = new HashSet<String>();
    static {
        included.add("java.lang.String");
        included.add("java.lang.Math");
        included.add("java.lang.StrictMath");
        included.add("java.lang.StringBuffer");
        included.add("java.lang.StringBuilder");
        included.add("java.lang.System");
        included.add("java.lang.Runtime");
    }
}
