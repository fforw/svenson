package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.matcher.RegExPathMatcher;
import org.svenson.test.*;

public class JSONParserTestCase
{
    protected static Logger log = LoggerFactory.getLogger(JSONParserTestCase.class);

    private JSONParser parser;

    @Before
    public void initParser()
    {
        parser = new JSONParser();

    }
    @Test
    public void thatParsingIntoMapWorks()
    {
        Map m = parser.parse(HashMap.class, "{\"foo\":\"bar\",\"baz\":42}");
        assertThat(m, is(notNullValue()));
        assertThat(m.size(), is(2));
        assertThat(m.get("foo"), is((Object) "bar"));
        assertThat(m.get("baz"), is((Object) 42l));

        m = parser.parse(HashMap.class, "{}");
        assertThat(m, is(notNullValue()));
        assertThat(m.size(), is(0));

        m = parser.parse(HashMap.class, "{\"foo\":42}");
        assertThat(m, is(notNullValue()));
        assertThat(m.size(), is(1));
        assertThat(m.get("foo"), is((Object) 42l));
    }

    @Test
    public void thatParsingIntoArrayWorks()
    {
        List l = parser.parse(ArrayList.class, "[]");
        assertThat(l, is(notNullValue()));
        assertThat(l.size(), is(0));

        l = parser.parse(ArrayList.class, "[1]");
        assertThat(l, is(notNullValue()));
        assertThat(l.size(), is(1));
        assertThat(l.get(0), is((Object) 1l));

        l = parser.parse(ArrayList.class, "[1,\"foo\"]");
        assertThat(l, is(notNullValue()));
        assertThat(l.size(), is(2));
        assertThat(l.get(0), is((Object) 1l));
        assertThat(l.get(1), is((Object) "foo"));
    }

    @Test
    public void simpleBeanParsing()
    {
        Bean b = parser.parse(Bean.class, "{\"foo\":\"12\"}");

        assertThat(b, is(notNullValue()));
        assertThat(b.getFoo(), is("12"));

        b = parser.parse(Bean.class, "{\"foo\":\"baz!\"}");
        assertThat(b, is(notNullValue()));
        assertThat(b.getFoo(), is("baz!"));

        b = parser.parse(Bean.class, "{\"bar\":123}");
        assertThat(b, is(notNullValue()));
        assertThat(b.getNotBar(), is((Object) 123));
    }

    @Test
    public void nestedBeanParsing()
    {
        Bean b = parser.parse(Bean.class, "{\"inner\":[{\"bar\":42}]}");
        
        assertThat(b, is(notNullValue()));
        assertThat(b.getInner().get(0).getBar(), is(42));
        
        b = parser.parse(Bean.class, "{\"inner2\":{\"foo\":{\"bar\":42}}}");
        
        assertThat(b, is(notNullValue()));
        assertThat(b.getInner2().get("foo").getBar(), is(42));
    }

    @Test
    public void nestedBeanParsingWithSetAndCollection()
    {
        Bean b = parser.parse(Bean.class, "{\"inner3\":[{\"bar\":42}]}");

        assertThat(b, is(notNullValue()));
        InnerBean innerBean = b.getInner3().iterator().next();
        assertThat(innerBean.getBar(), is(42));

        b = parser.parse(Bean.class, "{\"inner4\":[{\"bar\":42}]}");

        assertThat(b, is(notNullValue()));
        innerBean = b.getInner4().iterator().next();
        assertThat(innerBean.getBar(), is(42));
    }

    @Test
    public void dynAttrsBeanParsing()
    {
        DynAttrsBean b = parser.parse(DynAttrsBean.class, "{\"foo\":42,\"bar\":12}");

        assertThat(b, is(notNullValue()));
        assertThat(b.getFoo(), is("42"));
        assertThat(b.getProperty("foo"), is(nullValue()));
        assertThat(b.getProperty("bar"), is((Object) 12l));
    }

    @Test
    public void BeanWithAddMethodParsing()
    {
        FooBean foo = parser.parse(FooBean.class, "{\"bar\":[{},{}]}");

        assertThat(foo, is(notNullValue()));
        assertThat(foo.getBars().size(), is(2));

    }


    @Test
    public void thatInterfaceMappingWorks()
    {
        JSONParser parser = new JSONParser();

        Collection c = parser.parse(Collection.class, "[1,7]");
        Iterator i = c.iterator();
        assertThat((Long)i.next(),is(1l));
        assertThat((Long)i.next(),is(7l));
        

        List l = parser.parse(List.class, "[3,2]");
        assertThat((Long)l.get(0),is(3l));
        assertThat((Long)l.get(1),is(2l));
        // XXX: There seems to be some race condition that makes this previous assumption false in that
        // the returned object is actually a HashSet. it boils down to the iteration over the array
        // being different (on my new multi core CPU?).
        // Since the result is actually not an error, Collection is a 
        assertThat("Class is " + c.getClass(), c.getClass().equals(ArrayList.class),is(true));

        Map m = parser.parse(Map.class, "{\"foo\":\"foo!\"}");
        assertThat((String)m.get("foo"),is("foo!"));
        assertThat(m,is(HashMap.class));

        Map<Class,Class> interfaceMappings = new HashMap<Class, Class>();
        interfaceMappings.put(Map.class,TreeMap.class);
        parser.setInterfaceMappings(interfaceMappings);

        m = parser.parse(Map.class, "{\"foo\":\"foo!\"}");
        assertThat((String)m.get("foo"),is("foo!"));
        assertThat(m,is(TreeMap.class));

    }

    @Test(expected = JSONParseException.class)
    public void testGarbage1()
    {
        parser.parse(HashMap.class, "{\"foo\":}");
    }

    @Test(expected = JSONParseException.class)
    public void testGarbage2()
    {
        parser.parse(HashMap.class, "{:}");
    }

    @Test(expected = JSONParseException.class)
    public void testGarbage2a()
    {
        parser.parse(HashMap.class, "{,}");
    }

    @Test(expected = JSONParseException.class)
    public void testGarbage3()
    {
        parser.parse(HashMap.class, "{:\"foo\"}");
    }

    @Test(expected = JSONParseException.class)
    public void testGarbage4()
    {
        parser.parse(ArrayList.class, "[,]");
    }

    @Test(expected = JSONParseException.class)
    public void testGarbage5()
    {
        parser.parse(ArrayList.class, "[1,]");
    }

    @Test(expected = JSONParseException.class)
    public void testGarbage6()
    {
        parser.parse(ArrayList.class, "[,1]");
    }

    @Test
    public void thatTypeHintsWork()
    {
        String json = "{\"child\":{\"foo\":\"bar!\"}}";
        ContainerBean containerBean = parser.parse(ContainerBean.class, json);
        assertThat(containerBean, is(notNullValue()));
        assertThat(containerBean.getChildBean(), is(HashMap.class));

        parser.addTypeHint(".child", DynAttrsBean.class);
        containerBean = parser.parse(ContainerBean.class, json);
        assertThat(containerBean, is(notNullValue()));
        assertThat(containerBean.getChildBean(), is(DynAttrsBean.class));
        DynAttrsBean b = (DynAttrsBean)containerBean.getChildBean();
        assertThat(b.getFoo(), is("bar!"));
    }

    @Test
    public void thatTypeHintsWorkWithMatchers()
    {
        String json = "{\"b1\":{\"foo\":\"bar!\"},\"b2\":{\"foo\":\"qux!\"}}";

        Map containerBean = parser.parse(Map.class, json);
        assertThat(containerBean, is(notNullValue()));
        assertThat(containerBean.get("b1"), is(HashMap.class));

        parser.addTypeHint(new RegExPathMatcher("\\.b[0-9]"), DynAttrsBean.class);
        containerBean = parser.parse(Map.class, json);
        assertThat(containerBean, is(notNullValue()));
        DynAttrsBean b = (DynAttrsBean)containerBean.get("b1");
        assertThat(b,is(notNullValue()));
        assertThat(b.getFoo(), is("bar!"));
        b = (DynAttrsBean)containerBean.get("b2");
        assertThat(b,is(notNullValue()));
        assertThat(b.getFoo(), is("qux!"));
    }
    
    @Test
    public void thatParsingIntoAClassPropertyWorks()
    {
        BeanWithClassProperty bean = parser.parse(BeanWithClassProperty.class, "{\"classProperty\":\"java.lang.String\"}");

        assertThat(bean, is(notNullValue()));
        assertThat(bean.getClassProperty().equals(String.class), is(true));
    }

    @Test
    public void thatArraysCanBeParsedIntoDynamicProperties()
    {
        DynAttrsBean bean = parser.parse(DynAttrsBean.class, "{\"438793569376\":[42,12]}");
        assertThat(bean.getProperty("438793569376"), is(ArrayList.class));
    }

    @Test(expected = JSONParseException.class)
    public void thatJSONNamesWin()
    {
        ContainerBean bean = parser.parse(ContainerBean.class, "{\"childBean\":\"foo\"}");
    }

    @Test
    public void thatEnumParsingWorks()
    {
        SomeEnum e = parser.parse(SomeEnum.class, "\"VAL1\"");
        assertThat(e, is (SomeEnum.VAL1));
    }

    @Test
    public void thatBeanWithEnumParsingWorks()
    {
        BeanWithEnum bean = parser.parse(BeanWithEnum.class, "{\"someEnum\":\"VAL2\"}");

        assertThat(bean.getSomeEnum(),is(SomeEnum.VAL2));
    };

    @Test
    public void thatTypeMappingWithEnumParsingWorks()
    {
        parser.addTypeHint(".qerw", SomeEnum.class);
        Map bean = parser.parse(Map.class, "{\"qerw\":\"VAL2\"}");

        assertThat((SomeEnum)bean.get("qerw"),is(SomeEnum.VAL2));
    };

    @Test
    public void thatBeanPropertiesWork()
    {
        MediaEntry entry = JSONParser.defaultJSONParser().parse(MediaEntry.class, "{\"media\":{\"type\":\"text\\/html\"}}");
        assertThat(entry, is(notNullValue()));
        assertThat(entry.getMedia(), is(notNullValue()));
        assertThat(entry.getMedia().getType(), is("text/html"));
    }

    @Test
    public void testParsingIntoSets()
    {
        Set<String> s = JSONParser.defaultJSONParser().parse(Set.class, "[\"abc\",\"def\"]");
        assertThat(s,is(notNullValue()));
        assertThat(s.size(),is(2));
        assertThat(s.contains("abc"),is(true));
        assertThat(s.contains("def"),is(true));
    }
    
    @Test
    public void testTypeDistance()
    {
        assertThat(JSONParser.getTypeDistance(Collection.class, Collection.class, 1), is(nullValue()));
        assertThat(JSONParser.getTypeDistance(Collection.class, List.class,1), is(1));
        assertThat(JSONParser.getTypeDistance(Collection.class, Set.class,1), is(1));
        assertThat(JSONParser.getTypeDistance(Collection.class, SortedSet.class,1), is(2));
    }
    
    @Test
    public void thatParsingNullValuesWorks()
    {
        JSONParser parser = JSONParser.defaultJSONParser();
        Map m = parser.parse(Map.class, "null");
        assertThat(m, is(nullValue()));

        Bar bar = parser.parse(Bar.class, "null");
        assertThat(bar, is(nullValue()));

        List l = parser.parse(List.class, "null");
        assertThat(l, is(nullValue()));
        
    }
    
    @Test
    public void testBeanWithStringAdder()
    {
        BeanWithStringAdder bean = JSONParser.defaultJSONParser().parse(BeanWithStringAdder.class, "{\"values\":[\"foo\",null,\"bar\"]}");
        
        assertThat(bean.getValues().size(), is(3));
        assertThat(bean.getValues().get(0), is("foo"));
        assertThat(bean.getValues().get(1), is( nullValue()));
        assertThat(bean.getValues().get(2), is("bar"));
    }

    @Test
    public void testBeanWithIgnoredInvalidProperties() {
        BeanIgnoringInvalidProperties bean = JSONParser.defaultJSONParser()
                .parse(BeanIgnoringInvalidProperties.class, "{\"stringCollection\": \"single string\", \"longValue\": 2}");
        assertThat(bean.getLongValue(), is(2L));
        assertThat(bean.getStringCollection(), is(nullValue()));
    }
}
