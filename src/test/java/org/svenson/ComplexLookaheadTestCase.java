package org.svenson;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.matcher.SubtypeMatcher;
import org.svenson.test.complexlookahead.Base;
import org.svenson.test.complexlookahead.ListVarianceContainer;
import org.svenson.test.complexlookahead.MapVarianceContainer;
import org.svenson.test.complexlookahead.PropertyVarianceContainer;
import org.svenson.test.complexlookahead.Foo;

public class ComplexLookaheadTestCase
{
    private static Logger log = LoggerFactory.getLogger(ComplexLookaheadTestCase.class);
    private JSONParser parser;


    @Before
    public void init()
    {
        parser = new JSONParser();

        ClassNameBasedTypeMapper typeMapper = new ClassNameBasedTypeMapper();
        typeMapper.setEnforcedBaseType(Base.class);
        typeMapper.setBasePackage(Base.class.getPackage().getName());
        typeMapper.setDiscriminatorField("type");
        typeMapper.setPathMatcher(new SubtypeMatcher(Base.class));

        parser.setTypeMapper(typeMapper);

    }

    @Test
    public void testPropertyVariance()
    {

        PropertyVarianceContainer container = parser.parse(PropertyVarianceContainer.class, "{\"foo\":\"bar\"}");
        assertThat((String) container.getFoo(), is("bar"));

        PropertyVarianceContainer container2 = parser.parse(PropertyVarianceContainer.class, "{\"foo\":{\"type\":\"Foo\",\"bar\":12}}");

        Foo foo = (Foo) container2.getFoo();
        assertThat(foo.getBar(), is(12));

    }

    @Test
    public void testMapVariance()
    {

        MapVarianceContainer container = parser.parse(MapVarianceContainer.class, "{\"props\":{\"1\":{\"type\":\"Foo\",\"bar\":1},\"2\": " +
            "\"quz\"}}");

        assertThat(container.getProps().get("1") instanceof Foo, is(true));
        assertThat(container.getProps().get("2") instanceof String, is(true));
    }

    @Test
    public void testListVariance()
    {
        ListVarianceContainer container = parser.parse(ListVarianceContainer.class, "{\"props\":[{\"type\":\"Foo\",\"bar\":1},\"quz\"]}");

        assertThat(container.getProps().get(0) instanceof Foo, is(true));
        assertThat(container.getProps().get(1) instanceof String, is(true));
    }

}
