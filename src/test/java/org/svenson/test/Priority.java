package org.svenson.test;

import org.svenson.JSONProperty;

public class Priority
{
    private String foo,bar;

    public String getFoo()
    {
        return foo;
    }

    @JSONProperty(priority = 1)
    public void setFoo(String foo)
    {
        this.foo = foo;
    }

    public String getBar()
    {
        return bar;
    }

    public void setBar(String bar)
    {
        this.bar = bar;
    }
    
    
}
