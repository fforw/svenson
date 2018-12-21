package org.svenson.info.reflection;

import org.svenson.SvensonRuntimeException;
import org.svenson.info.Setter;

import java.lang.reflect.Method;

public class ReflectionSetter implements Setter {
    private final Method setterMethod;

    public ReflectionSetter(Method setterMethod) {
        this.setterMethod = setterMethod;
    }

    public boolean isWriteable() {
        return true;
    }

    public void set(Object target, Object value) {

        try {
            setterMethod.invoke(target, value);
        } catch (Throwable t) {
            throw new SvensonRuntimeException("Error setting value " + value + " on target " + target + " using " + setterMethod, t);
        }
    }
}
