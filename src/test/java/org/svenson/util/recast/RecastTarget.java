package org.svenson.util.recast;

import org.svenson.JSONTypeHint;

import java.util.List;

public class RecastTarget
{
    private String name;
    private int num;

    private List<RecastTargetComponent> components;

    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public int getNum()
    {
        return num;
    }


    public void setNum(int num)
    {
        this.num = num;
    }


    public List<RecastTargetComponent> getComponents()
    {
        return components;
    }


    @JSONTypeHint(RecastTargetComponent.class)
    public void setComponents(List<RecastTargetComponent> components)
    {
        this.components = components;
    }
}
