package org.svenson.info.methodhandle;

import org.svenson.SvensonRuntimeException;
import org.svenson.info.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class MethodHandleAccessFactory implements AccessFactory {
    @Override
    public Getter createGetter(Method method) {
        return new MethodHandleGetter(unreflect(method));


    }

    private MethodHandle unreflect(Method method) {
        try {
            return MethodHandles.lookup().unreflect(method);
        } catch (IllegalAccessException e) {
            throw new SvensonRuntimeException("can't transform method into MethodHandle " + method.getName(), e);
        }
    }

    @Override
    public Setter createSetter(Method method) {
        return new MethodHandleSetter(unreflect(method));
    }

    @Override
    public Adder createAdder(Method method) {
        return new MethodHandleAdder(unreflect(method));
    }

    @Override
    public PostConstructInvoker createPostConstructInvoker(Method method) {
        return new MethodHandlePostConstructInvoker(unreflect(method));
    }

}
