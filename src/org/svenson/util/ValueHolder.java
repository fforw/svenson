package org.svenson.util;

public class ValueHolder<V>
{
    private volatile V value;

    public V getValue()
    {
        return value;
    }

    public void setValue(V value)
    {
        this.value = value;
    }
}
