package org.svenson.info;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.test.FunkyNonProperties;
import org.svenson.test.SubClass;
import org.svenson.test.SuperClass;


public class JavaObjectSupportTestCase
{
    private final static Logger log = LoggerFactory.getLogger(JavaObjectSupportTestCase.class);


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


    @Test
    public void testFunkyNonProps()
    {
        final JSONClassInfo classInfo = new JavaObjectSupport().createClassInfo(FunkyNonProperties.class);
        assertThat(classInfo.getPropertyNames().size(), is(0));
    }
}
