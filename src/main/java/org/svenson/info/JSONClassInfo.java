package org.svenson.info;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.svenson.JSONPropertyOrder;
import org.svenson.util.ExceptionWrapper;

/**
 * Encapsulates svensons knowledge about a class. Provides a constructor method. 
 * 
 * @author fforw at gmx dot de
 *
 */
public class JSONClassInfo
{

    private final ConstructorInfo constructorInfo;
    private final Class<?> cls;

    protected final Map<String, JSONPropertyInfo> propertyInfos;

    private final List<JSONPropertyInfo> sortedPropertyInfos;

    public JSONClassInfo(Class<?> cls, Map<String, ? extends JSONPropertyInfo> propertyInfos, Constructor<?> ctor)
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
        if (ctor != null)
        {
            constructorInfo = new ConstructorInfo(ctor);
        }
        else
        {
            constructorInfo = null;
        }
        this.propertyInfos = (Map<String, JSONPropertyInfo>) propertyInfos;
        
        List<JSONPropertyInfo> copy = new ArrayList<JSONPropertyInfo>(propertyInfos.values());
        
        JSONPropertyOrder orderAnno = cls.getAnnotation(JSONPropertyOrder.class);
        
        Class<? extends Comparator> comparatorClass = JSONPropertyPriorityComparator.class;
        
        if (orderAnno != null)
        {
            comparatorClass = orderAnno.value();
        }
        
        try
        {
            Collections.sort(copy, comparatorClass.newInstance());
        }
        catch (InstantiationException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (IllegalAccessException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        
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


    public ConstructorInfo getConstructorInfo()
    {
        return constructorInfo;
    }
}
