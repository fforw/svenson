package org.svenson;

public interface ObjectFactory<T>
{
    /**
     * Returns <code>true</code> if the factory can create objects of the given class
     * 
     * @param cls
     * @return
     */
    boolean supports(Class<T> cls);
    
    /**
     * Creates an instance of the given type.
     * @param typeHint
     * @return
     */
    T create(Class<T> typeHint);
}
