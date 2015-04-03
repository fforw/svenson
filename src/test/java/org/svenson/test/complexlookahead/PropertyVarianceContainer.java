package org.svenson.test.complexlookahead;

import org.svenson.JSONTypeHint;

public class PropertyVarianceContainer
{
    private Object foo;

    public Object getFoo()
    {
        return foo;
    }

    @JSONTypeHint(Base.class)
    public void setFoo(Object foo)
    {
        this.foo = foo;
    }


    @Override
    public String toString()
    {
        return super.toString() + ": "
            + "foo = " + foo
            ;
    }
}
