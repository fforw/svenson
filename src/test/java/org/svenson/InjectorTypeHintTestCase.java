package org.svenson;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.test.InjectorHint;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class InjectorTypeHintTestCase
{
    private static Logger log = LoggerFactory.getLogger(InjectorTypeHintTestCase.class);

    @Test
    public void testInjectorHint() throws Exception
    {
        InjectorHint hint = JSONParser.defaultJSONParser().parse(InjectorHint.class, "{\n" +
            "  \"foo\" : { \"value\" : \"bar\" }\n" +
            "}");


        assertThat(hint.getInjected().get("foo").getValue(), is("bar"));
    }
}
