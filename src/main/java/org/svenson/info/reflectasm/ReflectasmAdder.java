package org.svenson.info.reflectasm;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.svenson.info.Adder;

class ReflectasmAdder implements Adder {
    private final MethodAccess methodAccess;
    private final String name;
    private final Class<?>[] parameterTypes;

    public ReflectasmAdder(MethodAccess methodAccess, String name, Class<?>[] parameterTypes) {
        this.methodAccess = methodAccess;
        this.name = name;
        this.parameterTypes = parameterTypes;
    }

    @Override
    public boolean isWriteable() {
        return true;
    }

    @Override
    public void add(Object object, Object value) {
        methodAccess.invoke(object, name, parameterTypes, value);
    }
}
