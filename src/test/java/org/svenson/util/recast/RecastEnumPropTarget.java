package org.svenson.util.recast;

import org.svenson.JSONParameter;
import org.svenson.test.SomeEnum;

public class RecastEnumPropTarget
{
    private final SomeEnum someEnum;


    public RecastEnumPropTarget(
        @JSONParameter("someEnum") SomeEnum someEnum
    )
    {
        this.someEnum = someEnum;
    }


    public SomeEnum getSomeEnum()
    {
        return someEnum;
    }


}
