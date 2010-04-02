package org.svenson.test;

import java.util.Map;

import org.svenson.Linked;

public class LinkedMapBase
{
    private Map<String,LinkedChildBean> children;
    
    @Linked
    public void setChildren(Map<String, LinkedChildBean> children)
    {
        this.children = children;
    }
    
    public Map<String, LinkedChildBean> getChildren()
    {
        return children;
    }
}
