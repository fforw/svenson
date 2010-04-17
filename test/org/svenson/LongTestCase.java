package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class LongTestCase
{
    @Test
    public void thatMinAndMaxWork()
    {
        String minJSON = JSON.defaultJSON().forValue(new Bean(Long.MIN_VALUE));
        String maxJSON = JSON.defaultJSON().forValue(new Bean(Long.MAX_VALUE));
        assertThat(minJSON, is("{\"value\":" + Long.MIN_VALUE + "}"));
        assertThat(maxJSON, is("{\"value\":" + Long.MAX_VALUE + "}"));

        Bean minBean = JSONParser.defaultJSONParser().parse(Bean.class, minJSON);
        Bean maxBean = JSONParser.defaultJSONParser().parse(Bean.class, maxJSON);
        
        assertThat(minBean.getValue(), is(Long.MIN_VALUE));
        assertThat(maxBean.getValue(), is(Long.MAX_VALUE));
        
    }
    
    public static class Bean
    {
        private long value;

        public Bean()
        {
            
        }
        
        public Bean(long value)
        {
            this.value = value;
        }

        public long getValue()
        {
            return value;
        }

        public void setValue(long value)
        {
            this.value = value;
        }
        
        
    }
}
