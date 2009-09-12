package org.svenson;

import static org.easymock.EasyMock.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static junit.framework.TestCase.*;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.svenson.JSON;
import org.svenson.JSONable;
import org.svenson.JSONifier;
import org.svenson.test.IgnoredPropertyBean;
import org.svenson.test.SomeEnum;


public class JSONTestCase
{
    private JSON json = new JSON();

    @Test
    public void testInt()
    {
        assertThat(json.forValue(52), is("52"));
    }

    @Test
    public void testString()
    {
        assertThat(json.forValue("Hello \u00e4\u00f6\u00fc"), is("\"Hello \\u00e4\\u00f6\\u00fc\""));
        JSON json2 = new JSON();
        json2.setEscapeUnicodeChars(false);
        assertThat(json2.forValue("Hello \u00e4\u00f6\u00fc"), is("\"Hello \u00e4\u00f6\u00fc\""));
    }
    
    @Test
    public void testArray()
    {
        assertEquals("[1,2,3]", json.forValue(new int[] { 1,2,3}));
    }

    @Test
    public void testMap()
    {
        // treemap for ordered keys
        Map<String, Object> m=new TreeMap<String, Object>();
        m.put("foo", 42);
        m.put("bar", "baz");
        assertEquals("{\"bar\":\"baz\",\"foo\":42}", json.forValue(m));
    }

    @Test
    public void testBean()
    {
        String jsonDataset=json.forValue(new SimpleBean(new int[]{1,2},"baz"));
        //assertEquals("{bar:\"baz\",_class:\""+JSONTestCase.class.getName()+"$SimpleBean\",foo:[1,2]}",json);
        assertEquals("{\"bar\":\"baz\",\"foo\":[1,2]}",jsonDataset);
    }

    @Test
    public void testJSONable()
    {
        JSONable jsonableMock = createMock(JSONable.class);

        expect(jsonableMock.toJSON()).andReturn("{foo:1}");
        replay(jsonableMock);
        assertThat(json.forValue(jsonableMock), is("{foo:1}"));
        verify(jsonableMock);
    }

    @Test
    public void thatJSONifierWorks()
    {
        json.deregisterJSONifiers();

        assertThat(json.forValue(new JSONifiedBean()), is("{}"));

        JSONifier jsonifierMock = createMock(JSONifier.class);
        final String jsonifierOutput = "{foo:1}";
        expect(jsonifierMock.toJSON(isA(JSONifiedBean.class))).andReturn(jsonifierOutput);

        json.registerJSONifier(JSONifiedInterface.class, jsonifierMock);

        replay(jsonifierMock);
        String jsonDataset = json.forValue(new JSONifiedBean());

        assertThat(jsonDataset, is(jsonifierOutput));
        verify(jsonifierMock);
    }

    @Test
    public void testThatIgnoredPropertiesWork()
    {
        IgnoredPropertyBean bean = new IgnoredPropertyBean();
        bean.setProperty("foo", "bar");

        assertThat(json.forValue(bean), is("{\"foo\":\"bar\"}"));

    }

    @Test
    public void thatEnumGeneratioWorks()
    {
        Map m = new HashMap();
        m.put("test", SomeEnum.VAL1);

        assertThat(json.forValue(m), is("{\"test\":\"VAL1\"}"));
    }
    
    @Test
    public void thatQuoteQuotingWorksInAllModes()
    {
        JSON json = new JSON();
        json.setQuoteChar('\'');
        
        assertThat(json.quote("\""), is("'\"'"));
        assertThat(json.quote("'"), is("'\\\''"));
        
        json.setQuoteChar('"');
        
        assertThat(json.quote("\""), is("\"\\\"\""));
        assertThat(json.quote("'"), is("\"\'\""));
    }
    
    @Test
    public void thatWriterOutputWorks()
    {
        StringWriter sw = new StringWriter();
        json.writeJSONToWriter("abc \u00e4\u00f6\u00fc", sw);
        
        assertThat(sw.getBuffer().toString(), is("\"abc \\u00e4\\u00f6\\u00fc\""));
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

    public static interface JSONifiedInterface
    {
        
    }
    
    public static class JSONifiedBean implements JSONifiedInterface
    {
    }

}
