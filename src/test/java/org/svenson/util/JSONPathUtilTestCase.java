package org.svenson.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.svenson.ObjectFactory;
import org.svenson.test.Bean;
import org.svenson.test.BeanWithArray;
import org.svenson.test.BeanWrapper;
import org.svenson.test.InnerBean;


public class JSONPathUtilTestCase
{
    @Test
    public void thatSetPropertyPathWorks()
    {
        Bean bean = new Bean();
        final String value = "test-string";
        
        InnerBean inner = new InnerBean();
        
        JSONPathUtil pathUtil = new JSONPathUtil();
        
        pathUtil.setPropertyPath(bean, "foo", value);
        assertThat(bean.getFoo(), is(value));
        
        assertThat((String)pathUtil.getPropertyPath(bean,"foo"), is(value));

        pathUtil.setPropertyPath(bean, "inner[1]", inner);
        
        assertThat(bean.getInner().get(0), is(nullValue()));
        assertThat(bean.getInner().get(1), is(inner));
        assertThat(bean.getInner().size(), is(2));

        assertThat((InnerBean)pathUtil.getPropertyPath(bean,"inner[1]"), is(inner));

        pathUtil.setPropertyPath(bean, "inner2['test\"']", inner);
        
        assertThat(bean.getInner2().get("test\""), is(inner));
        assertThat(bean.getInner2().size(), is(1));

        assertThat((InnerBean)pathUtil.getPropertyPath(bean,"inner2['test\"']"), is(inner));
        
        BeanWithArray bean2 = new BeanWithArray();
        
        pathUtil.setPropertyPath(bean2, "foo[1]", value);
        
        assertThat(bean2.getFoo()[0], is(nullValue()));
        assertThat(bean2.getFoo()[1], is(value));
        assertThat(bean2.getFoo().length, is(2));
     
        assertThat((String)pathUtil.getPropertyPath(bean2,"foo[1]"), is(value));
        
        BeanWrapper wrapper = new BeanWrapper();
        
        pathUtil.setPropertyPath(wrapper, "bean.inner[0].bar", 1234);
        
        assertThat(wrapper.getBean().getInner().get(0).getBar(), is(1234));

        assertThat((Integer)pathUtil.getPropertyPath(wrapper,"bean.inner[0].bar"), is(1234));
    }

    @Test
    public void testPathOnCollections()
    {
        Map m = new HashMap();
        
        JSONPathUtil pathUtil = new JSONPathUtil();
        final String value = "yyy";
        pathUtil.setPropertyPath(m, "xxx", value);        
        assertThat((String)m.get("xxx"), is(value));

        pathUtil.setPropertyPath(m, "array[1]", value);        
        assertThat((String)((List)m.get("array")).get(1), is(value));
        
        pathUtil.setPropertyPath(m, "array[0].foo", value);        
        assertThat((String)((Map)((List)m.get("array")).get(0)).get("foo"), is(value));
        
    }
    
    @Test
    public void testOnDynamicProperties()
    {
        DynPropTestBean bean = new DynPropTestBean();
        
        JSONPathUtil pathUtil = new JSONPathUtil();
        final String value = "epituewutz";
        
        pathUtil.setPropertyPath(bean, "_foo", value);
        pathUtil.setPropertyPath(bean, "_bar", value);
        
        assertThat(bean.getFoo(), is(value));
        assertThat((String)bean.getProperty("_bar"), is(value));
        
        
        Map m = new HashMap();
        bean = new DynPropTestBean();
        
        pathUtil.setPropertyPath(m, "xxx", bean);

        assertThat((DynPropTestBean)m.get("xxx"), is(bean));

        pathUtil.setPropertyPath(m, "xxx._foo", value);
        assertThat(bean.getFoo(), is(value));
        pathUtil.setPropertyPath(m, "xxx._bar", value);
        assertThat((String)bean.getProperty("_bar"), is(value));
    }
    
    @Test
    public void testObjectFactory()
    {
        List<Map<String,String>> l = new ArrayList<Map<String,String>>();
        
        JSONPathUtil pathUtil = new JSONPathUtil();
        
        List<ObjectFactory<?>> factories = new ArrayList<ObjectFactory<?>>();
        factories.add(new ObjectFactory()
        {

            @SuppressWarnings("unchecked")
            public Object create(Class typeHint)
            {
                return new LinkedHashMap();
            }

            public boolean supports(Class cls)
            {
                return Map.class.isAssignableFrom(cls);
            }
        });
        pathUtil.setObjectFactories(factories);
        
        final String value = "208397aaq";
        pathUtil.setPropertyPath(l, "0.xxx", value);        
        Map<String, String> map = l.get(0);
        assertThat(map.get("xxx"), is(value));
        assertThat(map, isA(LinkedHashMap.class));
        
        
    }
}
