package org.svenson.test;

import org.svenson.Linked;

public class LinkedBeanBase
{
    private LinkedChildBean child;
    
    @Linked
    public LinkedChildBean getChild()
    {
        return child;
    }
    
    public void setChild(LinkedChildBean child)
    {
        this.child = child;
    }
}
