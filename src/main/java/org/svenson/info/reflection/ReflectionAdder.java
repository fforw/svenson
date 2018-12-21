package org.svenson.info.reflection;

import org.svenson.SvensonRuntimeException;
import org.svenson.info.Adder;

import java.lang.reflect.Method;

public class ReflectionAdder implements Adder {
    private final Method adderMethod;

    public ReflectionAdder(Method adderMethod) {
        this.adderMethod = adderMethod;
    }

    public boolean isWriteable() {
        return true;
    }

    public void add(Object target, Object value) {
        try {
            adderMethod.invoke(target, value);
        } catch (Throwable t) {
            throw new SvensonRuntimeException("Error adding value " + value + " to target " + target + " using " + adderMethod, t);
        }
    }
}
