package org.svenson.util;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.svenson.DynamicProperties;
import org.svenson.info.JSONClassInfo;
import org.svenson.info.JSONPropertyInfo;

/**
 * Contains some util methods to handle bean properties dynamically.
 *
 * @author fforw at gmx dot de
 *
 */
public class JSONBeanUtil
{
    /**
     * Returns the names of all properties of this dynamic properties object including the java bean properties.
     * Note that the method will return the <em>JSON property name</em> of the java bean methods.
     * @param bean     DynamicProperties object
     * @return a set containing all property names, both dynamic and static (JSON) names.
     */
    public static Set<String> getAllPropertyNames(Object bean)
    {

        Set<String> names  = new HashSet<String>( );

        if (bean instanceof DynamicProperties)
        {
            names.addAll(((DynamicProperties)bean).propertyNames());
        }
        if (bean instanceof Map)
        {
            names.addAll(((Map)bean).keySet());
        }
        names.addAll( getBeanPropertyNames(bean));
        return names;
    }

    /**
     * Returns all readable and writable bean property JSON names of the given object.
     * @param bean object
     * @return
     */
    public static Set<String> getBeanPropertyNames(Object bean)
    {
        return JSONClassInfo.forClass(bean.getClass()).getPropertyNames();
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
    public static Object getProperty(Object bean, String name)
        throws IllegalArgumentException
    {
        JSONPropertyInfo propertyInfo;
        if (bean instanceof Map)
        {
            return ((Map)bean).get(name);
        }
        else if ((propertyInfo = JSONClassInfo.forClass(bean.getClass()).getPropertyInfo(name)) != null && propertyInfo.isReadable())
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
    public static void setProperty(Object bean, String name, Object value)
        throws IllegalArgumentException
    {
        JSONPropertyInfo propertyInfo;
        if (bean instanceof Map)
        {
            ((Map)bean).put(name, value);
        }
        else if ((propertyInfo = JSONClassInfo.forClass(bean.getClass()).getPropertyInfo(name)) != null && propertyInfo.isWriteable())
        {
            propertyInfo.setProperty(bean, value);
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
}
