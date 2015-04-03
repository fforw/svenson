package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Map;

import org.junit.Test;
import org.svenson.test.BeanWithEnumMap;
import org.svenson.test.SomeEnum;

public class EnumHashMapTestCase
{
    @Test
    public void testParseMap()
    {
        JSONParser parser = JSONParser.defaultJSONParser();
        
        BeanWithEnumMap bean = parser.parse(BeanWithEnumMap.class, "{\"enums\":{\"aaa\":\"VAL1\",\"bbb\":\"VAL2\"}}");
        
        assertThat(bean, is(notNullValue()));
        Map<String, SomeEnum> enums = bean.getEnums();
        assertThat(enums.size(), is(2));
        assertThat(enums.get("aaa") == SomeEnum.VAL1, is(true));
        assertThat(enums.get("bbb") == SomeEnum.VAL2, is(true));
        
    }
}
