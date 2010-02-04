package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.Test;

public class IgnoreReadTestCase
{
    @Test
    public void test()
    {
        String json = JSON.defaultJSON().forValue(new MyDoc());
        assertThat(json, is("{}"));
    }


    public class MyDoc  
    {
       @JSONProperty(ignore=true)
       public List getXYZ() 
       {
           throw new IllegalStateException("Should not be called");
       }
    }
    
}
