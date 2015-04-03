/**
 * 
 */
package org.svenson.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ComplexDateConverter implements TypeConverter
{

    @SuppressWarnings("deprecation")
    public Object fromJSON(Object in)
    {
        if (in instanceof List)
        {
            List<Long> list = (List)in;

            int[] data = new int[6];
            int index = 0;
            while (list.size() > 0)
            {
                data[ index++ ] = (int)list.remove(0).longValue();
            }
            
            return new Date(
                data[0],
                data[1],
                data[2],
                data[3],
                data[4],
                data[5]);
        }
        else
        {
            throw new IllegalArgumentException("Parameter must be a List, was a " + in);
        }
    }

    @SuppressWarnings("deprecation")
    public Object toJSON(Object in)
    {
        if (in instanceof Date)
        {
            Date date = (Date)in;
            List<Long> list = new ArrayList<Long>();
            list.add((long)date.getYear());
            list.add((long)date.getMonth());
            list.add((long)date.getDate());
            list.add((long)date.getHours());
            list.add((long)date.getMinutes());
            list.add((long)date.getSeconds());
            return list;
        }
        else
        {
            throw new IllegalArgumentException("Parameter must be a java.util.List, was a " + in);
        }
    }
    
}