package org.svenson;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Abstract base class for bean that want to use dynamic properties.
 *
 * @author fforw at gmx dot de
 */
public abstract class AbstractDynamicProperties implements DynamicProperties
{
    private static final long serialVersionUID = -2474994145266650492L;

    private Map<String,Object> attrs = new HashMap<String, Object>();

    public Object getProperty(String name)
    {
        return attrs.get(name);
    }

    public void setProperty(String name, Object value)
    {
        attrs.put(name,value);
    }

    public Set<String> propertyNames()
    {
        return attrs.keySet();
    }
    
    public boolean hasProperty(String name)
    {
        return attrs.containsKey(name);
    }
    
    public Object removeProperty(String name)
    {
        return attrs.remove(name);
    }    
}
