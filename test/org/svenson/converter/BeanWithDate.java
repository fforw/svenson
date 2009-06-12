package org.svenson.converter;

import java.util.Date;

public class BeanWithDate
{
    private Date date;
    
    @JSONConverter( type = DateConverter.class)
    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }
    
    
}
