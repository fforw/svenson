package org.svenson.util;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.svenson.CouchJSONException;

public class ExceptionWrapper
{
    public static CouchJSONException wrap(InstantiationException e)
    {
        return new CouchJSONException(e);
    }

    public static CouchJSONException wrap(IllegalAccessException e)
    {
        return new CouchJSONException(e);
    }

    public static CouchJSONException wrap(InvocationTargetException e)
    {
        return new CouchJSONException(e);
    }

    public static CouchJSONException wrap(NoSuchMethodException e)
    {
        return new CouchJSONException(e);
    }

    public static CouchJSONException wrap(IOException e)
    {
        return new CouchJSONException(e);
    }

    public static CouchJSONException wrap(ClassNotFoundException e)
    {
        return new CouchJSONException(e);
    }
}
