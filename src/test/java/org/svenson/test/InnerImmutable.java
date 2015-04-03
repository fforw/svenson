package org.svenson.test;

import org.svenson.JSONParameter;

public class InnerImmutable
{
    private final String string;

    public InnerImmutable(
        @JSONParameter("string") String string)
    {
        this.string = string;
    }

    public String getString()
    {
        return string;
    }
}
