package org.svenson.matcher;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;


public class MapValuePathMatcherTestCase
{
    @Test
    public void thatitWorks()
    {
        MapValuePathMatcher m = new MapValuePathMatcher();
        assertThat( m.matches(".foo", null), is(true));
        assertThat( m.matches(".bar", null), is(true));
        assertThat( m.matches("", null), is(false));
        assertThat( m.matches("[]", null), is(false));
        assertThat( m.matches(".foo[]", null), is(false));
    }
}
