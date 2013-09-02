package org.svenson.test;

import org.svenson.JSONProperty;

public class PriorityOnBar
{
    private String foo,bar;

    public String getFoo()
    {
        return foo;
    }

    public void setFoo(String foo)
    {
        this.foo = foo;
    }

    @JSONProperty(priority = 1)
    public String getBar()
    {
        return bar;
    }

    public void setBar(String bar)
    {
        this.bar = bar;
    }
    
    
}
