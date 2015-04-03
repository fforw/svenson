package org.svenson.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Set;

import org.junit.Test;
import org.svenson.test.Bean;
import org.svenson.test.BeanWithArray;
import org.svenson.test.BeanWrapper;
import org.svenson.test.InnerBean;


public class JSONBeanUtilTestCase
{
    @Test
    public void thatGetAllPropertyNamesWorks()
    {
        DynPropTestBean bean = new DynPropTestBean();
        bean.setFoo("bar");
        bean.setProperty("baz", 42);

        Set<String> names = JSONBeanUtil.defaultUtil().getAllPropertyNames(bean);

        assertThat(names, is(notNullValue()));
        assertThat(names.size(),is(2));
        assertThat(names.contains("_foo"),is(true));
        assertThat(names.contains("baz"),is(true));

    }

    @Test
    public void thatGetBeanPropertyNamesWorks()
    {
        DynPropTestBean bean = new DynPropTestBean();

        Set<String> names = JSONBeanUtil.defaultUtil().getBeanPropertyNames(bean);

        assertThat(names, is(notNullValue()));
        assertThat(names.size(),is(1));
        assertThat(names.contains("_foo"),is(true));

    }

    @Test
    public void thatReadingBeanPropertiesWorks()
    {
        DynPropTestBean bean = new DynPropTestBean();
        bean.setFoo("bar!");
        assertThat((String)JSONBeanUtil.defaultUtil().getProperty(bean, "_foo"), is("bar!"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatReadingUndefinedBeanPropertiesDoesNotWork()
    {
        Object bean = new Object();
        JSONBeanUtil.defaultUtil().getProperty(bean, "bar");
    }

    @Test
    public void thatWritingBeanPropertiesWorks()
    {
        DynPropTestBean bean = new DynPropTestBean();

        JSONBeanUtil.defaultUtil().setProperty(bean, "_foo", "bar!");
        assertThat(bean.getFoo(), is("bar!"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatWritingUndefinedBeanPropertiesDoesNotWork()
    {
        Object bean = new Object();
        JSONBeanUtil.defaultUtil().setProperty(bean, "bar", "bar!");
    }
}
