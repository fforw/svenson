package org.svenson.info;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.svenson.test.SubClass;
import org.svenson.test.SuperClass;


public class JavaObjectSupportTestCase
{
    @Test
    public void testOverride()
    {
        assertThat(JavaObjectSupport.isOveriding(Object.class, null), is(true));
        assertThat(JavaObjectSupport.isOveriding(SubClass.class, Object.class), is(true));
        assertThat(JavaObjectSupport.isOveriding(SuperClass.class, Object.class), is(true));
        assertThat(JavaObjectSupport.isOveriding(SubClass.class, SuperClass.class), is(true));

        assertThat(JavaObjectSupport.isOveriding(SuperClass.class, SubClass.class), is(false));
        assertThat(JavaObjectSupport.isOveriding(Object.class, SuperClass.class), is(false));
        assertThat(JavaObjectSupport.isOveriding(Object.class, SubClass.class), is(false));
    }

}
