package org.svenson.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.svenson.converter.JSONConverter;
import org.svenson.converter.TypeConverter;
import org.svenson.converter.TypeConverterRepository;

public class TypeConverterCache
{
    private ConcurrentMap<Class,ValueHolder<Map<String,TypeConverter>>> classToTypeConverter = new ConcurrentHashMap<Class, ValueHolder<Map<String,TypeConverter>>>();

    private TypeConverterRepository typeConverterRepository;

    public TypeConverterCache(TypeConverterRepository typeConverterRepository)
    {
        this.typeConverterRepository = typeConverterRepository;
    }
    
    public TypeConverter getTypeConverter(Object target, String name)
    {
        ValueHolder<Map<String,TypeConverter>> holder = new ValueHolder<Map<String,TypeConverter>>();

        Class cls = target.getClass();
        ValueHolder<Map<String,TypeConverter>> existing = classToTypeConverter.putIfAbsent(cls, holder);
        if (existing != null)
        {
            holder = existing;
        }

        Map<String,TypeConverter> typeConverters = holder.getValue();
        if (typeConverters == null)
        {
            synchronized (holder)
            {
                typeConverters = holder.getValue();
                if (typeConverters == null)
                {
                    typeConverters = new HashMap<String, TypeConverter>();

                    for (PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(cls))
                    {
                        Method readMethod = pd.getReadMethod();
                        Method writeMethod = pd.getWriteMethod();

                        JSONConverter anno = readMethod.getAnnotation(JSONConverter.class);
                        if (anno == null && writeMethod != null)
                        {
                            anno = writeMethod.getAnnotation(JSONConverter.class);
                        }
                        if (anno != null)
                        {
                            TypeConverter typeConverter = null;
                            if (anno.name().length() == 0)
                            {
                                 typeConverter = typeConverterRepository.getConverterByType(anno.type());
                            }
                            else
                            {
                                typeConverter = typeConverterRepository.getConverterById(anno.name());
                            }
                            typeConverters.put(pd.getName(), typeConverter);
                        }
                    }
                    holder.setValue(typeConverters);
                }
            }
        }
        return typeConverters.get(name);            
    }

}
