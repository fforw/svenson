package org.svenson.test;

import org.svenson.JSONParameter;
import org.svenson.JSONTypeHint;

import java.util.List;
import java.util.Map;

public class CTORMapVariance
{
    private final Map<String,Object> values;

    public CTORMapVariance(
        @JSONParameter("value") @JSONTypeHint(CTVBase.class) Map<String, Object> values
    )
    {
        this.values = values;
    }

    public Map<String, Object> getValues()
    {
        return values;
    }
}
