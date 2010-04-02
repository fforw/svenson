package org.svenson.test;

import org.svenson.Linked;

public class LinkedArrayBase
{
    private LinkedChildBean[] children;
    
    @Linked
    public LinkedChildBean[] getChildren()
    {
        return children;
    }
    
    public void setChildren(LinkedChildBean[] children)
    {
        this.children = children;
    }
}
