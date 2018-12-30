package org.svenson.info.methodhandle;

import org.svenson.SvensonRuntimeException;
import org.svenson.info.Setter;

import java.lang.invoke.MethodHandle;

class MethodHandleSetter implements Setter {
    private final MethodHandle handle;

    public MethodHandleSetter(MethodHandle handle) {
        this.handle = handle;
    }

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
}
