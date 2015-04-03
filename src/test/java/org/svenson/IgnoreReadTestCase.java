package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.Test;
import org.svenson.test.Bean;

public class IgnoreReadTestCase
{
    @Test
    public void test()
    {
        String json = JSON.defaultJSON().forValue(new MyDoc());
        assertThat(json, is("{}"));
    }
    
    @Test
    public void testNullValue()
    {
        Bean bean = new Bean();
        String json = JSON.defaultJSON().forValue(bean);
        
        assertThat(json, containsString("\"foo\":null"));
        assertThat(json, containsString("\"bar\":0"));
        
        System.out.println(json);
        
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
