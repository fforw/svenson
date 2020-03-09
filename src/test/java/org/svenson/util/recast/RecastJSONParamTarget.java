package org.svenson.util.recast;

import org.svenson.JSONParameter;

public class RecastJSONParamTarget
{
    private final String name;

    private final int num;

    public RecastJSONParamTarget(
        @JSONParameter("name")
        String name,
        @JSONParameter("num")
        int num
    )
    {
        this.name = name;
        this.num = num;
    }


    public String getName()
    {
        return name;
    }


    public int getNum()
    {
        return num;
    }
}
