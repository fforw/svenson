package org.svenson;

public class ClassNameBasedTypeMapper extends AbstractPropertyValueBasedTypeMapper
{
    @Override
    protected Class getTypeHintFromTypeProperty(Object o) throws IllegalStateException
    {
        String value =(String) o;

        if (value == null)
        {
            throw new IllegalArgumentException("class name can't be null");
        }
        
        try
        {
            return Class.forName((String)o);
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalStateException("Invalid class name in JSON property", e);
        }
    }
}
