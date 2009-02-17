/**
 *
 */
package org.svenson.test;

import java.util.List;
import java.util.Map;

import org.svenson.JSONProperty;
import org.svenson.JSONTypeHint;

public class Bean
{
    private String foo;

    int bar;

    private List<InnerBean> inners;

    private Map<String, InnerBean> inner2;

    public String getFoo()
    {
        return foo;
    }

    public void setFoo(String foo)
    {
        this.foo = foo;
    }

    @JSONProperty("bar")
    public int getNotBar()
    {
        return bar;
    }

    public void setNotBar(int bar)
    {
        this.bar = bar;
    }

    public List<InnerBean> getInner()
    {
        return inners;
    }

    public Map<String, InnerBean> getInner2()
    {
        return inner2;
    }

    @JSONTypeHint(InnerBean.class)
    public void setInner2(Map<String, InnerBean> inner2)
    {
        this.inner2 = inner2;
    }

    @JSONTypeHint(InnerBean.class)
    public void setInner(List<InnerBean> inners)
    {
        this.inners = inners;
    }

}