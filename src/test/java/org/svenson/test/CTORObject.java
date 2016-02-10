package org.svenson.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.JSONParameter;
import org.svenson.JSONTypeHint;

public class CTORObject
{
    private static Logger log = LoggerFactory.getLogger(CTORObject.class);


    private final String value;

    public CTORObject(
        @JSONParameter("value") Object value
    )
    {
        this.value = value == null ? "null" : value.getClass().getName() + ":" + value;
    }


    public String getValue()
    {
        return value;
    }
}
