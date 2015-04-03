package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;

public class IgnoreWriteTestCase
{
    @Test
    public void thatIgnoreWriteWorks()
    {
        JSON jsonGenerator = JSON.defaultJSON();
        JSONParser jsonParser = JSONParser.defaultJSONParser();
        
        String json = jsonGenerator.forValue(new WriteTestBean());
        assertThat(json, containsString("\"type\":\"writeTestBean\""));
        assertThat(json, containsString("\"foo\":42"));
        
        WriteTestBean bean = jsonParser.parse(WriteTestBean.class, "{\"type\":\"writeTestBean\",\"foo\":12}");
        assertThat(bean, is(notNullValue()));
        assertThat(bean.getFoo(), is(12));
    }
    
    @Test(expected = JSONParseException.class )
    public void thatNotIgnoringWorks()
    {
        JSONParser jsonParser = JSONParser.defaultJSONParser();
        
        jsonParser.parse(WriteTestBean2.class, "{\"type\":\"writeTestBean2\"}");
    }
    
    public static class WriteTestBean
    {
        private String type = "writeTestBean";
        
        private int foo = 42;
        
        @JSONProperty(readOnly = true)
        public String getType()
        {
            return type;
        }
        
        public int getFoo()
        {
            return foo;
        }
        
        public void setFoo(int foo)
        {
            this.foo = foo;
        }
    }

    public static class WriteTestBean2
    {
        private String type = "writeTestBean2";
        
        public String getType()
        {
            return type;
        }
    }
}
