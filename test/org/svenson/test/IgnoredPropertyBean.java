package org.svenson.test;

import org.svenson.AbstractDynamicProperties;
import org.svenson.JSONProperty;

public class IgnoredPropertyBean extends AbstractDynamicProperties
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String test;

    @JSONProperty(ignore = true)
    public void setTest(String test)
    {
        this.test = test;
    }

    public String getTest()
    {
        return test;
    }

}
