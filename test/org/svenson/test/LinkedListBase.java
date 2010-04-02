package org.svenson.test;

import java.util.List;

import org.svenson.Linked;

public class LinkedListBase
{
    private List<LinkedChildBean> children;
    
    @Linked
    public void setChildren(List<LinkedChildBean> children)
    {
        this.children = children;
    }
    
    public List<LinkedChildBean> getChildren()
    {
        return children;
    }
}
