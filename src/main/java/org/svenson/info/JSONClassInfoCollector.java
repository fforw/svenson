package org.svenson.info;

import org.svenson.JSONParameter;
import org.svenson.JSONParameters;
import org.svenson.JSONTypeHint;

import javax.annotation.PostConstruct;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

final class JSONClassInfoCollector {
    private final Class<?> cls;
    private final Map<String, JsonProperty> javaBeanProperties = new HashMap<String, JsonProperty>();
    private Constructor<?> constructor;
    private Class<?> constructorTypeHint;
    private Method postConstructMethod;

    public JSONClassInfoCollector(Class<?> cls) {
        this.cls = cls;
    }

    public void visitConstructor(Constructor<?> c) {

        int wildCardIndex = -1;
        Annotation[][] parameterAnnotations = c.getParameterAnnotations();

        for (int i = 0, parameterAnnotationsLength = parameterAnnotations.length; i < parameterAnnotationsLength;
             i++) {
            Annotation[] annotations = parameterAnnotations[i];
            for (Annotation annotation : annotations) {
                if (annotation instanceof JSONParameter) {
                    constructor = c;
                } else if (annotation instanceof JSONParameters) {
                    if (!Map.class.isAssignableFrom(c.getParameterTypes()[i])) {
                        throw new IllegalStateException("@JSONParameters annotation must be on a constructor map parameter");
                    }
                    constructor = c;
                    wildCardIndex = i;
                }
                if (annotation instanceof JSONTypeHint) {
                    Class<?>[] parameterTypes = c.getParameterTypes();
                    if (wildCardIndex == i) {
                        if (parameterTypes.length != 1) {
                            throw new IllegalStateException("@JSONParameters/@JSONTypeHint combination must only have one map parameter");
                        }
                        if (!Map.class.isAssignableFrom(parameterTypes[0])) {
                            throw new IllegalStateException("@JSONParameters/@JSONTypeHint combination must be on a constructor map parameter");
                        }
                        constructorTypeHint = ((JSONTypeHint) annotation).value();
                    }
                }
            }
        }
    }

    public void visitMethod(Method method) {

        if (!isPublicDeclaredMethod(method)) {
            return;
        }

        if (method.getAnnotation(PostConstruct.class) != null) {
            if (method.getParameterTypes().length != 0) {
                throw new IllegalStateException("@PostConstruct methods can't have parameters: " + method);
            }

            postConstructMethod = method;
            return;
        }

        for (JavaBeanMethod jbMethod : JavaBeanMethod.values()) {
            if (jbMethod.matches(method)) {
                final String name = jbMethod.toPropertyName(method);
                if (javaBeanProperties.containsKey(name)) {
                    final JsonProperty property = javaBeanProperties.get(name);
                    javaBeanProperties.put(name, property.merge(new JsonProperty(cls, name, jbMethod.type(method))));
                } else {
                    javaBeanProperties.put(name, new JsonProperty(cls, name, jbMethod.type(method)));
                }
            }
        }
    }


    public JSONClassInfo toClassInfo() {
        return new JSONClassInfo(cls, getPropertyInfos(), constructor, constructorTypeHint, postConstructMethod);
    }

    private static boolean isPublicDeclaredMethod(Method m) {
        return !Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers()) && !m.isBridge() && !m.getName().equals("getClass");
    }

    private Map<String, JSONPropertyInfo> getPropertyInfos() {
        final Map<String, JSONPropertyInfo> result = new HashMap<String, JSONPropertyInfo>();

        for (final JsonProperty property : javaBeanProperties.values()) {
            final JavaObjectPropertyInfo value = property.toPropertyInfo();
            result.put(value.getJsonName() != null ? value.getJsonName() : value.getJavaPropertyName(), value);
        }

        return result;
    }

}
