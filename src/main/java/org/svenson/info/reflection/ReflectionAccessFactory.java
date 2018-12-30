package org.svenson.info.reflection;

import org.svenson.info.*;

import java.lang.reflect.Method;

public class ReflectionAccessFactory implements AccessFactory {
    @Override
    public Getter createGetter(Method getterMethod) {
        return new ReflectionGetter(getterMethod);
    }

    @Override
    public Setter createSetter(Method setterMethod) {
        return new ReflectionSetter(setterMethod);
    }

    @Override
    public Adder createAdder(Method adderMethod) {
        return new ReflectionAdder(adderMethod);
    }

    @Override
    public PostConstructInvoker createPostConstructInvoker(Method postConstructMethod) {
        return new ReflectionPostConstructInvoker(postConstructMethod);
    }
}
