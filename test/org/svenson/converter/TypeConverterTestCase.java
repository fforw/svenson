package org.svenson.converter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Date;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.svenson.JSON;
import org.svenson.JSONParser;

public class TypeConverterTestCase
{
    private DefaultTypeConverterRepository typeConverterRepository;
    @Before
    public void init()
    {
        typeConverterRepository = new DefaultTypeConverterRepository();
        typeConverterRepository.addTypeConverter(new DateConverter());
        typeConverterRepository.addTypeConverter(new ComplexDateConverter());
    }
    
    
    @Test
    public void thatJSONParsingWorks()
    {
        String json = "{\"date\":\"1970-01-01T00:00:00\"}";

        JSONParser parser = new JSONParser();
        parser.setTypeConverterRepository(typeConverterRepository);
        
        BeanWithDate bean = parser.parse(BeanWithDate.class, json);
        assertThat(bean.getDate(), is(new Date(0)));
    }

    @Test
    public void thatJSONGeneratingWorks()
    {
        BeanWithDate bean = new BeanWithDate();
        bean.setDate(new Date(0));
        
        JSON jsonGenerator = new JSON();
        
        jsonGenerator.setTypeConverterRepository(typeConverterRepository);
        
        assertThat(jsonGenerator.forValue(bean), is("{\"date\":\"1970-01-01T00:00:00\"}"));
    }

    @Test
    public void thatComplexJSONGeneratingWorks()
    {
        BeanWithCCDate bean = new BeanWithCCDate();
        bean.setDate(new Date(0));
        
        JSON jsonGenerator = new JSON();
        
        jsonGenerator.setTypeConverterRepository(typeConverterRepository);
        
        assertThat(jsonGenerator.forValue(bean), is("{\"date\":[70,0,1,1,0,0]}"));
    }
    
    @Test
    public void thatComplexJSONParsingWorks()
    {
        String json = "{\"date\":[70,0,1,1,0,0]}";
        
        JSONParser parser = new JSONParser();
        parser.setTypeConverterRepository(typeConverterRepository);
        
        BeanWithCCDate bean = parser.parse(BeanWithCCDate.class, json);
        assertThat(bean.getDate(), is(new Date(0)));
    }
}
