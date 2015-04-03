package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Date;

import org.apache.commons.beanutils.ConversionException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.converter.DateConverter;

public class TypeConverterTestCase
{
    private static Logger log = LoggerFactory.getLogger(TypeConverterTestCase.class);
    
    @Test
    public void testJSONifying()
    {
        JSON gen = new JSON();
        gen.registerTypeConversion(Date.class, new DateConverter());
        String v = gen.forValue(new Bean());
        assertThat(v, is("{\"date\":\"1970-01-01T00:00:00\"}"));
        v = JSON.defaultJSON().forValue(new Bean());
        assertThat(v, is(not("{\"date\":\"1970-01-01T00:00:00\"}")));
    }
    
    @Test
    public void testJSONParsing()
    {
        JSONParser parser = new JSONParser();
        parser.registerTypeConversion(Date.class, new DateConverter());
        Bean b = parser.parse(Bean.class, "{\"date\":\"1970-12-14T20:00:00\"}");
        assertThat(b.getDate().getTime(),is(30052800000L));
    }
    
    @Test(expected = ConversionException.class)
    public void testJSONParsing2()
    {
        Bean b = JSONParser.defaultJSONParser().parse(Bean.class, "{\"date\":\"1970-12-14T20:00:00\"}");
    }
    
    public static class Bean
    {
        private Date date = new Date(0);

        public Date getDate()
        {
            return date;
        }

        public void setDate(Date date)
        {
            this.date = date;
        }
        
    }

}
