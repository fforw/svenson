package org.svenson.info.reflection;

import org.svenson.SvensonRuntimeException;
import org.svenson.info.PostConstructInvoker;

import java.lang.reflect.Method;

public class ReflectionPostConstructInvoker implements PostConstructInvoker {
    private final Method postConstruct;

    public ReflectionPostConstructInvoker(Method postConstruct) {
        this.postConstruct = postConstruct;
    }

    @Override
    public void invoke(Object object) {
        try {
            postConstruct.invoke(object);
        } catch (Throwable t) {
            throw new SvensonRuntimeException("Error executing PostConstruct at target " + object + " using " + postConstruct, t);
        }
    }
}
