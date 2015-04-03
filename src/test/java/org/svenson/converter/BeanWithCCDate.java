package org.svenson.converter;

import java.util.Date;

public class BeanWithCCDate
{
    private Date date;
    
    @JSONConverter( type = ComplexDateConverter.class)
    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }
    
    
}
