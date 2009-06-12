package org.svenson.converter;

/**
 * Interface for all JSON type converters
 * @author shelmberger
 *
 */
public interface TypeConverter
{
    /**
     * Converts the given object when parsing JSON.
     * 
     * @param in    input object
     * @return      ouput object
     */
    Object fromJSON(Object in);

    /**
     * Converts the given object when generating JSON.
     * 
     * @param in    input object
     * @return      ouput object
     */
    Object toJSON(Object in);
}
