package org.svenson.test;

import org.svenson.JSONParameter;

public class Qux
{
    private final String name;

    public Qux(
        @JSONParameter("name")String name
    )
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }
}
