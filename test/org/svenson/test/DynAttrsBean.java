/**
 *
 */
package org.svenson.test;

import org.svenson.AbstractDynamicProperties;


public class DynAttrsBean
    extends AbstractDynamicProperties
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String foo;

    public String getFoo()
    {
        return foo;
    }

    public void setFoo(String foo)
    {
        this.foo = foo;
    }
}