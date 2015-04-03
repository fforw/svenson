package org.svenson.converter;

/**
 * Interface implemented by the repositories for {@link TypeConverter}s.
 * 
 * @see DefaultTypeConverterRepository
 * 
 * @author fforw at gmx dot de
 *
 */
public interface TypeConverterRepository
{
    
    TypeConverter getConverterById(String id);
    <T extends TypeConverter> T getConverterByType(Class<T> t);
}
