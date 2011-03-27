package org.svenson.info;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates svensons knowledge about a class. Provides a constructor method. 
 * 
 * @author fforw at gmx dot de
 *
 */
public class JSONClassInfo
{

    private Class<?> cls;

    protected Map<String, ? extends JSONPropertyInfo> propertyInfos;

    public JSONClassInfo(Class<?> cls, Map<String, ? extends JSONPropertyInfo> propertyInfos)
    {
        this.cls = cls;
        this.propertyInfos = propertyInfos;
    }


    public JSONPropertyInfo getPropertyInfo(String jsonPropertyName)
    {
        return propertyInfos.get(jsonPropertyName);
    }


    public Set<String> getPropertyNames()
    {
        return propertyInfos.keySet();
    }


    @SuppressWarnings("unchecked")
    public Collection<JSONPropertyInfo> getPropertyInfos()
    {
        return (Collection<JSONPropertyInfo>)propertyInfos.values();
    }


    @Override
    public String toString()
    {
        return super.toString() + " cls = " + cls + ", propertyInfos = " + propertyInfos;
    }

    
}
