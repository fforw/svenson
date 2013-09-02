package org.svenson.info;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Encapsulates svensons knowledge about a class. Provides a constructor method. 
 * 
 * @author fforw at gmx dot de
 *
 */
public class JSONClassInfo
{

    private Class<?> cls;

    protected Map<String, JSONPropertyInfo> propertyInfos;

    private List<JSONPropertyInfo> sortedPropertyInfos;

    public JSONClassInfo(Class<?> cls, Map<String, ? extends JSONPropertyInfo> propertyInfos)
    {
        if (cls == null)
        {
            throw new IllegalArgumentException("Class can't be null.");
        }
        if (propertyInfos == null)
        {
            throw new IllegalArgumentException("Infos can't be null.");
        }
        
        this.cls = cls;
        this.propertyInfos = (Map<String, JSONPropertyInfo>) propertyInfos;
        
        List<JSONPropertyInfo> copy = new ArrayList<JSONPropertyInfo>(propertyInfos.values());
        
        Collections.sort(copy, new JSONPropertyInfoComparator());
        
        this.sortedPropertyInfos = copy;
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
    public List<JSONPropertyInfo> getPropertyInfos()
    {
        return sortedPropertyInfos;
    }


    @Override
    public String toString()
    {
        return super.toString() + " cls = " + cls + ", propertyInfos = " + propertyInfos;
    }

    
}
