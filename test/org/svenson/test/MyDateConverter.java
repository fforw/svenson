package org.svenson.test;

import java.text.MessageFormat;
import java.util.Date;

import org.svenson.converter.TypeConverter;

/**
 * A simple type converter for java.util.Date.
 */
public class MyDateConverter
    implements TypeConverter
{

    public Object fromJSON(final Object in)
    {
        Object result = null;
        if (in instanceof Long)
        {
            result = new Date(((Long) in).longValue());
        }
        else if (in != null)
        {
            throw new IllegalArgumentException(MessageFormat.format(
                "Parameter must be a Long, was a {0} ( {1})", in, in.getClass())); //$NON-NLS-1$
        }
        return result;
    }


    public Object toJSON(final Object in)
    {
        Object result = null;
        if (in instanceof Date)
        {
            result = Long.valueOf(((Date) in).getTime());
        }
        else if (in != null)
        {
            throw new IllegalArgumentException(
                MessageFormat.format(
                    MessageFormat
                        .format("Parameter must be a {0}, was a {1}", Date.class.getName()), in.getClass().getName())); //$NON-NLS-1$
        }
        return result;
    }
}
