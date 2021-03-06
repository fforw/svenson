package org.svenson;

/**
 * Typemapper that maps simple class name strings to classes, with configurable base package
 * and optional base type enforcement.
 * 
 * @author fforw at gmx dot de
 *
 */
public class ClassNameBasedTypeMapper extends AbstractPropertyValueBasedTypeMapper
{
    private String basePackage;
    
    private Class enforcedBaseType;
    
    /**
     * Sets the base type to which all created instances must be assignable to.
     * 
     * @param enforcedBaseType  if <code>true</code>, make sure that the instances create are assignable to the given
     *                          base type
     */
    public void setEnforcedBaseType(Class enforcedBaseType)
    {
        this.enforcedBaseType = enforcedBaseType;
    }
    
    /**
     * Sets the base package that is put before the given type to form the fully qualified
     * class names.
     * 
     * @param basePackage   the base package that is put before the given type to form the fully qualified
     *                      class names
     */
    public void setBasePackage(String basePackage)
    {
        this.basePackage = basePackage;
    }
    
    @Override
    protected Class getTypeHintFromTypeProperty(Object o) throws IllegalStateException
    {
        if (o == null)
        {
            throw new IllegalArgumentException("class name can't be null");
        }

        String value =(String) o;

        if (basePackage != null)
        {
            value = basePackage + "." + value; 
        }
        
        try
        {
            Class cls = Class.forName(value);
            if (enforcedBaseType != null && !enforcedBaseType.isAssignableFrom(cls))
            {
                throw new IllegalStateException(cls + " is not assignable to " + enforcedBaseType);
            }
            return cls;
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalStateException("No class found for property '" + value + "'", e);
        }
    }
}
