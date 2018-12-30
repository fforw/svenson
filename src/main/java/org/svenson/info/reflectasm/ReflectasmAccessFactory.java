package org.svenson.info.reflectasm;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.svenson.info.*;

import java.lang.reflect.Method;

public class ReflectasmAccessFactory implements AccessFactory {


    @Override
    public Getter createGetter(final Method getterMethod) {
        final MethodAccess methodAccess = MethodAccess.get(getterMethod.getDeclaringClass());
        final String name = getterMethod.getName();
        return new ReflectasmGetter(methodAccess, name);
    }

    @Override
    public Setter createSetter(final Method method) {
        final MethodAccess methodAccess = MethodAccess.get(method.getDeclaringClass());
        final String name = method.getName();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        return new ReflectasmSetter(methodAccess, name, parameterTypes);
    }

    @Override
    public Adder createAdder(Method method) {
        final MethodAccess methodAccess = MethodAccess.get(method.getDeclaringClass());
        final String name = method.getName();
        final Class<?>[] parameterTypes = method.getParameterTypes();
        return new ReflectasmAdder(methodAccess, name, parameterTypes);
    }

    @Override
    public PostConstructInvoker createPostConstructInvoker(Method postConstructMethod) {
        final MethodAccess methodAccess = MethodAccess.get(postConstructMethod.getDeclaringClass());
        final String name = postConstructMethod.getName();
        return new ReflectasmPostConstructInvoker(methodAccess, name);
    }

}
