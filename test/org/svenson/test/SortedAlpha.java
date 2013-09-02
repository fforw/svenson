package org.svenson.test;

import org.svenson.JSONPropertyOrder;

@JSONPropertyOrder(AlphaComparator.class)
public class SortedAlpha
{
    private int a,c,b;

    public int getA()
    {
        return a;
    }

    public void setA(int a)
    {
        this.a = a;
    }

    public int getC()
    {
        return c;
    }

    public void setC(int c)
    {
        this.c = c;
    }

    public int getB()
    {
        return b;
    }

    public void setB(int b)
    {
        this.b = b;
    }
    
    
}
