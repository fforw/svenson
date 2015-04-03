package org.svenson.info;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

class MethodUtil
{
    public static <T extends Annotation> T getAnnotation(Class<T> cls, Method... methods)
    {
        T annotation = null;
        for (Method method : methods)
        {
            if (method != null && (annotation = method.getAnnotation(cls)) != null)
            {
                return annotation;
            }
        }
        return null;
    }
}
