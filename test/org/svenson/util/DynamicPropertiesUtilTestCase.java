package org.svenson.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Set;

import org.junit.Test;


public class DynamicPropertiesUtilTestCase
{
    @Test
    public void thatGetAllPropertyNamesWorks()
    {
        DynPropTestBean bean = new DynPropTestBean();
        bean.setFoo("bar");
        bean.setProperty("baz", 42);

        Set<String> names = DynamicPropertiesUtil.getAllPropertyNames(bean);

        assertThat(names, is(notNullValue()));
        assertThat(names.size(),is(2));
        assertThat(names.contains("_foo"),is(true));
        assertThat(names.contains("baz"),is(true));

    }

    @Test
    public void thatGetBeanPropertyNamesWorks()
    {
        DynPropTestBean bean = new DynPropTestBean();

        Set<String> names = DynamicPropertiesUtil.getBeanPropertyNames(bean);

        assertThat(names, is(notNullValue()));
        assertThat(names.size(),is(1));
        assertThat(names.contains("_foo"),is(true));

    }

    @Test
    public void thatReadingBeanPropertiesWorks()
    {
        DynPropTestBean bean = new DynPropTestBean();
        bean.setFoo("bar!");
        assertThat((String)DynamicPropertiesUtil.getProperty(bean, "_foo"), is("bar!"));
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
        DynPropTestBean bean = new DynPropTestBean();

        DynamicPropertiesUtil.setProperty(bean, "_foo", "bar!");
        assertThat(bean.getFoo(), is("bar!"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatWritingUndefinedBeanPropertiesDoesNotWork()
    {
        Object bean = new Object();
        DynamicPropertiesUtil.setProperty(bean, "bar", "bar!");
    }
}
