package org.svenson.info.reflectasm;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.svenson.info.Getter;

class ReflectasmGetter implements Getter {
    private final MethodAccess methodAccess;
    private final String name;

    public ReflectasmGetter(MethodAccess methodAccess, String name) {
        this.methodAccess = methodAccess;
        this.name = name;
    }

    @Override
    public boolean isReadable() {
        return true;
    }

    @Override
    public Object get(Object o) {
        return methodAccess.invoke(o, name);
    }
}
