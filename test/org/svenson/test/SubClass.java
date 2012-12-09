package org.svenson.test;

import java.util.Date;

import org.svenson.converter.JSONConverter;

public class SubClass
    extends SuperClass<Date>
{

    @Override
    @JSONConverter(type = MyDateConverter.class)
    public Date getValue()
    {
        return super.getValue();
    }
}
