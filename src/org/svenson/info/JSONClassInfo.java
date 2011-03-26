package org.svenson.info;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.svenson.JSONProperty;
import org.svenson.JSONReference;
import org.svenson.JSONTypeHint;

import com.sun.xml.internal.bind.v2.model.core.ClassInfo;

/**
 * Encapsulates svensons knowledge about a class. Provides a constructor method. 
 * 
 * @author fforw at gmx dot de
 *
 */
public class JSONClassInfo
{

    private Class cls;

    protected Map<String, ? extends JSONPropertyInfo> propertyInfos;

    public JSONClassInfo(Class cls, Map<String, ? extends JSONPropertyInfo> propertyInfos)
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
