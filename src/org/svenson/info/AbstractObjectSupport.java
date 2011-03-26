package org.svenson.info;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class AbstractObjectSupport implements ObjectSupport
{

    protected static <T extends Annotation> T getAnnotation(Class<T> cls, Method... methods)
    {
        T anno = null;
        for (Method method : methods)
        {
            if (method != null && (anno = method.getAnnotation(cls)) != null)
            {
                return anno;
            }
        }
        return null;
    }

    protected static String propertyName(String name, int prefixLen)
    {
        return Introspector.decapitalize(name.substring(prefixLen));
    }

}
