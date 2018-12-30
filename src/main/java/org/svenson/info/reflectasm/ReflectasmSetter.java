package org.svenson.info.reflectasm;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.svenson.info.Setter;

class ReflectasmSetter implements Setter {
    private final MethodAccess methodAccess;
    private final String name;
    private final Class<?>[] parameterTypes;

    public ReflectasmSetter(MethodAccess methodAccess, String name, Class<?>[] parameterTypes) {
        this.methodAccess = methodAccess;
        this.name = name;
        this.parameterTypes = parameterTypes;
    }

    @Override
    public boolean isWriteable() {
        return true;
    }

    @Override
    public void set(Object object, Object value) {
        methodAccess.invoke(object, name, parameterTypes, value);
    }
}
