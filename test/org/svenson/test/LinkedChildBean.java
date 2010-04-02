package org.svenson.test;

import org.svenson.JSONProperty;

public class LinkedChildBean
{
    private String value;
    
    public LinkedChildBean()
    {
        this(null);
    }
    
    public LinkedChildBean(String value)
    {
        this.value = value;
    }

    @JSONProperty("_id")
    public void setValue(String value)
    {
        this.value = value;
    }
    
    public String getValue()
    {
        return value;
    }
}
