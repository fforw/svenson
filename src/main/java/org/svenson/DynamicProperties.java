package org.svenson;

import java.util.Set;

/**
 * Interface that allows classes to also have dynamic properties
 *
 * @author fforw at gmx dot de
 *
 */
public interface DynamicProperties
{
    /**
     * Sets the attribute with the given name to the given value.
     *
     * @param name      property name
     * @param value     if <code>null</code>, the attribute is removed.
     */
    void setProperty(String name, Object value);

    /**
     * returns value of the attribute with the given name.
     *
     * @param name      property name
     *
     * @return property value
     */
    Object getProperty(String name);

    /**
     * Returns the set of available dynamic attribute names.
     *
     * @return set of available dynamic attribute names
     */
    Set<String> propertyNames();


    /**
     * Returns <code>true</code> if this DynamicProperties object has a property with the given name.
     *
     * @param name      property name
     *
     * @return <code>true</code> if this DynamicProperties object has a property with the given name
     */

    boolean hasProperty(String name);

    /**
     * Removes the property with the given name from this DynamicProperties object.
     *
     * @param name      property name
     *
     * @return  the previous value of the removed property.
     */
    Object removeProperty(String name);
}
