package org.svenson.test;

import java.util.Comparator;

import org.svenson.info.JSONPropertyInfo;

abstract class AbstractAlphaComparator
implements Comparator<JSONPropertyInfo>
{
    private int n;

    protected AbstractAlphaComparator(boolean asc)
    {
        this.n = asc ? 1 : -1;
    }

    public int compare(JSONPropertyInfo o1, JSONPropertyInfo o2)
    {
        return n * o1.getJsonName().compareTo(o2.getJsonName());
    }

}
