package org.svenson.util;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.svenson.DynamicProperties;
import org.svenson.JSONParser;
import org.svenson.TypeAnalyzer;
import org.svenson.info.JSONClassInfo;
import org.svenson.info.JSONPropertyInfo;
import org.svenson.info.JavaObjectSupport;
import org.svenson.info.ObjectSupport;

/**
 * Contains some util methods to handle bean properties dynamically.
 *
 * @author fforw at gmx dot de
 *
 */
public class JSONBeanUtil
{
    private final static JSONBeanUtil instance = new JSONBeanUtil();
    
    private ObjectSupport objectSupport = new JavaObjectSupport();
    
    public void setObjectSupport(ObjectSupport objectSupport)
    {
        this.objectSupport = objectSupport;
    }
    
    /**
     * Returns the names of all properties of this dynamic properties object including the java bean properties.
     * Note that the method will return the <em>JSON property name</em> of the java bean methods.
     * @param bean     DynamicProperties object
     * @return a set containing all property names, both dynamic and static (JSON) names.
     */
    public Set<String> getAllPropertyNames(Object bean)
    {
        if (bean instanceof Map)
        {
            return new HashSet<String>(((Map)bean).keySet());
        }

        Set<String> names  = new HashSet<String>( );
        if (bean instanceof DynamicProperties)
        {
            names.addAll(((DynamicProperties)bean).propertyNames());
        }
        names.addAll( getBeanPropertyNames(bean));
        return names;
    }

    /**
     * Returns all readable and writable bean property JSON names of the given object.
     * @param bean object
     * @return
     */
    public Set<String> getBeanPropertyNames(Object bean)
    {
        JSONClassInfo classInfo = getClassInfoForBean(bean);
        return classInfo.getPropertyNames();
    }

    protected JSONClassInfo getClassInfoForBean(Object bean)
    {
        return TypeAnalyzer.getClassInfo(objectSupport, bean.getClass());
    }

    /**
     * Gets the bean or dynamic property with the given JSON property name. if
     * the class has a bean property with the given name, the value of that
     * property is returned. otherwise, the dynamic property with the given name
     * is returned.
     *
     * @param bean java bean
     * @param name JSON property name
     * @return the property value.
     * @throws IllegalArgumentException if there is no bean property with the
     *             given name on the given dynamicProperties object and the
     *             class of the bean does not implement
     *             {@link DynamicProperties}
     */
    public Object getProperty(Object bean, String name)
        throws IllegalArgumentException
    {
        JSONPropertyInfo propertyInfo;
        if (bean instanceof Map)
        {
            return ((Map)bean).get(name);
        }
        else if ((propertyInfo = getClassInfoForBean(bean).getPropertyInfo(name)) != null && propertyInfo.isReadable())
        {
                return propertyInfo.getProperty(bean);
        }
        else if (bean instanceof DynamicProperties)
        {
            return ((DynamicProperties) bean).getProperty(name);
        }
        else
        {
            throw new IllegalArgumentException(bean +
                " has no JSON property with the name '" + name +
                "' and does not implements DynamicProperties");
        }
    }


    /**
     * Sets the bean or dynamic property with the given JSON property name to
     * the given value. if the class has a bean property with the given name,
     * the value of that property is overwritten. otherwise, the dynamic
     * property with the given name is overwritten.
     *
     * @param bean bean or dynamic properties instance
     * @param name JSON property name
     * @param value property value
     * @throws IllegalArgumentException if there is no bean property with the
     *             given name on the given dynamicProperties object and the
     *             class of the bean does not implement
     *             {@link DynamicProperties}
     */
    public void setProperty(Object bean, String name, Object value)
        throws IllegalArgumentException
    {
        JSONPropertyInfo propertyInfo;
        if (bean instanceof Map)
        {
            ((Map)bean).put(name, value);
        }
        else if ((propertyInfo = getClassInfoForBean(bean).getPropertyInfo(name)) != null && propertyInfo.isWriteable())
        {
            if (Enum.class.isAssignableFrom(propertyInfo.getType()) && value instanceof String)
            {
                Class<Enum> cls = (Class)propertyInfo.getType();
                propertyInfo.setProperty(bean, Enum.valueOf(cls, (String)value));
            }
            else
            {
                propertyInfo.setProperty(bean, value);
            }
        }
        else if (bean instanceof DynamicProperties)
        {
            ((DynamicProperties) bean).setProperty(name, value);
        }
        else
        {
            throw new IllegalArgumentException(bean +
                " has no JSON property with the name '" + name +
                "' and does not implements DynamicProperties");
        }
    }

    public static JSONBeanUtil defaultUtil()
    {
        return instance;
    }
}
