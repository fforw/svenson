package org.svenson;

public class ContainerBean
{
    private Object childBean;

    @JSONProperty("child")
    public Object getChildBean()
    {
        return childBean;
    }

    public void setChildBean(Object childBean)
    {
        this.childBean = childBean;
    }

}
