package org.svenson.test;

import java.util.ArrayList;
import java.util.List;

public class BeanWithStringAdder
{
    private List<String> values = new ArrayList<String>();
    
    public void addValues(String s)
    {
        values.add(s);
    }
    
    public List<String> getValues()
    {
        return values;
    }
}
