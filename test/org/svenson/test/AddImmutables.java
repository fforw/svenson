package org.svenson.test;

import org.svenson.JSONParameter;
import org.svenson.JSONTypeHint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddImmutables
{
    private final List<Qux> quxes;

    public AddImmutables()
    {
        quxes = new ArrayList<Qux>();
    }

    public void addQuxes(Qux qux)
    {
        quxes.add(qux);
    }

    public List<Qux> getQuxes()
    {
        return quxes;
    }
}

