package org.svenson.converter;

import java.util.HashMap;
import java.util.Map;

/**
 * Default repository for svenson type converters. Allows registering converters
 * with or without id and resolving them by id or by type.
 * 
 * @author shelmberger
 */
public class DefaultTypeConverterRepository implements TypeConverterRepository
{
    private Map<String, TypeConverter> converters = new HashMap<String, TypeConverter>();

    /**
     * Registers the given converter under a generated id.
     * 
     * @param converter
     */
    public void addTypeConverter(TypeConverter converter)
    {
        String id = createId();
        addTypeConverter(id,converter);
    }

    /**
     * Registers the given converter under the given id.
     * 
     * @param id
     * @param converter
     */
    public void addTypeConverter(String id, TypeConverter converter)
    {
        converters.put(id, converter);
    }
    
    /**
     * Creates a converter id.
     * @return
     */
    private String createId()
    {
        return "__converter-" + converters.size();
    }
    

    /**
     * {@inheritDoc}
     */
    public <T extends TypeConverter> T getConverterByType(Class<T> t)
    {
        T typeConverter = null; 
        for (TypeConverter tc : converters.values())
        {
            if (t.isAssignableFrom(tc.getClass()))
            {
                if (typeConverter != null)
                {
                    throw new IllegalStateException("Found more than one instances of " + t);
                }
                
                typeConverter = (T)tc;
            }
        }
        return typeConverter;
    }

    /**
     * {@inheritDoc}
     */
    public TypeConverter getConverterById(String id)
    {
        return converters.get(id);
    }

}
