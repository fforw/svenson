package org.svenson.info.reflectasm;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.svenson.info.PostConstructInvoker;

class ReflectasmPostConstructInvoker implements PostConstructInvoker {


    private final MethodAccess methodAccess;
    private final String name;

    public ReflectasmPostConstructInvoker(MethodAccess methodAccess, String name) {
        this.methodAccess = methodAccess;
        this.name = name;
    }

    @Override
    public void invoke(Object object) {
        methodAccess.invoke(object, name);
    }
}
