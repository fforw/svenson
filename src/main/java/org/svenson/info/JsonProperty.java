package org.svenson.info;

import org.svenson.JSONProperty;
import org.svenson.JSONReference;
import org.svenson.JSONTypeHint;
import org.svenson.converter.JSONConverter;
import org.svenson.converter.TypeConverter;
import org.svenson.converter.TypeConverterRepository;
import org.svenson.info.reflection.ReflectionAdder;
import org.svenson.info.reflection.ReflectionGetter;
import org.svenson.info.reflection.ReflectionSetter;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static org.svenson.info.JavaBeanMethod.*;
import static org.svenson.info.Reflection.resolveAnnotation;

final class JsonProperty {
    private final AccessFactory factory;
    private final Class<?> declaringClass;
    private final String name;
    private final Class<?> type;
    private final JavaBeanMethod readMethod;

    public JsonProperty(AccessFactory factory, Class<?> declaringClass, String name, Class<?> type) {
        this.factory = factory;
        this.declaringClass = declaringClass;
        this.name = name;
        this.type = type;
        readMethod = (Boolean.class == type || type == Boolean.TYPE) ? IS : GET;
    }

    public String getName() {
        return name;
    }

    public Class<?> getType() {
        return type;
    }

    private Method getReadMethod() {
        try {
            return declaringClass.getMethod(readMethod.toMethodName(name));
        } catch (NoSuchMethodException ex) {
            return null;
        }
    }

    private Method getSetterMethod() {
        return findMethod(SET.toMethodName(this.name), type);
    }

    private Method findMethod(String methodName, Class<?>... params) {
        Class<?> current = declaringClass;
        while (current != null) {
            try {
                return current.getDeclaredMethod(methodName, params);
            } catch (NoSuchMethodException ex) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private Method getAdderMethod() {
        Method direct = findAdderMethod(type);
        if (direct != null) {
            return direct;
        }
        Class<?> addType = resolveTypeHint();
        if (addType != null) {
            return findAdderMethod(addType);
        }
        return null;
    }

    private Method findAdderMethod(Class<?> addType) {
        final String adderMethod = ADD.toMethodName(name);
        final Method resolved = findMethod(adderMethod, addType);
        if (resolved != null) {
            return resolved;
        }
        if (adderMethod.endsWith("s")) {
            return findMethod(adderMethod.substring(0, adderMethod.length() - 1), addType);
        }
        return null;
    }

    private Class<?> resolveTypeHint() {
        final Method readMethod = getReadMethod();
        JSONTypeHint typeHint = Reflection.resolveAnnotation(JSONTypeHint.class, readMethod, getSetterMethod());

        if (typeHint != null) {
            return typeHint.value();
        } else if (type != null && Iterable.class.isAssignableFrom(type)) {
            if (readMethod != null) {
                final Type returnType = readMethod.getGenericReturnType();
                if (returnType instanceof ParameterizedType) {
                    ParameterizedType type = (ParameterizedType) returnType;
                    return (Class<?>) type.getActualTypeArguments()[0];
                }
            }
        } else if (type != null && type.isArray()) {
            return type.getComponentType();
        }
        return null;
    }


    public JavaObjectPropertyInfo toPropertyInfo() {
        final Method getterMethod = getReadMethod();
        final Method setterMethod = getSetterMethod();
        final Method adderMethod = getAdderMethod();
        String jsonName = name;
        boolean ignore = false;
        boolean ignoreIfNull = false;
        boolean readOnly = false;
        int priority = 0;
        String linkIdProperty;

        JSONProperty jsonProperty = resolveAnnotation(JSONProperty.class, getterMethod, setterMethod, adderMethod);


        if (jsonProperty != null) {
            if (jsonProperty.value().length() > 0) {
                jsonName = jsonProperty.value();
            }
            ignore = jsonProperty.ignore();
            ignoreIfNull = jsonProperty.ignoreIfNull();
            readOnly = jsonProperty.readOnly();
            priority = jsonProperty.priority();
        }

        JSONReference refAnno = resolveAnnotation(JSONReference.class, getterMethod, setterMethod, adderMethod);
        if (refAnno != null) {
            linkIdProperty = refAnno.idProperty();
        } else {
            linkIdProperty = null;
        }
        Class<?> typeHint = resolveTypeHint();
        if (typeHint == null && adderMethod != null) {
            typeHint = adderMethod.getParameterTypes()[0];
        }

        PropertyTypeConverterResolver converterResolver = converterResolver(getterMethod, setterMethod);
        Getter getter = getterMethod != null ? factory.createGetter(getterMethod) : new UnreadableGetter(name);
        Setter setter = setterMethod != null ? factory.createSetter(setterMethod) : new UnwriteableSetter(name);
        Adder adder = setterMethod==null && adderMethod!= null ? factory.createAdder(adderMethod) : new UnwriteableAdder(name);

        return new JavaObjectPropertyInfo(
                name,
                getter,
                setter,
                adder,
                type,
                typeHint,
                ignore,
                ignoreIfNull,
                readOnly,
                jsonName,
                linkIdProperty,
                priority, converterResolver);
    }

    private PropertyTypeConverterResolver converterResolver(final Method getterMethod, final Method setterMethod) {
        final JSONConverter converterAnno = resolveAnnotation(JSONConverter.class, getterMethod, setterMethod);
        if(converterAnno == null){
            return new UnresolvablePropertyTypeConverterResolver();
        }
        final String name = converterAnno.name();
        if (name.length() == 0) {
            return new ByTypePropertyTypeConverterResolver(converterAnno.type());
        }
        return new ByIdPropertyTypeConverterResolver(name);
    }

    public JsonProperty merge(JsonProperty jsonProperty) {
        if (getReadMethod().getReturnType().equals(jsonProperty.getType())) {
            return jsonProperty;
        }
        return this;
    }

    private static class UnresolvablePropertyTypeConverterResolver implements PropertyTypeConverterResolver {
        public TypeConverter resolve(TypeConverterRepository typeConverterRepository) {
            return null;
        }
    }

    private static class ByTypePropertyTypeConverterResolver implements PropertyTypeConverterResolver {
        private final Class<? extends TypeConverter> type;

        public ByTypePropertyTypeConverterResolver(Class<? extends TypeConverter> type) {
            this.type = type;
        }

        public TypeConverter resolve(TypeConverterRepository typeConverterRepository) {
            if (typeConverterRepository != null) {
                return typeConverterRepository.getConverterByType(type);
            }
            return null;
        }
    }

    private static class ByIdPropertyTypeConverterResolver implements PropertyTypeConverterResolver {
        private final String name;

        public ByIdPropertyTypeConverterResolver(String name) {
            this.name = name;
        }

        public TypeConverter resolve(TypeConverterRepository typeConverterRepository) {
            if (typeConverterRepository != null) {
                return typeConverterRepository.getConverterById(name);
            }
            return null;
        }
    }
}
