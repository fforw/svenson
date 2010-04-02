package org.svenson;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.test.LinkedArrayBase;
import org.svenson.test.LinkedBeanBase;
import org.svenson.test.LinkedChildBean;
import org.svenson.test.LinkedListBase;
import org.svenson.test.LinkedMapBase;

public class LinkedTestCase
{
    private static Logger log = LoggerFactory.getLogger(LinkedTestCase.class);
    
    @Test
    public void testLinkedBean()
    {
        LinkedBeanBase base = new LinkedBeanBase();
        base.setChild(new LinkedChildBean("child_id"));
        
        String json = JSON.defaultJSON().forValue(base);
        assertThat(json, is("{\"child\":\"child_id\"}"));
    }

    @Test
    public void testLinkedBeanArray()
    {
        LinkedArrayBase base = new LinkedArrayBase();
        base.setChildren(new LinkedChildBean[] { new LinkedChildBean("child_id"),new LinkedChildBean("child_id 2") } );
        
        String json = JSON.defaultJSON().forValue(base);
        
        assertThat(json, is("{\"children\":[\"child_id\",\"child_id 2\"]}"));
    }
    
    @Test
    public void testLinkedBeanList()
    {
        LinkedListBase base = new LinkedListBase();
        base.setChildren(Arrays.asList( new LinkedChildBean("child_id"),new LinkedChildBean("child_id 2") ) );
        
        String json = JSON.defaultJSON().forValue(base);
        
        assertThat(json, is("{\"children\":[\"child_id\",\"child_id 2\"]}"));
    }

    @Test
    public void testLinkedMap()
    {
        LinkedMapBase base = new LinkedMapBase();
        Map<String, LinkedChildBean> map = new TreeMap<String, LinkedChildBean>();
        map.put("foo", new LinkedChildBean("child-1"));
        map.put("bar", new LinkedChildBean("child-2"));
        base.setChildren(map);
        
        String json = JSON.defaultJSON().forValue(base);
        
        assertThat(json, startsWith("{\"children\":{"));
        assertThat(json, containsString("\"foo\":\"child-1\""));
        assertThat(json, containsString("\"bar\":\"child-2\""));
    }

    @Test
    public void testParseBack()
    {
        {
            LinkedBeanBase base = JSONParser.defaultJSONParser().parse(LinkedBeanBase.class, "{\"child\":\"child_id\"}");
            assertThat(base.getChild(), is(nullValue()));
        }
        {
            LinkedArrayBase base = JSONParser.defaultJSONParser().parse(LinkedArrayBase.class, "{\"children\":[\"child_id\",\"child_id 2\"]}");
            assertThat(base.getChildren(), is(nullValue()));
        }
        {
            LinkedListBase base = JSONParser.defaultJSONParser().parse(LinkedListBase.class, "{\"children\":[\"child_id\",\"child_id 2\"]}");
            assertThat(base.getChildren(), is(nullValue()));
        }
        {
            LinkedMapBase base = JSONParser.defaultJSONParser().parse(LinkedMapBase.class, "{\"children\":{\"foo\":\"child-1\",\"bar\":\"child-2\"}}");
            assertThat(base.getChildren(), is(nullValue()));
        }
    }
    /*
INFO  ..org.svenson.LinkedTestCase: {"child":"child_id"}
INFO  ..org.svenson.LinkedTestCase: {"children":["child_id","child_id 2"]}
INFO  ..org.svenson.LinkedTestCase: {"children":["child_id","child_id 2"]}
INFO  ..org.svenson.LinkedTestCase: {"children":{"foo":"child-1","bar":"child-2"}}
     */
}
