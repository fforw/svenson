package org.svenson.test;

import java.util.ArrayList;
import java.util.List;

public class AdderTypeHint
{
    private List<Bar> bars = new ArrayList<Bar>();

    public List<Bar> getBars()
    {
        return bars;
    }

    public void addBars(Bar bar)
    {
        bars.add(bar);
    }
}
