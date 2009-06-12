/**
 * 
 */
package org.svenson.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.svenson.util.ExceptionWrapper;

public class DateConverter
    implements TypeConverter
{
    private String format;

    private TimeZone timeZone;

    public DateConverter()
    {
        this(null, null);
    }

    public DateConverter(String format)
    {
        this(format, null);
    }

    public DateConverter(String format, TimeZone timeZone)
    {
        setFormat(format);
        setTimeZone(timeZone);
    }

    public void setFormat(String format)
    {
        if (format == null)
        {
            format = "yyyy-MM-dd'T'HH:mm:ss";
        }
        this.format = format;
    }

    public void setTimeZone(TimeZone timeZone)
    {
        if (timeZone == null)
        {
            timeZone = TimeZone.getTimeZone("GMT");
        }
        this.timeZone = timeZone;
    }

    private SimpleDateFormat createFormat()
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(timeZone);
        return sdf;
    }

    public Object fromJSON(Object in)
    {
        if (in instanceof String)
        {
            SimpleDateFormat sdf = createFormat();
            try
            {
                return sdf.parse((String) in);
            }
            catch (ParseException e)
            {
                throw ExceptionWrapper.wrap(e);
            }
        }
        else
        {
            throw new IllegalArgumentException("Parameter must be a String, was a " + in);
        }
    }

    public Object toJSON(Object in)
    {
        if (in instanceof Date)
        {
            SimpleDateFormat sdf = createFormat();
            return sdf.format(in);
        }
        else
        {
            throw new IllegalArgumentException("Parameter must be a java.util.Date, was a " + in);
        }
    }
}
