package org.svenson;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Abstract base class for bean that want to use dynamic properties.
 *
 * @author shelmberger
 */
public abstract class AbstractDynamicProperties implements DynamicProperties
{
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
}
