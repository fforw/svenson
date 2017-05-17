package org.svenson;

/**
 * Factory that can create user objects with special dependencies from within svenson.
 * 
 * @see JSONParser#addObjectFactory(ObjectFactory)
 * 
 * @author fforw at gmx dot de
 * 
 * @param <T> Base type of types to create
 */
public interface ObjectFactory<T>
{
    /**
     * Returns <code>true</code> if the factory can create objects of the given class
     * 
     * @param cls   Class to check for support of
     * @return  <code>true</code> if the given class can be created by this factory
     */
    boolean supports(Class<T> cls);
    
    /**
     * Creates an instance of the given type.
     * @param cls   Class to create an instance of
     * @return  new instance
     */
    T create(Class<T> cls);
}
