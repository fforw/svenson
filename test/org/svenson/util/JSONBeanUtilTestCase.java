package org.svenson.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.svenson.test.Bean;
import org.svenson.test.BeanWithArray;
import org.svenson.test.BeanWrapper;
import org.svenson.test.FooBean;
import org.svenson.test.InnerBean;


public class JSONBeanUtilTestCase
{
    @Test
    public void thatGetAllPropertyNamesWorks()
    {
        DynPropTestBean bean = new DynPropTestBean();
        bean.setFoo("bar");
        bean.setProperty("baz", 42);

        Set<String> names = JSONBeanUtil.getAllPropertyNames(bean);

        assertThat(names, is(notNullValue()));
        assertThat(names.size(),is(2));
        assertThat(names.contains("_foo"),is(true));
        assertThat(names.contains("baz"),is(true));

    }

    @Test
    public void thatGetBeanPropertyNamesWorks()
    {
        DynPropTestBean bean = new DynPropTestBean();

        Set<String> names = JSONBeanUtil.getBeanPropertyNames(bean);

        assertThat(names, is(notNullValue()));
        assertThat(names.size(),is(1));
        assertThat(names.contains("_foo"),is(true));

    }

    @Test
    public void thatReadingBeanPropertiesWorks()
    {
        DynPropTestBean bean = new DynPropTestBean();
        bean.setFoo("bar!");
        assertThat((String)JSONBeanUtil.getProperty(bean, "_foo"), is("bar!"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatReadingUndefinedBeanPropertiesDoesNotWork()
    {
        Object bean = new Object();
        JSONBeanUtil.getProperty(bean, "bar");
    }

    @Test
    public void thatWritingBeanPropertiesWorks()
    {
        DynPropTestBean bean = new DynPropTestBean();

        JSONBeanUtil.setProperty(bean, "_foo", "bar!");
        assertThat(bean.getFoo(), is("bar!"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void thatWritingUndefinedBeanPropertiesDoesNotWork()
    {
        Object bean = new Object();
        JSONBeanUtil.setProperty(bean, "bar", "bar!");
    }
    
    @Test
    @Ignore
    public void thatSetPropertyPathWorks()
    {
        Bean bean = new Bean();
        final String value = "test-string";
        
        InnerBean inner = new InnerBean();
        
        JSONBeanUtil.setPropertyPath(bean, "foo", value);
        assertThat(bean.getFoo(), is(value));

        JSONBeanUtil.setPropertyPath(bean, "inner[1]", inner);
        
        assertThat(bean.getInner().get(0), is(nullValue()));
        assertThat(bean.getInner().get(1), is(inner));
        assertThat(bean.getInner().size(), is(2));

        JSONBeanUtil.setPropertyPath(bean, "inner2['test\"']", inner);
        
        assertThat(bean.getInner2().get("test\""), is(inner));
        assertThat(bean.getInner2().size(), is(1));
        
        BeanWithArray bean2 = new BeanWithArray();
        
        
        JSONBeanUtil.setPropertyPath(bean2, "foo[1]", value);
        
        assertThat(bean2.getFoo()[0], is(nullValue()));
        assertThat(bean2.getFoo()[1], is(value));
        assertThat(bean2.getFoo().length, is(2));
     
        
        BeanWrapper wrapper = new BeanWrapper();
        
        JSONBeanUtil.setPropertyPath(wrapper, "bean.inner[0].bar", 1234);
        
        assertThat(wrapper.getBean().getInner().get(0).getBar(), is(1234));
    }
    
    @Test
    public void thatObjectFactoryWorks()
    {
        
    }
}
