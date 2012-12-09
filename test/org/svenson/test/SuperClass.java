package org.svenson.test;

import java.util.Date;

import org.svenson.converter.JSONConverter;

public class SuperClass<T>
{

    protected T value;


    public void setValue(final T value)
    {
        this.value = value;
    }


    public T getValue()
    {
        return value;
    }


    @JSONConverter(type = MyDateConverter.class)
    public Date getValueNotOverridden()
    {
        return (Date) value;
    }

}
