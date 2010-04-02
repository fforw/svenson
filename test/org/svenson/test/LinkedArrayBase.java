package org.svenson.test;

import org.svenson.JSONReference;

public class LinkedArrayBase
{
    private LinkedChildBean[] children;
    
    @JSONReference
    public LinkedChildBean[] getChildren()
    {
        return children;
    }
    
    public void setChildren(LinkedChildBean[] children)
    {
        this.children = children;
    }
}
