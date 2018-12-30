package org.svenson.info.methodhandle;

import org.svenson.SvensonRuntimeException;
import org.svenson.info.Adder;

import java.lang.invoke.MethodHandle;

class MethodHandleAdder implements Adder {
    private final MethodHandle handle;

    public MethodHandleAdder(MethodHandle handle) {
        this.handle = handle;
    }

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
}
