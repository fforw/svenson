package org.svenson.test;

import org.svenson.JSONParameter;

public class ConstructorParametrization
{
    public final String name;
    public final int age;

    public ConstructorParametrization(
        @JSONParameter("name") String name,
        @JSONParameter("age") int age)
    {
        this.name = name;
        this.age = age;
    }
}
