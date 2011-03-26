package org.svenson.info;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.svenson.JSONProperty;
import org.svenson.JSONReference;
import org.svenson.JSONTypeHint;
import org.svenson.converter.JSONConverter;
import org.svenson.converter.TypeConverter;
import org.svenson.converter.TypeConverterRepository;

public class JavaObjectSupport implements ObjectSupport
{
    private static final String ADDER_PREFIX = "add";

    private static final String SETTER_PREFIX = "set";

    private static final String GETTER_PREFIX = "get";

    private static final String ISSER_PREFIX = "is";


    private TypeConverterRepository typeConverterRepository;
    protected ConcurrentMap<Class<?>, ClassInfoHolder> holders;

    public JavaObjectSupport()
    {
        this(null);
    }
    
    public JavaObjectSupport(TypeConverterRepository typeConverterRepository)
    {
        this.typeConverterRepository = typeConverterRepository;
        clear();
    }

    public JSONClassInfo forClass(Class<?> cls)
    {
        ClassInfoHolder holder = new ClassInfoHolder(cls);
        ClassInfoHolder existing = holders.putIfAbsent(cls, holder);
        if (existing != null)
        {
            holder = existing;
        }
        
        return holder.getInfo();
    }


    /**
     * Releases/clears all class infos.
     */
    public void clear()
    {
        holders = new ConcurrentHashMap<Class<?>, ClassInfoHolder>();
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
    
    public class ClassInfoHolder
    {
        private Class cls;
        private volatile JSONClassInfo classInfo;

        public ClassInfoHolder(Class cls)
        {
            this.cls = cls;
        }
        
        public JSONClassInfo getInfo()
        {
            if (classInfo == null)
            {
                synchronized(this)
                {
                    if (classInfo == null)
                    {
                        classInfo = createClassInfo();
                    }
                }
            }
            return classInfo;
        }

        private JSONClassInfo createClassInfo()
        {
            Map<String, JSONPropertyInfo> javaNameToInfo = new HashMap<String, JSONPropertyInfo>();

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
                    JSONPropertyInfo pair = javaNameToInfo.get(javaPropertyName);
                    if (pair != null)
                    {
                        pair.setSetterMethod(m);
                    }
                    else
                    {
                        pair = new JSONPropertyInfo(javaPropertyName, null, m);
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
                        JSONPropertyInfo pair = javaNameToInfo.get(javaPropertyName);
                        if (pair != null)
                        {
                            pair.setGetterMethod(m);
                        }
                        else
                        {
                            javaNameToInfo.put(javaPropertyName, new JSONPropertyInfo(javaPropertyName,
                                m, null));
                        }
                    }
                    else if (name.startsWith(ISSER_PREFIX))
                    {
                        String javaPropertyName = propertyName(name, ISSER_PREFIX.length());
                        JSONPropertyInfo pair = javaNameToInfo.get(javaPropertyName);
                        if (pair != null)
                        {
                            pair.setGetterMethod(m);
                        }
                        else
                        {
                            javaNameToInfo.put(javaPropertyName, new JSONPropertyInfo(javaPropertyName,
                                m, null));
                        }
                    }
                }
                else if (name.startsWith(ADDER_PREFIX) && m.getParameterTypes().length == 1)
                {
                    String javaPropertyName = propertyName(name, ADDER_PREFIX.length());
                    JSONPropertyInfo pair = javaNameToInfo.get(javaPropertyName);
                    if (pair != null)
                    {
                        pair.setAdderMethod(m);
                    }
                    else
                    {
                        JSONPropertyInfo newInfo = new JSONPropertyInfo(javaPropertyName, null, null);
                        newInfo.setAdderMethod(m);
                        javaNameToInfo.put(javaPropertyName, newInfo);
                    }
                }
            }

            HashMap<String, JSONPropertyInfo> propertyInfos = new HashMap<String, JSONPropertyInfo>(javaNameToInfo.size());

            for (Map.Entry<String, JSONPropertyInfo> e : javaNameToInfo.entrySet())
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
                
                if (typeConverterRepository != null)
                {
                    JSONConverter converterAnno = getAnnotation(JSONConverter.class, getterMethod, setterMethod);
                    
                    if (converterAnno != null)
                    {
                        TypeConverter typeConverter = null;
                        if (converterAnno.name().length() == 0)
                        {
                             typeConverter = typeConverterRepository.getConverterByType(converterAnno.type());
                        }
                        else
                        {
                            typeConverter = typeConverterRepository.getConverterById(converterAnno.name());
                        }
                        propertyInfo.setTypeConverter(typeConverter);
                    }
                }
                

                propertyInfos.put(jsonPropertyName, propertyInfo);
            }
            
            return new JSONClassInfo(cls, propertyInfos);
        }
    }
}
