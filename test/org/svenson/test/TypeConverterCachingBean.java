package org.svenson.test;

import org.svenson.JSONProperty;
import org.svenson.converter.JSONConverter;

import java.util.Date;

public class TypeConverterCachingBean
{

    private Date timestamp;

    @JSONProperty("timestamp")
    @JSONConverter(type = MyDateConverter.class)
    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }

}