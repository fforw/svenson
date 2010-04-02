package org.svenson.test;

import org.svenson.JSONReference;

public class LinkedBeanBase
{
    private LinkedChildBean child;
    
    @JSONReference
    public LinkedChildBean getChild()
    {
        return child;
    }
    
    public void setChild(LinkedChildBean child)
    {
        this.child = child;
    }
}
