package org.svenson.util;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

import org.svenson.SvensonRuntimeException;

/**
 * Wraps checked exceptions in unchecked {@link SvensonRuntimeException}. Allows to customizes
 * the handling of checked exceptions in the library for each specific type.
 *
 * @author fforw at gmx dot de
 *
 */
public class ExceptionWrapper
{
    public static SvensonRuntimeException wrap(IntrospectionException e)
    {
        return new SvensonRuntimeException(e);
    }

    public static SvensonRuntimeException wrap(IllegalAccessException e)
    {
        return new SvensonRuntimeException(e);
    }

    public static SvensonRuntimeException wrap(InvocationTargetException e)
    {
        return new SvensonRuntimeException("InvocationTargetException, target is"+e.getTargetException());
    }

    public static SvensonRuntimeException wrap(NoSuchMethodException e)
    {
        return new SvensonRuntimeException(e);
    }

    public static SvensonRuntimeException wrap(IOException e)
    {
        return new SvensonRuntimeException(e);
    }

    public static SvensonRuntimeException wrap( InstantiationException e)
    {
        return new SvensonRuntimeException(e);
    }

    public static SvensonRuntimeException wrap(ClassNotFoundException e)
    {
        return new SvensonRuntimeException(e);
    }

    public static SvensonRuntimeException wrap(ParseException e)
    {
        return new SvensonRuntimeException(e);
    }

    public static SvensonRuntimeException wrap(NumberFormatException e)
    {
        return new SvensonRuntimeException(e);
    }

    public static SvensonRuntimeException wrap(ArrayIndexOutOfBoundsException e)
    {
        // TODO Auto-generated method stub
        return null;
    }
}
