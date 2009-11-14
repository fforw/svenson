/**
 *
 */
package org.svenson.test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.svenson.JSONProperty;
import org.svenson.JSONTypeHint;

public class Bean
{
    private String foo;

    int bar;

    private List<InnerBean> inners;

    private Map<String, InnerBean> inner2;

    private Set<InnerBean> inner3;

    private Collection<InnerBean> inner4;

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
    @JSONProperty(ignoreIfNull = true)
    public void setInner2(Map<String, InnerBean> inner2)
    {
        this.inner2 = inner2;
    }

    @JSONProperty(ignoreIfNull = true)
    @JSONTypeHint(InnerBean.class)
    public void setInner(List<InnerBean> inners)
    {
        this.inners = inners;
    }
    
    @JSONProperty(ignoreIfNull = true)
    @JSONTypeHint(InnerBean.class)
    public void setInner3(Set<InnerBean> inner3)
    {
        this.inner3 = inner3;
    }
    
    public Set<InnerBean> getInner3()
    {
        return inner3;
    }

    @JSONProperty(ignoreIfNull = true)
    @JSONTypeHint(InnerBean.class)
    public void setInner4(Collection<InnerBean> inner4)
    {
        this.inner4 = inner4;
    }
    
    public Collection<InnerBean> getInner4()
    {
        return inner4;
    }
}