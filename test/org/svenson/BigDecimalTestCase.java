package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.math.BigDecimal;

import org.junit.Test;

public class BigDecimalTestCase
{
    @Test
    public void thatMinAndMaxWork()
    {
        final String value = "42376534098645709365439065398763908756203956043987639287564089653498765.243756238756287562784";
        BigDecimal dec = new BigDecimal(value);
        Bean bean = new Bean(dec);
 
        String json = JSON.defaultJSON().forValue(bean);
        assertThat(json, is("{\"value\":" + value +"}"));
        
        bean = JSONParser.defaultJSONParser().parse(Bean.class, json);
        
        assertThat(bean.getValue(), is(dec));
        
    }
    
    public static class Bean
    {
        private BigDecimal value;

        public Bean()
        {
            
        }
        
        public Bean(BigDecimal value)
        {
            this.value = value;
        }

        public BigDecimal getValue()
        {
            return value;
        }

        public void setValue(BigDecimal value)
        {
            this.value = value;
        }
        
        
    }
}
