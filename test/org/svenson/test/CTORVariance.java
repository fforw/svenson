package org.svenson.test;

import org.svenson.JSONParameter;
import org.svenson.JSONTypeHint;

public class CTORVariance
{
    private final String name;
    private final CTVBase value;

    public CTORVariance(
        @JSONParameter("value") @JSONTypeHint(CTVBase.class) Object o
    )
    {
        if (o instanceof CTVBase)
        {
            name = null;
            value = (CTVBase) o;
        }
        else
        {
            name = (String) o;
            value = null;
        }
    }

    public String getName()
    {
        return name;
    }

    public CTVBase getValue()
    {
        return value;
    }
}
