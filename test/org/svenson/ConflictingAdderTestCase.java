package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

public class ConflictingAdderTestCase
{
    
    @Test
    public void test()
    {
        Bean bean = new Bean();
        List<String> foos = Arrays.asList("bar", "baz");
        bean.setFoo(foos);
        
        String json = JSON.defaultJSON().forValue(bean);
        
        assertThat(json, is("{\"foo\":[\"bar\",\"baz\"]}"));
        
        Bean b2 = JSONParser.defaultJSONParser().parse(Bean.class, json);
        assertThat(b2.getFoo(), is(foos));
    }
    
    public static class Bean 
    {
        private List<String> foo;
        
        public void setFoo(List<String> foo)
        {
            this.foo = foo;
        }
        
        public List<String> getFoo()
        {
            if (foo == null)
            {
                return Collections.emptyList();
            }
            return foo;
        }
        
        public void addFoo(List<String> s)
        {
            if (foo == null)
            {
                foo = new ArrayList<String>();
            }
            this.foo.addAll(s);
        }
    }
}
