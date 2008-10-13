/**
 *
 */
package org.couchjson;

import org.svenson.AbstractDynamicProperties;


public class DynAttrsBean
    extends AbstractDynamicProperties
{
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