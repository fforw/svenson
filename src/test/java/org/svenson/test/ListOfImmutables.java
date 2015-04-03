package org.svenson.test;

import org.svenson.JSONParameter;
import org.svenson.JSONTypeHint;

import java.util.List;

public class ListOfImmutables
{
    private final List<Qux> quxes;

    public ListOfImmutables(
        @JSONParameter("quxes") @JSONTypeHint(Qux.class) List<Qux> quxes
    )
    {
        this.quxes = quxes;
    }

    public List<Qux> getQuxes()
    {
        return quxes;
    }
}
