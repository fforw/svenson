package org.svenson;

public class ClassNameBasedTypeMapper extends AbstractPropertyValueBasedTypeMapper
{
    private String basePackage;
    
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
            return Class.forName(value);
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalStateException("Invalid class name in JSON property", e);
        }
    }
}
