package org.svenson.util;


public class InvalidPropertyPathException extends RuntimeException
{

    private static final long serialVersionUID = 4764915637457208219L;

    public InvalidPropertyPathException(String propertyPath, String message)
    {
        super("Invalid property path '" + propertyPath + "': " + message);
    }
}
