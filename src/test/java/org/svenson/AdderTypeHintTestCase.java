package org.svenson;

import org.junit.Test;
import org.svenson.test.AdderTypeHint;
import org.svenson.test.Bar;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class AdderTypeHintTestCase
{
    @Test
    public void test()
    {
        JSONParser parser = JSONParser.defaultJSONParser();

        AdderTypeHint obj = parser.parse(AdderTypeHint.class, "{\"bars\" :[{}]}");

        assertThat(obj, is(notNullValue()));
        assertThat(obj.getBars(), is(notNullValue()));
        assertThat(obj.getBars().get(0), is(Bar.class));
    }

}
