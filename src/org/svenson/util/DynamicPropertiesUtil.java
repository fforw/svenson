package org.svenson.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.svenson.DynamicProperties;
import org.svenson.JSONParser;

/**
 * Contains some util methods to handle bean properties dynamically.
 *
 * @author shelmberger
 *
 */
public class DynamicPropertiesUtil
{
    /**
     * Returns the names of all properties of this dynamic properties object including the java bean properties.
     * Note that the method will return the <em>JSON property name</em> of the java bean methods.
     * @param dynamicProperties     DynamicProperties object
     * @return a set containing all property names, both dynamic and static (JSON) names.
     */
    public static Set<String> getAllPropertyNames(DynamicProperties dynamicProperties)
    {
        Set<String> names  = new HashSet<String>( dynamicProperties.propertyNames());
        names.addAll( getBeanPropertyNames(dynamicProperties));
        return names;
    }

    /**
     * Returns all readable and writable bean property JSON names of the given object.
     * @param dynamicProperties object
     * @return
     */
    public static Set<String> getBeanPropertyNames(Object dynamicProperties)
    {
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(dynamicProperties.getClass());
        Set<String> names  = new HashSet<String>();
        for (PropertyDescriptor pd : pds)
        {
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();
            if (readMethod != null && writeMethod != null)
            {
               String name = JSONParser.getJSONPropertyNameFromDescriptor(dynamicProperties, pd);
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
     * @param dynamicProperties java bean
     * @param name JSON property name
     * @return the property value.
     * @throws IllegalArgumentException if there is no bean property with the
     *             given name on the given dynamicProperties object and the
     *             class of the bean does not implement
     *             {@link DynamicProperties}
     */
    public static Object getProperty(Object dynamicProperties, String name)
        throws IllegalArgumentException
    {
        try
        {
            String propertyName = JSONParser.getPropertyNameFromAnnotation(dynamicProperties, name);
            if (PropertyUtils.isReadable(dynamicProperties, propertyName))
            {
                return PropertyUtils.getProperty(dynamicProperties, propertyName);
            }
            else if (dynamicProperties instanceof DynamicProperties)
            {
                return ((DynamicProperties) dynamicProperties).getProperty(name);
            }
            else if (dynamicProperties instanceof Map)
            {
                return ((Map)dynamicProperties).get(name);
            }
            else
            {
                throw new IllegalArgumentException(dynamicProperties +
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
     * @param dynamicProperties bean or dynamic properties instance
     * @param name JSON property name
     * @param value property value
     * @throws IllegalArgumentException if there is no bean property with the
     *             given name on the given dynamicProperties object and the
     *             class of the bean does not implement
     *             {@link DynamicProperties}
     */
    public static void setProperty(Object dynamicProperties, String name, Object value)
        throws IllegalArgumentException
    {
        try
        {
            String propertyName = JSONParser.getPropertyNameFromAnnotation(dynamicProperties, name);
            if (PropertyUtils.isWriteable(dynamicProperties, propertyName))
            {
                PropertyUtils.setProperty(dynamicProperties, propertyName, value);
            }
            else if (dynamicProperties instanceof DynamicProperties)
            {
                ((DynamicProperties) dynamicProperties).setProperty(name, value);
            }
            else if (dynamicProperties instanceof Map)
            {
                ((Map)dynamicProperties).put(name, value);
            }
            else
            {
                throw new IllegalArgumentException(dynamicProperties +
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
