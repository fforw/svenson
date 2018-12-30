package org.svenson.info.methodhandle;

import org.svenson.SvensonRuntimeException;
import org.svenson.info.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

public class MethodHandleAccessFactory implements AccessFactory {
    @Override
    public Getter createGetter(Method method) {
        final MethodHandle handle = unreflect(method);
        return new Getter() {
            @Override
            public boolean isReadable() {
                return true;
            }

            @Override
            public Object get(Object o) {
                try {
                    return handle.invoke(o);
                } catch (Throwable throwable) {
                    throw new SvensonRuntimeException(throwable);
                }
            }
        };


    }

    private MethodHandle unreflect(Method getterMethod) {
        try {
            return MethodHandles.lookup().unreflect(getterMethod);
        } catch (IllegalAccessException e) {
            throw new SvensonRuntimeException("can't transform method into MethodHandle " + getterMethod.getName(), e);
        }
    }

    @Override
    public Setter createSetter(Method method) {
        final MethodHandle handle = unreflect(method);
        return new Setter() {
            @Override
            public boolean isWriteable() {
                return true;
            }

            @Override
            public void set(Object o, Object val) {
                try {
                    handle.invoke(o, val);
                } catch (Throwable throwable) {
                    throw new SvensonRuntimeException(throwable);
                }
            }
        };
    }

    @Override
    public Adder createAdder(Method method) {
        final MethodHandle handle = unreflect(method);
        return new Adder() {
            @Override
            public boolean isWriteable() {
                return true;
            }

            @Override
            public void add(Object o, Object val) {
                try {
                    handle.invoke(o, val);
                } catch (Throwable throwable) {
                    throw new SvensonRuntimeException(throwable);
                }
            }
        };
    }

    @Override
    public PostConstructInvoker createPostConstructInvoker(Method method) {
        final MethodHandle handle = unreflect(method);
        return new PostConstructInvoker() {

            @Override
            public void invoke(Object o) {
                try {
                    handle.invokeExact(o);
                } catch (Throwable throwable) {
                    throw new SvensonRuntimeException(throwable);
                }
            }
        };
    }
}
