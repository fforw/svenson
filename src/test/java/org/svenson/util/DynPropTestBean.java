package org.svenson.util;

import org.svenson.AbstractDynamicProperties;
import org.svenson.JSONProperty;

public class DynPropTestBean extends AbstractDynamicProperties
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String foo;

    @JSONProperty("_foo")
    public String getFoo()
    {
        return foo;
    }

    public void setFoo(String foo)
    {
        this.foo = foo;
    }
}
