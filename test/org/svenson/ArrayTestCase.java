package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.test.BeanWithArray;

public class ArrayTestCase
{
    private static Logger log = LoggerFactory.getLogger(ArrayTestCase.class);
    
    @Test
    public void thatGeneratingWorks()
    {
        JSON json = JSON.defaultJSON();
        
        BeanWithArray bean = new BeanWithArray();
        bean.setFoo(new String[]{"one","two"});
        String out = json.forValue(bean );
        
        assertThat(out, is("{\"foo\":[\"one\",\"two\"]}"));
    }

    @Test
    public void thatParsingWorks()
    {
        JSONParser jsonParser = JSONParser.defaultJSONParser();
        
        BeanWithArray bean = jsonParser.parse(BeanWithArray.class, "{\"foo\":[\"one\",\"two\"]}");
        
        assertThat(bean, is(notNullValue()));
        assertThat(bean.getFoo().length, is(2));
        assertThat(bean.getFoo()[0], is("one"));
        assertThat(bean.getFoo()[1], is("two"));
        assertThat(bean.getFoo().length, is(2));
    }

}
