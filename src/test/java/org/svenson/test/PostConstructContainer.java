package org.svenson.test;

import org.svenson.JSONTypeHint;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

public class PostConstructContainer
{
    private List<PostConstructBean> list;
    private Map<String,PostConstructBean> map;

    public List<PostConstructBean> getList()
    {
        return list;
    }

    @JSONTypeHint(PostConstructBean.class)
    public void setList(List<PostConstructBean> list)
    {
        this.list = list;
    }

    public Map<String, PostConstructBean> getMap()
    {
        return map;
    }

    @JSONTypeHint(PostConstructBean.class)
    public void setMap(Map<String, PostConstructBean> map)
    {
        this.map = map;
    }
}
