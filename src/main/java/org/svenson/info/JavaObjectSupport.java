package org.svenson.info;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class JavaObjectSupport implements ObjectSupport {


    public JavaObjectSupport() {
    }

    public JSONClassInfo createClassInfo(final Class<?> cls) {
        JSONClassInfoCollector visitor = new JSONClassInfoCollector(cls);

        for (Constructor<?> c : cls.getConstructors()) {
            visitor.visitConstructor(c);
        }
        for (Method m : cls.getMethods()) {
            visitor.visitMethod(m);
        }
        return visitor.toClassInfo();
    }

}
