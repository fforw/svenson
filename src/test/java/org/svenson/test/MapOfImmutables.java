package org.svenson.test;

import org.svenson.JSONParameter;
import org.svenson.JSONTypeHint;

import java.util.List;
import java.util.Map;

public class MapOfImmutables
{
    private final Map<String, Qux> quxes;

    public MapOfImmutables(
        @JSONParameter("quxes") @JSONTypeHint(Qux.class) Map<String, Qux> quxes
    )
    {
        this.quxes = quxes;
    }

    public Map<String, Qux> getQuxes()
    {
        return quxes;
    }
}
