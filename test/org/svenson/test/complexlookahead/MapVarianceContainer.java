package org.svenson.test.complexlookahead;

import org.svenson.JSONTypeHint;

import java.util.Map;

public class MapVarianceContainer
{
    private Map<String,Object> props;

    public Map<String, Object> getProps()
    {
        return props;
    }

    @JSONTypeHint(Foo.class)
    public void setProps(Map<String, Object> props)
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
