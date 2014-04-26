package org.svenson.info;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import org.svenson.JSONProperty;
import org.svenson.JSONReference;
import org.svenson.JSONTypeHint;
import org.svenson.converter.JSONConverter;
import org.svenson.converter.TypeConverter;
import org.svenson.converter.TypeConverterRepository;
import org.svenson.util.Util;

public class JavaObjectSupport extends AbstractObjectSupport
{
    private static final String ADDER_PREFIX = "add";

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private static final String ISSER_PREFIX = "is";


    public JavaObjectSupport()
    {
    }
    

    public JSONClassInfo createClassInfo(Class<?> cls)
    {
        Map<String, JavaObjectPropertyInfo> javaNameToInfo = new HashMap<String, JavaObjectPropertyInfo>();

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
                JavaObjectPropertyInfo pair = javaNameToInfo.get(javaPropertyName);
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
                    pair = new JavaObjectPropertyInfo(javaPropertyName, null, m);
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
                    JavaObjectPropertyInfo pair = javaNameToInfo.get(javaPropertyName);
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
                        javaNameToInfo.put(javaPropertyName, new JavaObjectPropertyInfo(javaPropertyName,
                            m, null));
                    }
                }
                else if (name.startsWith(ISSER_PREFIX))
                {
                    String javaPropertyName = propertyName(name, ISSER_PREFIX.length());
                    JavaObjectPropertyInfo pair = javaNameToInfo.get(javaPropertyName);
                    if (pair != null)
                    {
                        pair.setGetterMethod(m);
                    }
                    else
                    {
                        javaNameToInfo.put(javaPropertyName, new JavaObjectPropertyInfo(javaPropertyName,
                            m, null));
                    }
                }
            }
            else if (name.startsWith(ADDER_PREFIX) && m.getParameterTypes().length == 1)
            {
                String javaPropertyName = propertyName(name, ADDER_PREFIX.length());
                JavaObjectPropertyInfo pair = javaNameToInfo.get(javaPropertyName);
                if (pair != null)
                {
                    if ( pair.getSetterMethod() == null)
                    {
                        pair.setAdderMethod(m);
                    }
                }
                else
                {
                    JavaObjectPropertyInfo newInfo = new JavaObjectPropertyInfo(javaPropertyName, null, null);
                    newInfo.setAdderMethod(m);
                    javaNameToInfo.put(javaPropertyName, newInfo);
                }
            }
        }

        HashMap<String, JavaObjectPropertyInfo> propertyInfos = new HashMap<String, JavaObjectPropertyInfo>(javaNameToInfo.size());

        for (Map.Entry<String, JavaObjectPropertyInfo> e : javaNameToInfo.entrySet())
        {
            String jsonPropertyName = e.getKey();
            JavaObjectPropertyInfo propertyInfo = e.getValue();

            Method getterMethod = propertyInfo.getGetterMethod();
            Method setterMethod = propertyInfo.getSetterMethod();
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
