package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class JSONParserTestCase
{
    protected static Logger log = Logger.getLogger(JSONParserTestCase.class);

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
        Bean b = parser.parse(Bean.class, "{\"foo\":12}");

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
        assertThat(foo.bars().size(), is(2));

    }


    @Test
    public void thatInterfaceMappingWorks()
    {
        JSONParser parser = new JSONParser();

        Collection c = parser.parse(Collection.class, "[1,7]");
        Iterator i = c.iterator();
        assertThat((Long)i.next(),is(1l));
        assertThat((Long)i.next(),is(7l));
        assertThat(c,is(ArrayList.class));

        List l = parser.parse(List.class, "[3,2]");
        assertThat((Long)l.get(0),is(3l));
        assertThat((Long)l.get(1),is(2l));
        assertThat(c,is(ArrayList.class));

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
    public void thatParsingIntoAClassPropertyWorks()
    {
        BeanWithClassProperty bean = parser.parse(BeanWithClassProperty.class, "{\"classProperty\":\"java.lang.String\"}");

        assertThat(bean, is(notNullValue()));
        assertThat(bean.getClassProperty(), equalTo(String.class));
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
        TestEnum e = parser.parse(TestEnum.class, "\"VAL1\"");
        assertThat(e, is (TestEnum.VAL1));
    }

    @Test
    public void thatBeanWithEnumParsingWorks()
    {
        BeanWithEnum bean = parser.parse(BeanWithEnum.class, "{\"testEnum\":\"VAL2\"}");

        assertThat(bean.getTestEnum(),is(TestEnum.VAL2));
    };

    @Test
    public void thatTypeMappingWithEnumParsingWorks()
    {
        parser.addTypeHint(".qerw", TestEnum.class);
        Map bean = parser.parse(Map.class, "{\"qerw\":\"VAL2\"}");

        assertThat((TestEnum)bean.get("qerw"),is(TestEnum.VAL2));
    };
}
