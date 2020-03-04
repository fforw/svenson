package org.svenson.util.recast;

import org.svenson.JSONTypeHint;

import java.util.Map;

public class RecastMapPropTarget
{
    private Map<String,RecastTargetComponent> components;


    public Map<String, RecastTargetComponent> getComponents()
    {
        return components;
    }

    @JSONTypeHint(RecastTargetComponent.class)
    public void setComponents(Map<String, RecastTargetComponent> components)
    {
        this.components = components;
    }
}
