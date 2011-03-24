package org.svenson;

import static junit.framework.Assert.assertEquals;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.svenson.test.Bean;
import org.svenson.test.IgnoredPropertyBean;
import org.svenson.test.InnerBean;
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
        
        assertThat(jsonDataset, containsString("\"bar\":\"baz\""));
        assertThat(jsonDataset, containsString("\"foo\":[1,2]"));
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
    
    @Test
    public void generatingSetsAndCollectionsOfInnerBeans()
    {
        Bean b = new Bean();
        b.setFoo("xxx");
        b.setNotBar(2345);
        
        HashSet<InnerBean> set = new HashSet<InnerBean>();
        InnerBean innerBean = new InnerBean();
        innerBean.setBar(1234);
        set.add(innerBean);
        b.setInner3(set);
        
        String json = JSON.defaultJSON().forValue(b);
        assertThat(json, containsString("\"foo\":\"xxx\""));
        assertThat(json, containsString("\"bar\":2345"));
        assertThat(json, not(containsString("\"inner2\"")));
        assertThat(json, not(containsString("\"inner\"")));
        assertThat(json, not(containsString("\"inner4\"")));
        assertThat(json, containsString("\"inner3\":[{\"bar\":1234}]"));

        innerBean.setBar(1234);
        set.add(innerBean);
        b.setInner3(null);
        b.setInner4(set);
        
        json = JSON.defaultJSON().forValue(b);
        assertThat(json, containsString("\"foo\":\"xxx\""));
        assertThat(json, containsString("\"bar\":2345"));
        assertThat(json, not(containsString("\"inner2\"")));
        assertThat(json, not(containsString("\"inner\"")));
        assertThat(json, not(containsString("\"inner3\"")));
        assertThat(json, containsString("\"inner4\":[{\"bar\":1234}]"));
        
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
