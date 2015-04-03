package org.svenson.test;

import org.svenson.JSONParameter;

public class Immutable
{
    private final InnerImmutable inner;

    public Immutable(
        @JSONParameter("inner") InnerImmutable inner)
    {
        this.inner = inner;
    }

    public InnerImmutable getInner()
    {
        return inner;
    }
}
