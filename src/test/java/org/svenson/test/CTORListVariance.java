package org.svenson.test;

import org.svenson.JSONParameter;
import org.svenson.JSONTypeHint;

import java.util.List;

public class CTORListVariance
{
    private final List<Object> values;

    public CTORListVariance(
        @JSONParameter("value") @JSONTypeHint(CTVBase.class) List<Object> values
    )
    {
        this.values = values;
    }

    public List<Object> getValues()
    {
        return values;
    }
}
