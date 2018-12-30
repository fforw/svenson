package org.svenson.info;

import org.svenson.info.methodhandle.MethodHandleAccessFactory;
import org.svenson.info.reflectasm.ReflectasmAccessFactory;
import org.svenson.info.reflection.ReflectionAccessFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class JavaObjectSupport implements ObjectSupport {
    private final AccessFactory factory;

    public JavaObjectSupport() {
        this(new ReflectionAccessFactory());
    }

    public JavaObjectSupport(AccessFactory factory) {
        this.factory = factory;
    }

    public JSONClassInfo createClassInfo(final Class<?> cls) {
        JSONClassInfoCollector visitor = new JSONClassInfoCollector(factory, cls);

        for (Constructor<?> c : cls.getConstructors()) {
            visitor.visitConstructor(c);
        }
        for (Method m : cls.getMethods()) {
            visitor.visitMethod(m);
        }
        return visitor.toClassInfo();
    }

}
