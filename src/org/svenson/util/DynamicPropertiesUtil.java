package org.svenson.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.svenson.DynamicProperties;
import org.svenson.JSONParser;

/**
 * Renamed to {@link JSONBeanUtil}.
 *
 * @author fforw at gmx dot de
 * @deprecated
 *
 */
@Deprecated
public class DynamicPropertiesUtil
{
    /**
     * Returns the names of all properties of this dynamic properties object including the java bean properties.
     * Note that the method will return the <em>JSON property name</em> of the java bean methods.
     * @param dynamicProperties     DynamicProperties object
     * @return a set containing all property names, both dynamic and static (JSON) names.
     * @deprecated use JSONBeanUtil
     */
    @Deprecated
    public static Set<String> getAllPropertyNames(DynamicProperties dynamicProperties)
    {
        return JSONBeanUtil.getAllPropertyNames(dynamicProperties);
    }

    /**
     * Returns all readable and writable bean property JSON names of the given object.
     * @param dynamicProperties object
     * @return
     * @deprecated use JSONBeanUtil
     */
    @Deprecated
    public static Set<String> getBeanPropertyNames(Object dynamicProperties)
    {
        return JSONBeanUtil.getBeanPropertyNames(dynamicProperties);
    }

    /**
     * Gets the bean or dynamic property with the given JSON property name. if
     * the class has a bean property with the given name, the value of that
     * property is returned. otherwise, the dynamic property with the given name
     * is returned.
     *
     * @param dynamicProperties java bean
     * @param name JSON property name
     * @return the property value.
     * @throws IllegalArgumentException if there is no bean property with the
     *             given name on the given dynamicProperties object and the
     *             class of the bean does not implement
     *             {@link DynamicProperties}
     * @deprecated use JSONBeanUtil
     */
    @Deprecated
    public static Object getProperty(Object dynamicProperties, String name)
        throws IllegalArgumentException
    {
        return JSONBeanUtil.getProperty(dynamicProperties, name);
    }

    /**
     * Sets the bean or dynamic property with the given JSON property name to
     * the given value. if the class has a bean property with the given name,
     * the value of that property is overwritten. otherwise, the dynamic
     * property with the given name is overwritten.
     *
     * @param dynamicProperties bean or dynamic properties instance
     * @param name JSON property name
     * @param value property value
     * @throws IllegalArgumentException if there is no bean property with the
     *             given name on the given dynamicProperties object and the
     *             class of the bean does not implement
     *             {@link DynamicProperties}
     * @deprecated use JSONBeanUtil
     */
    @Deprecated
    public static void setProperty(Object dynamicProperties, String name, Object value)
        throws IllegalArgumentException
    {
        JSONBeanUtil.setProperty(dynamicProperties, name, value);
    }
}
