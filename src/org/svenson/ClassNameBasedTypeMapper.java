package org.svenson;

public class ClassNameBasedTypeMapper extends AbstractPropertyValueBasedTypeMapper
{
    @Override
    protected Class getTypeHintFromTypeProperty(Object value) throws IllegalStateException
    {
        try
        {
            return Class.forName((String)value);
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalStateException("Invalid class name in JSON property", e);
        }
    }
}
