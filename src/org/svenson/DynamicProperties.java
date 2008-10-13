package org.svenson;

import java.util.Set;

/**
 * Interface that allows classes to also have dynamic attributes
 *
 * @author shelmberger
 *
 */
public interface DynamicProperties
{
    /**
     * Sets the attribute with the given name to the given value.
     *
     * @param name
     * @param value if <code>null</code>, the attribute is removed.
     */
    void setProperty(String name, Object value);

    /**
     * returns value of the attribute with the given name.
     *
     * @param name
     */
    Object getProperty(String name);

    /**
     * Returns the set of available attribute names.
     *
     * @return
     */
    Set<String> propertyNames();
}
