package org.svenson.test.complexlookahead;

import org.svenson.JSONTypeHint;

import java.util.List;
import java.util.Map;

public class ListVarianceContainer
{
    private List<Object> props;


    public List<Object> getProps()
    {
        return props;
    }

    @JSONTypeHint(Foo.class)
    public void setProps(List<Object> props)
    {
        this.props = props;
    }

    @Override
    public String toString()
    {
        return super.toString() + ": "
            + "props = " + props
            ;
    }
}
