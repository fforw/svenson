package org.svenson.info;

/**
 * Created by sven on 15.03.15.
 */
public class ParameterInfo
{
    private final int index;
    private final Class typeHint;

    public ParameterInfo(int index, Class typeHint)
    {
        this.index = index;
        this.typeHint = typeHint;
    }

    public int getIndex()
    {
        return index;
    }

    public Class getTypeHint()
    {
        return typeHint;
    }
}
