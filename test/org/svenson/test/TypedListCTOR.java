package org.svenson.test;

import org.svenson.JSONParameter;
import org.svenson.JSONTypeHint;

import java.util.List;

public class TypedListCTOR
{
    private final List<Bar> bar;
    private final long age;

    public TypedListCTOR(
        @JSONParameter("bar") @JSONTypeHint(Bar.class) List<Bar> bar,
        @JSONParameter("age") int age)
    {
        this.bar = bar;
        this.age = age;
    }

    public List<Bar> getBar()
    {
        return bar;
    }

    public long getAge()
    {
        return age;
    }
}
