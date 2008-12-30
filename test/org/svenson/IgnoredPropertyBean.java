package org.svenson;

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
