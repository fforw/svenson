package org.svenson;


import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.converter.DefaultTypeConverterRepository;
import org.svenson.test.MyDateConverter;
import org.svenson.test.TypeConverterCachingBean;

import java.util.Date;
import java.util.regex.Pattern;

public class TypeConverterCachingTestCase
{
    private static Logger log = LoggerFactory.getLogger(TypeConverterCachingTestCase.class);


    private final static Pattern CHECK = Pattern.compile("^\\{\"timestamp\":[0-9]+\\}$");


    // we don't cache the typeconverter evaluation for now, but the requirement expressed here  is still valid
    @Test
    public void testTypeConverterScenario()
    {
        TypeConverterCachingBean demoObject = new TypeConverterCachingBean();
        demoObject.setTimestamp(new Date());

        // This converts the Date field NOT using the type converter since it is not registered
        JSON jsonWithoutTypeConverter = new JSON();

        String result = jsonWithoutTypeConverter.forValue(demoObject);

        // timestamp is converted into a complex object
        assertThat(result,startsWith("{\"timestamp\":{"));

        // This should convert the Date field using the type converter
        // It doesn't since the TypeAnalyzer stores type information in a static HashMap
        JSON jsonWithTypeConverter = new JSON();
        DefaultTypeConverterRepository typeConverterRepository = new DefaultTypeConverterRepository();
        typeConverterRepository.addTypeConverter(new MyDateConverter());
        jsonWithTypeConverter.setTypeConverterRepository(typeConverterRepository);
        String result2 = jsonWithTypeConverter.forValue(demoObject);

        log.info("Second conversion with converter: {}", result2);
        assertThat("Converted output does not match expected pattern", CHECK.matcher(result2).matches(),is(true));
    }
}
