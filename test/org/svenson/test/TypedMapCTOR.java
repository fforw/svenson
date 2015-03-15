package org.svenson.test;

import org.svenson.JSONParameter;
import org.svenson.JSONTypeHint;

import java.util.Map;

public class TypedMapCTOR
{
    private final Map<String,Bar> bar;
    private final boolean flag;

    public TypedMapCTOR(
        @JSONParameter("bar") @JSONTypeHint(Bar.class) Map<String, Bar> bar,
        @JSONParameter("flag") boolean flag)
    {
        this.bar = bar;

        this.flag = flag;
    }

    public Map<String,Bar> getBar()
    {
        return bar;
    }


    public boolean isFlag()
    {
        return flag;
    }
}
