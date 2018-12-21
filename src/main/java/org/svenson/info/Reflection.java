package org.svenson.info;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

class Reflection {
    public static <T extends Annotation> T resolveAnnotation(Class<T> cls, Method... methods) {
        T annotation = null;
        for (Method method : methods) {
            if (method != null && (annotation = method.getAnnotation(cls)) != null) {
                return annotation;
            }
        }
        return null;
    }
}
