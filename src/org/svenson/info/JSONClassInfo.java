package org.svenson.info;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.svenson.JSONProperty;
import org.svenson.JSONReference;
import org.svenson.JSONTypeHint;

import com.sun.xml.internal.bind.v2.model.core.ClassInfo;

/**
 * Encapsulates svensons knowledge about a class. Provides a constructor method. 
 * 
 * @author fforw at gmx dot de
 *
 */
public class JSONClassInfo
{

    private static final String ADDER_PREFIX = "add";

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private static final String ISSER_PREFIX = "is";

    protected static ConcurrentMap<Class<?>, JSONClassInfo> holders;
    static
    {
        clear();
    }

    private Class cls;

    protected Map<String, JSONPropertyInfo> propertyInfos;


    protected JSONClassInfo(Class cls)
    {
        this.cls = cls;
    }
    
    private synchronized void init()
    {
        Map<String, JSONPropertyInfo> propertyInfos = new HashMap<String, JSONPropertyInfo>();

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
                JSONPropertyInfo pair = propertyInfos.get(javaPropertyName);
                if (pair != null)
                {
                    pair.setSetterMethod(m);
                }
                else
                {
                    pair = new JSONPropertyInfo(javaPropertyName, null, m);
                    propertyInfos.put(javaPropertyName, pair);
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
                    JSONPropertyInfo pair = propertyInfos.get(javaPropertyName);
                    if (pair != null)
                    {
                        pair.setGetterMethod(m);
                    }
                    else
                    {
                        propertyInfos.put(javaPropertyName, new JSONPropertyInfo(javaPropertyName,
                            m, null));
                    }
                }
                else if (name.startsWith(ISSER_PREFIX))
                {
                    String javaPropertyName = propertyName(name, ISSER_PREFIX.length());
                    JSONPropertyInfo pair = propertyInfos.get(javaPropertyName);
                    if (pair != null)
                    {
                        pair.setGetterMethod(m);
                    }
                    else
                    {
                        propertyInfos.put(javaPropertyName, new JSONPropertyInfo(javaPropertyName,
                            m, null));
                    }
                }
            }
            else if (name.startsWith(ADDER_PREFIX) && m.getParameterTypes().length == 1)
            {
                String javaPropertyName = propertyName(name, ADDER_PREFIX.length());
                JSONPropertyInfo pair = propertyInfos.get(javaPropertyName);
                if (pair != null)
                {
                    pair.setAdderMethod(m);
                }
                else
                {
                    JSONPropertyInfo newInfo = new JSONPropertyInfo(javaPropertyName, null, null);
                    newInfo.setAdderMethod(m);
                    propertyInfos.put(javaPropertyName, newInfo);
                }
            }
        }

        this.propertyInfos = new HashMap<String, JSONPropertyInfo>();

        for (Map.Entry<String, JSONPropertyInfo> e : propertyInfos.entrySet())
        {
            String jsonPropertyName = e.getKey();
            JSONPropertyInfo propertyInfo = e.getValue();

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
            propertyInfo.setJsonName(jsonPropertyName);

            JSONReference refAnno = getAnnotation(JSONReference.class, getterMethod, setterMethod);

            if (refAnno != null)
            {
                propertyInfo.setLinkIdProperty(refAnno.idProperty());
            }

            JSONTypeHint typeHintAnno = getAnnotation(JSONTypeHint.class, getterMethod,
                setterMethod);
            Class<?>[] parameterTypes;
            Class paramType;
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


    public JSONPropertyInfo getPropertyInfo(String jsonPropertyName)
    {
        return propertyInfos.get(jsonPropertyName);
    }


    public Set<String> getPropertyNames()
    {
        return propertyInfos.keySet();
    }


    public Collection<JSONPropertyInfo> getPropertyInfos()
    {
        return propertyInfos.values();
    }


    /**
     * Returns the {@link ClassInfo} for the given class.
     * 
     * @param cls   class
     * @return
     */
    public static JSONClassInfo forClass(Class cls)
    {
        JSONClassInfo holder = new JSONClassInfo(cls);
        JSONClassInfo existing = holders.putIfAbsent(cls, holder);
        if (existing != null)
        {
            return existing;
        }
        else
        {
            holder.init();
            return holder;
        }
    }


    /**
     * Releases/clears all class infos.
     */
    public static void clear()
    {
        holders = new ConcurrentHashMap<Class<?>, JSONClassInfo>();
    }

    @Override
    public String toString()
    {
        return super.toString() + " cls = " + cls + ", propertyInfos = " + propertyInfos;
    }

    
}
