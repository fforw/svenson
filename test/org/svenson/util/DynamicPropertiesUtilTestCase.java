package org.svenson.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Set;

import org.junit.Test;
import org.svenson.DynAttrsBean;


public class DynamicPropertiesUtilTestCase
{
    @Test
    public void thatGetAllPropertyNamesWorks()
    {
        DynAttrsBean bean = new DynAttrsBean();
        bean.setFoo("bar");
        bean.setProperty("baz", 42);

        Set<String> names = DynamicPropertiesUtil.getAllPropertyNames(bean);

        assertThat(names, is(notNullValue()));
        assertThat(names.size(),is(2));
        assertThat(names.contains("foo"),is(true));
        assertThat(names.contains("baz"),is(true));

    }

    @Test
    public void thatGetBeanPropertyNamesWorks()
    {
        DynAttrsBean bean = new DynAttrsBean();

        Set<String> names = DynamicPropertiesUtil.getBeanPropertyNames(bean);

        assertThat(names, is(notNullValue()));
        assertThat(names.size(),is(1));
        assertThat(names.contains("foo"),is(true));

    }

    @Test
    public void thatReadingBeanPropertiesWorks()
    {
        DynAttrsBean bean = new DynAttrsBean();
        bean.setFoo("bar!");
        assertThat((String)DynamicPropertiesUtil.getProperty(bean, "foo"), is("bar!"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatReadingUndefinedBeanPropertiesDoesNotWork()
    {
        Object bean = new Object();
        DynamicPropertiesUtil.getProperty(bean, "bar");
    }

    @Test
    public void thatWritingBeanPropertiesWorks()
    {
        DynAttrsBean bean = new DynAttrsBean();

        DynamicPropertiesUtil.setProperty(bean, "foo", "bar!");
        assertThat(bean.getFoo(), is("bar!"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatWritingUndefinedBeanPropertiesDoesNotWork()
    {
        Object bean = new Object();
        DynamicPropertiesUtil.setProperty(bean, "bar", "bar!");
    }
}
