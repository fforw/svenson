package org.svenson.util;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.svenson.JSONParseException;
import org.svenson.JSONProperty;
import org.svenson.JSONReference;
import org.svenson.JSONTypeHint;

public class ClassInfo
{

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private static final String ISSER_PREFIX = "is";

    private static ConcurrentMap<Class<?>, ClassInfo> holders;
    static
    {
        clear();
    }
    
    private Class cls;

    private Map<String, PropertyInfo> propertyInfos;

    public ClassInfo(Class cls)
    {
        this.cls = cls;
    }

    public synchronized void init()
    {
        if (this.propertyInfos != null)
        {
            return;
        }
        
        Map<String, PropertyInfo> propertyInfos = new HashMap<String, PropertyInfo>();

        for (Method m : cls.getMethods())
        {
            String name = m.getName();


            if (name.startsWith(SETTER_PREFIX) && m.getParameterTypes().length == 1)
            {
                String javaPropertyName = propertyName(name, SETTER_PREFIX.length());
                PropertyInfo pair = propertyInfos.get(javaPropertyName);
                if (pair != null)
                {
                    pair.setSetterMethod(m);
                }
                else
                {
                    propertyInfos.put(javaPropertyName, new PropertyInfo(javaPropertyName, null, m));
                }
            }
            else if (m.getParameterTypes().length == 0 && !m.getReturnType().equals(void.class))
            {
                if (name.startsWith(GETTER_PREFIX) && !name.equals("getClass"))
                {
                    String javaPropertyName = propertyName(name, GETTER_PREFIX.length());
                    PropertyInfo pair = propertyInfos.get(javaPropertyName);
                    if (pair != null)
                    {
                        pair.setGetterMethod(m);
                    }
                    else
                    {
                        propertyInfos.put(javaPropertyName, new PropertyInfo(javaPropertyName, m, null));
                    }
                }
                else if (name.startsWith(ISSER_PREFIX))
                {
                    String javaPropertyName = propertyName(name, ISSER_PREFIX.length());
                    PropertyInfo pair = propertyInfos.get(javaPropertyName);
                    if (pair != null)
                    {
                        pair.setGetterMethod(m);
                    }
                    else
                    {
                        propertyInfos.put(javaPropertyName, new PropertyInfo(javaPropertyName, m, null));
                    }
                }
            }
        }

        this.propertyInfos = new HashMap<String, PropertyInfo>();

        for (Map.Entry<String, PropertyInfo> e : propertyInfos.entrySet())
        {
            String jsonPropertyName = e.getKey();
            PropertyInfo propertyInfo = e.getValue();

            Method getterMethod = propertyInfo.getGetterMethod();
            Method setterMethod = propertyInfo.getSetterMethod();
            JSONProperty jsonProperty = getAnnotation(JSONProperty.class, getterMethod,
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
            }

            JSONReference refeAnno = getAnnotation(JSONReference.class, getterMethod,
                setterMethod);
            
            propertyInfo.setLinkedProperty(refeAnno != null);
            
            JSONTypeHint typeHintAnno = getAnnotation(JSONTypeHint.class, getterMethod, setterMethod);
            if (typeHintAnno != null)
            {
                propertyInfo.setTypeHint(typeHintAnno.value());
            }

            this.propertyInfos.put(jsonPropertyName, propertyInfo);
        }

    }


    private static <T extends Annotation> T getAnnotation(Class<T> cls, Method readMethod,
        Method writeMethod)
    {
        T anno = null;
        if (readMethod != null)
        {
            anno = readMethod.getAnnotation(cls);
        }
        if (anno == null && writeMethod != null)
        {
            anno = writeMethod.getAnnotation(cls);
        }
        return anno;
    }


    private static String propertyName(String name, int prefixLen)
    {
        return Introspector.decapitalize(name.substring(prefixLen));
    }


        
    public PropertyInfo getPropertyInfo(String jsonPropertyName)
    {
        return propertyInfos.get(jsonPropertyName);
    }


    public Set<String> getPropertyNames()
    {
        return propertyInfos.keySet();
    }

    public static ClassInfo forClass(Class cls)
    {
        ClassInfo holder = new ClassInfo(cls);
        ClassInfo existing = holders.putIfAbsent(cls, holder);
        if (existing != null)
        {
            holder = existing;
        }
        
        holder.init();
        return holder;
    }
    
    /**
     * Release all class infos.
     * 
     */
    public static void clear()
    {
        holders = new ConcurrentHashMap<Class<?>, ClassInfo>();
    }
    
}
