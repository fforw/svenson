package org.svenson.reflectasm.reflectasm;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.svenson.JSONProperty;
import org.svenson.JSONReference;
import org.svenson.JSONTypeHint;
import org.svenson.converter.JSONConverter;
import org.svenson.info.AbstractObjectSupport;
import org.svenson.info.JSONClassInfo;
import org.svenson.info.JSONPropertyInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public class ReflectAsmObjectSupport
    extends AbstractObjectSupport
{

    private static final String ADDER_PREFIX = "add";

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private static final String ISSER_PREFIX = "is";

    public JSONClassInfo createClassInfo(Class<?> cls)
    {

        Map<String, ReflectAsmPropertyInfo> javaNameToInfo = new HashMap<String, ReflectAsmPropertyInfo>();

        MethodAccess methodAccess = MethodAccess.get(cls);

        for (Method m : cls.getMethods())
        {
            String name = m.getName();

            if ((m.getModifiers() & Modifier.PUBLIC) == 0 || name.equals("getClass"))
            {
                continue;
            }

            if (name.startsWith(SETTER_PREFIX) && m.getParameterTypes().length == 1)
            {
                String javaPropertyName = propertyName(name, SETTER_PREFIX.length());
                ReflectAsmPropertyInfo pair = javaNameToInfo.get(javaPropertyName);
                if (pair != null)
                {
                    Method existing = pair.getSetterMethod();

                    if (existing == null || isOveriding(m.getDeclaringClass(), existing.getDeclaringClass()))
                    {
                        pair.setSetterMethod(m);
                        pair.setAdderMethod(null);
                    }

                }
                else
                {
                    pair = new ReflectAsmPropertyInfo(javaPropertyName, methodAccess);
                    pair.setSetterMethod(m);
                    javaNameToInfo.put(javaPropertyName, pair);
                }

                Class<?>[] parameterTypes;
                Class<?> paramType;
                if ((parameterTypes = m.getParameterTypes()).length == 1 &&
                    (paramType = parameterTypes[0]).isArray())
                {
                    pair.setTypeHint(paramType.getComponentType());
                }
            }
            else if (m.getParameterTypes().length == 0 && !m.getReturnType().equals(void.class))
            {
                if (name.startsWith(GETTER_PREFIX))
                {
                    String javaPropertyName = propertyName(name, GETTER_PREFIX.length());
                    ReflectAsmPropertyInfo pair = javaNameToInfo.get(javaPropertyName);
                    if (pair != null)
                    {
                        Method existing = pair.getGetterMethod();

                        if (existing == null || isOveriding(m.getDeclaringClass(), existing.getDeclaringClass()))
                        {
                            pair.setGetterMethod(m);
                        }
                    }
                    else
                    {
                        ReflectAsmPropertyInfo methods = new ReflectAsmPropertyInfo(javaPropertyName, methodAccess);
                        methods.setGetterMethod(m);
                        javaNameToInfo.put(javaPropertyName, methods);

                    }
                }
                else if (name.startsWith(ISSER_PREFIX))
                {
                    String javaPropertyName = propertyName(name, ISSER_PREFIX.length());
                    ReflectAsmPropertyInfo pair = javaNameToInfo.get(javaPropertyName);
                    if (pair != null)
                    {
                        pair.setGetterMethod(m);
                    }
                    else
                    {
                        ReflectAsmPropertyInfo methods = new ReflectAsmPropertyInfo(javaPropertyName, methodAccess);
                        methods.setGetterMethod(m);
                        javaNameToInfo.put(javaPropertyName, methods);
                    }
                }
            }
            else if (name.startsWith(ADDER_PREFIX) && m.getParameterTypes().length == 1)
            {
                String javaPropertyName = propertyName(name, ADDER_PREFIX.length());
                ReflectAsmPropertyInfo pair = javaNameToInfo.get(javaPropertyName);
                if (pair != null)
                {
                    if ( pair.getSetterMethod() == null)
                    {
                        pair.setAdderMethod(m);
                        pair.setSetterMethod(null);
                    }
                }
                else
                {
                    ReflectAsmPropertyInfo methods = new ReflectAsmPropertyInfo(javaPropertyName, methodAccess);
                    methods.setAdderMethod(m);
                    javaNameToInfo.put(javaPropertyName, methods);
                }
            }
        }

        HashMap<String, ReflectAsmPropertyInfo> propertyInfos = new HashMap<String, ReflectAsmPropertyInfo>(javaNameToInfo.size());

        for (Map.Entry<String, ReflectAsmPropertyInfo> e : javaNameToInfo.entrySet())
        {
            String jsonPropertyName = e.getKey();
            ReflectAsmPropertyInfo propertyInfo = e.getValue();

            Method getterMethod = propertyInfo.getGetterMethod();
            Method setterMethod = propertyInfo.getSetterMethod();
            Method adderMethod = propertyInfo.getAdderMethod();

            JSONProperty jsonProperty = MethodUtil.getAnnotation(JSONProperty.class, getterMethod,
                setterMethod);

            if (jsonProperty != null)
            {
                if (jsonProperty.value().length() > 0)
                {
                    jsonPropertyName = jsonProperty.value();
                }

                propertyInfo.setIgnore(jsonProperty.ignore());
                propertyInfo.setIgnoreIfNull(jsonProperty.ignoreIfNull());
                propertyInfo.setReadOnly(jsonProperty.readOnly());
                propertyInfo.setPriority(jsonProperty.priority());
            }
            propertyInfo.setJsonName(jsonPropertyName);

            JSONConverter converterAnno = MethodUtil.getAnnotation(JSONConverter.class, getterMethod, setterMethod);

            if (converterAnno != null)
            {
                if (converterAnno.name().length() > 0)
                {
                    propertyInfo.setConverterName(converterAnno.name());
                }
                else
                {
                    propertyInfo.setConverterType(converterAnno.type());
                }
            }

            JSONReference refAnno = MethodUtil.getAnnotation(JSONReference.class, getterMethod, setterMethod);

            if (refAnno != null)
            {
                propertyInfo.setLinkIdProperty(refAnno.idProperty());
            }

            JSONTypeHint typeHintAnno = MethodUtil.getAnnotation(JSONTypeHint.class, getterMethod,
                setterMethod);
            Class<?>[] parameterTypes;
            Class paramType;
            if (typeHintAnno != null)
            {
                propertyInfo.setTypeHint(typeHintAnno.value());
            }

            int getterMethodIndex = -1;
            int setterMethodIndex = -1;
            int adderMethodIndex = -1;

            if (getterMethod != null)
            {
                getterMethodIndex = methodAccess.getIndex(getterMethod.getName(), 0);
            }
            if (setterMethod != null)
            {
                setterMethodIndex = methodAccess.getIndex(setterMethod.getName(), 1);
            }
            if (adderMethod != null)
            {
                adderMethodIndex = methodAccess.getIndex(adderMethod.getName(), 1);
            }

            propertyInfo.setAdderMethodIndex(adderMethodIndex);
            propertyInfo.setGetterMethodIndex(getterMethodIndex);
            propertyInfo.setSetterMethodIndex(setterMethodIndex);
            propertyInfo.clearMethodReferences();

            propertyInfos.put(jsonPropertyName, propertyInfo);

        }


        return new JSONClassInfo(cls, propertyInfos);
    }
    /**
     * Returns <code>true</code> if class a is a subclass of class b or if b is <code>null</code>.
     * @param a
     * @param b
     * @return
     */
    static boolean isOveriding(Class<?> a, Class<?> b)
    {
        if (b == null)
        {
            return true;
        }

        Class<?> cls = a;
        Class<?> superClass;
        while ( (superClass = cls.getSuperclass()) != null)
        {
            cls = superClass;
            if (cls.equals(b))
            {
                return true;
            }
        }

        return false;
    }

}
