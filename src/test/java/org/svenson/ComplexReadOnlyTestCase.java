package org.svenson;

import org.junit.Test;
import org.svenson.test.ComplexReadOnly;
import org.svenson.test.ComplexReadOnlyChild;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class ComplexReadOnlyTestCase
{
    @Test
    public void testComplexReadOnlyParsing() throws Exception
    {

        ComplexReadOnlyChild.reset();
        ComplexReadOnly cro = JSONParser.defaultJSONParser().parse(ComplexReadOnly.class, "{\"kid\":{}," +
            "\"kidArray\":[{},{}]}");
        assertThat(ComplexReadOnlyChild.getCount(), is(0));

    }
}
