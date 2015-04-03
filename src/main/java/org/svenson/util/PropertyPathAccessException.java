package org.svenson.util;

public class PropertyPathAccessException
    extends RuntimeException
{
    private static final long serialVersionUID = 8271024489492558383L;

    public PropertyPathAccessException(String propertyPath, Object bean, String message)
    {
        super("Access error in path \"" + propertyPath + "\" on " + bean + ": " +  message);
    }
    
}
