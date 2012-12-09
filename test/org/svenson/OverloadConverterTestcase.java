package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

import java.util.Date;

import org.junit.Test;
import org.svenson.converter.DefaultTypeConverterRepository;
import org.svenson.test.MyDateConverter;
import org.svenson.test.SubClass;

public class OverloadConverterTestcase
{
    @Test
    public void test()
    {
        SubClass subClass = new SubClass();
        subClass.setValue(new Date(1000000));

        // Register my own type converter for Date
        DefaultTypeConverterRepository typeConverterRepository = new DefaultTypeConverterRepository();
        typeConverterRepository.addTypeConverter(new MyDateConverter());

        // Convert to JSON.
        JSON gen = new JSON();
        gen.setTypeConverterRepository(typeConverterRepository);
        String json = gen.forValue(subClass);
        System.out.println(json);        
        assertThat(json, containsString("\"value\":1000000"));
    }
}
