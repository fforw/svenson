package org.svenson.info.methodhandle;

import org.svenson.SvensonRuntimeException;
import org.svenson.info.PostConstructInvoker;

import java.lang.invoke.MethodHandle;

class MethodHandlePostConstructInvoker implements PostConstructInvoker {

    private final MethodHandle handle;

    public MethodHandlePostConstructInvoker(MethodHandle handle) {
        this.handle = handle;
    }

    @Override
    public void invoke(Object o) {
        try {
            handle.invoke(o);
        } catch (Throwable throwable) {
            throw new SvensonRuntimeException(throwable);
        }
    }
}
