package org.svenson;

import static org.easymock.EasyMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static junit.framework.TestCase.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.svenson.JSON;
import org.svenson.JSONable;
import org.svenson.JSONifier;
import org.svenson.test.IgnoredPropertyBean;
import org.svenson.test.TestEnum;


public class JSONTestCase
{
    private JSON JSON = new JSON();

    @Test
    public void testInt()
    {
        assertThat(JSON.forValue(52), is("52"));
    }

    @Test
    public void testString()
    {
        assertThat(JSON.forValue("Hello äöü"), is("\"Hello \\u00e4\\u00f6\\u00fc\""));
    }

    @Test
    public void testArray()
    {
        assertEquals("[1,2,3]", JSON.forValue(new int[] { 1,2,3}));
    }

    @Test
    public void testMap()
    {
        // treemap for ordered keys
        Map<String, Object> m=new TreeMap<String, Object>();
        m.put("foo", 42);
        m.put("bar", "baz");
        assertEquals("{\"bar\":\"baz\",\"foo\":42}", JSON.forValue(m));
    }

    @Test
    public void testBean()
    {
        String json=JSON.forValue(new SimpleBean(new int[]{1,2},"baz"));
        //assertEquals("{bar:\"baz\",_class:\""+JSONTestCase.class.getName()+"$SimpleBean\",foo:[1,2]}",json);
        assertEquals("{\"bar\":\"baz\",\"foo\":[1,2]}",json);
    }

    @Test
    public void testJSONable()
    {
        JSONable jsonableMock = createMock(JSONable.class);

        expect(jsonableMock.toJSON()).andReturn("{foo:1}");
        replay(jsonableMock);
        assertThat(JSON.forValue(jsonableMock), is("{foo:1}"));
        verify(jsonableMock);
    }

    @Test
    public void thatJSONifierWorks()
    {
        JSON.deregisterJSONifiers();

        assertThat(JSON.forValue(new JSONifiedBean()), is("{}"));

        JSONifier jsonifierMock = createMock(JSONifier.class);
        final String jsonifierOutput = "{foo:1}";
        expect(jsonifierMock.toJSON(isA(JSONifiedBean.class))).andReturn(jsonifierOutput);

        JSON.registerJSONifier(JSONifiedBean.class, jsonifierMock);

        replay(jsonifierMock);
        String json = JSON.forValue(new JSONifiedBean());

        assertThat(json, is(jsonifierOutput));
        verify(jsonifierMock);
    }

    @Test
    public void testThatIgnoredPropertiesWork()
    {
        IgnoredPropertyBean bean = new IgnoredPropertyBean();
        bean.setProperty("foo", "bar");

        assertThat(JSON.forValue(bean), is("{\"foo\":\"bar\"}"));

    }

    @Test
    public void thatEnumGeneratioWorks()
    {
        Map m = new HashMap();
        m.put("test", TestEnum.VAL1);

        assertThat(JSON.forValue(m), is("{\"test\":\"VAL1\"}"));
    }
    
    public void thatQuoteQuotingWorksInAllModes()
    {
        JSON json = new JSON();
        json.setQuoteChar('\'');
        
        assertThat(json.quote("\""), is("\""));
        assertThat(json.quote("'"), is("\\\'"));
        
        json.setQuoteChar('"');
        
        assertThat(json.quote("\""), is("\\\""));
        assertThat(json.quote("'"), is("\'"));
    }

    public static class SimpleBean
    {
        private Object foo,bar;

        public SimpleBean(Object foo, Object bar)
        {
            this.foo=foo;
            this.bar=bar;
        }

        public Object getFoo()
        {
            return foo;
        }

        public Object getBar()
        {
            return bar;
        }
    }

    public static class JSONifiedBean
    {
    }

}
