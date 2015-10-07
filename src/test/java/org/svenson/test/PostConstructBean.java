package org.svenson.test;

import javax.annotation.PostConstruct;

public class PostConstructBean
{
    private int foo;
    private boolean initialized;

    public int getFoo()
    {
        return foo;
    }

    public void setFoo(int foo)
    {
        this.foo = foo;
    }

    @PostConstruct
    public void init()
    {
        this.initialized = true;
    }

    public boolean isInitialized()
    {
        return initialized;
    }
}
