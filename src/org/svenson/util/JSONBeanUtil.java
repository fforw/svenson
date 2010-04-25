package org.svenson.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.svenson.DynamicProperties;
import org.svenson.JSONParseException;
import org.svenson.JSONParser;
import org.svenson.JSONTypeHint;
import org.svenson.ObjectFactory;

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
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(bean.getClass());
        Set<String> names  = new HashSet<String>();
        for (PropertyDescriptor pd : pds)
        {
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();
            if (readMethod != null && writeMethod != null)
            {
               String name = JSONParser.getJSONPropertyNameFromDescriptor(bean, pd);
               names.add(name);
            }
        }
        return names;
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
        try
        {
            String propertyName = JSONParser.getPropertyNameFromAnnotation(bean, name);
            if (propertyName != null && PropertyUtils.isReadable(bean, propertyName))
            {
                return PropertyUtils.getProperty(bean, propertyName);
            }
            else if (bean instanceof DynamicProperties)
            {
                return ((DynamicProperties) bean).getProperty(name);
            }
            else if (bean instanceof Map)
            {
                return ((Map)bean).get(name);
            }
            else
            {
                throw new IllegalArgumentException(bean +
                    " has no JSON property with the name '" + name +
                    "' and does not implements DynamicProperties");
            }
        }
        catch (IllegalAccessException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (InvocationTargetException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (NoSuchMethodException e)
        {
            throw ExceptionWrapper.wrap(e);
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
        try
        {
            String propertyName = JSONParser.getPropertyNameFromAnnotation(bean, name);
            if (propertyName != null && PropertyUtils.isWriteable(bean, propertyName))
            {
                PropertyUtils.setProperty(bean, propertyName, value);
            }
            else if (bean instanceof DynamicProperties)
            {
                ((DynamicProperties) bean).setProperty(name, value);
            }
            else if (bean instanceof Map)
            {
                ((Map)bean).put(name, value);
            }
            else
            {
                throw new IllegalArgumentException(bean +
                    " has no JSON property with the name '" + name +
                    "' and does not implements DynamicProperties");
            }
        }
        catch (IllegalAccessException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (InvocationTargetException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (NoSuchMethodException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
    }
}
