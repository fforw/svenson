package perf;


import java.util.List;

import org.svenson.JSONTypeHint;

public class Foo
{
    private List<Bar> bars;

    
    public List<Bar> getBars()
    {
        return bars;
    }

    @JSONTypeHint(Bar.class)
    public void setBars(List<Bar> bars)
    {
        this.bars = bars;
    }
    
}
