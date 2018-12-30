package org.svenson.info.methodhandle;

import org.svenson.SvensonRuntimeException;
import org.svenson.info.Getter;

import java.lang.invoke.MethodHandle;

class MethodHandleGetter implements Getter {
    private final MethodHandle handle;

    public MethodHandleGetter(MethodHandle handle) {
        this.handle = handle;
    }

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
}
