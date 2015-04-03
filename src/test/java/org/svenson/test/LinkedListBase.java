package org.svenson.test;

import java.util.List;

import org.svenson.JSONReference;

public class LinkedListBase
{
    private List<LinkedChildBean> children;
    
    @JSONReference
    public void setChildren(List<LinkedChildBean> children)
    {
        this.children = children;
    }
    
    public List<LinkedChildBean> getChildren()
    {
        return children;
    }
}
