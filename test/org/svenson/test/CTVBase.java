package org.svenson.test;

import org.svenson.JSONProperty;

public abstract class CTVBase
{
    @JSONProperty(readOnly = true)
    public String getType()
    {
        return this.getClass().getSimpleName();
    }
}
