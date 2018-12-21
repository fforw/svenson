package org.svenson.info.reflection;

import org.svenson.SvensonRuntimeException;
import org.svenson.info.Getter;

import java.lang.reflect.Method;

public class ReflectionGetter implements Getter {
    private final Method getter;

    public ReflectionGetter(Method getter) {
        this.getter = getter;
    }

    public boolean isReadable() {
        return true;
    }

    public Object get(Object target) {
        try {
            return getter.invoke(target);
        } catch (Throwable t) {
            throw new SvensonRuntimeException("Error getting value from target " + target + " using " + getter, t);
        }
    }
}
