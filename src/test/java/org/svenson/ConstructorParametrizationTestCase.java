package org.svenson;

import org.junit.Test;
import org.svenson.matcher.SubtypeMatcher;
import org.svenson.test.AddImmutables;
import org.svenson.test.Bar;
import org.svenson.test.CTORListVariance;
import org.svenson.test.CTORMapVariance;
import org.svenson.test.CTORVariance;
import org.svenson.test.CTVBase;
import org.svenson.test.CTVExtension;
import org.svenson.test.ConstructorParametrization;
import org.svenson.test.CtorParams;
import org.svenson.test.CtorParams2;
import org.svenson.test.Immutable;
import org.svenson.test.ListOfImmutables;
import org.svenson.test.MapOfImmutables;
import org.svenson.test.TypedListCTOR;
import org.svenson.test.TypedMapCTOR;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class ConstructorParametrizationTestCase
{
    @Test
    public void testConstructorParametrization()
    {
        ConstructorParametrization o = JSONParser.defaultJSONParser().parse(ConstructorParametrization.class,
            "{\"name\":\"Hans\", \"age\": 27}");

        assertThat(o,is(notNullValue()));
        assertThat(o.name,is("Hans"));
        assertThat(o.age,is(27));
    }

    @Test
    public void testTypedListConstructorParametrization()
    {
        TypedListCTOR o = JSONParser.defaultJSONParser().parse(TypedListCTOR.class,
            "{\"bar\":[{},{}], \"age\": 33}");

        assertThat(o,is(notNullValue()));
        assertThat(o.getBar().size(),is(2));
        assertThat(o.getBar().get(0),is(Bar.class));
        assertThat(o.getAge(),is(33L));
    }

    @Test
    public void testTypedMapConstructorParametrization()
    {
        JSONParser parser = JSONParser.defaultJSONParser();
        TypedMapCTOR o = parser.parse(TypedMapCTOR.class,
            "{\"bar\":{\"foo\": {}}, \"flag\": true}");

        assertThat(o,is(notNullValue()));
        assertThat(o.getBar().size(),is(1));
        assertThat(o.getBar().get("foo"),is(Bar.class));
        assertThat(o.isFlag(),is(true));
    }

    @Test
    public void testImmutableIntoImmutable()
    {
        JSONParser parser = JSONParser.defaultJSONParser();
        Immutable o = parser.parse(Immutable.class,
            "{\"inner\":{\"string\": \"foo\"}}");

        assertThat(o,is(notNullValue()));
        assertThat(o.getInner(),is(notNullValue()));
        assertThat(o.getInner().getString(), is("foo"));
    }

    @Test
    public void testListOfImmutablesIntoImmutable()
    {
        JSONParser parser = JSONParser.defaultJSONParser();
        ListOfImmutables o = parser.parse(ListOfImmutables.class,
            "{\"quxes\":[{\"name\" : \"Anton\"}]}");

        assertThat(o,is(notNullValue()));
        assertThat(o.getQuxes(),is(notNullValue()));
        assertThat(o.getQuxes().get(0),is(notNullValue()));
        assertThat(o.getQuxes().get(0).getName(),is("Anton"));
    }

    @Test
    public void testMapOfImmutablesIntoImmutable()
    {
        JSONParser parser = JSONParser.defaultJSONParser();
        MapOfImmutables o = parser.parse(MapOfImmutables.class,
            "{\"quxes\":{\"one\":{\"name\" : \"Klara\"}}}");

        assertThat(o,is(notNullValue()));
        assertThat(o.getQuxes(),is(notNullValue()));
        assertThat(o.getQuxes().get("one"),is(notNullValue()));
        assertThat(o.getQuxes().get("one").getName(),is("Klara"));
    }

    @Test
    public void testAddingImmutablesToAnImmutable()
    {
        JSONParser parser = JSONParser.defaultJSONParser();
        AddImmutables o = parser.parse(AddImmutables.class,
            "{\"quxes\":[{\"name\" : \"Anna\"}]}");

        assertThat(o,is(notNullValue()));
        assertThat(o.getQuxes(),is(notNullValue()));
        assertThat(o.getQuxes().get(0),is(notNullValue()));
        assertThat(o.getQuxes().get(0).getName(),is("Anna"));
    }


    @Test
    public void testVariance()
    {
        JSONParser parser = setupParser();

        CTORVariance o = parser.parse(CTORVariance.class,
            "{\"value\":{\"type\" : \"CTVExtension\"}}");

        assertThat(o,is(notNullValue()));
        assertThat(o.getValue(),is(CTVExtension.class));
        assertThat(o.getName(),is(nullValue()));

        CTORVariance o2 = parser.parse(CTORVariance.class,
            "{\"value\":\"Beate\"}");

        assertThat(o2,is(notNullValue()));
        assertThat(o2.getValue(),is(nullValue()));
        assertThat(o2.getName(),is("Beate"));
    }

    private JSONParser setupParser()
    {
        JSONParser parser = new JSONParser();

        ClassNameBasedTypeMapper typeMapper = new ClassNameBasedTypeMapper();
        typeMapper.setBasePackage(CTVBase.class.getPackage().getName());
        typeMapper.setEnforcedBaseType(CTVBase.class);
        typeMapper.setDiscriminatorField("type");
        typeMapper.setPathMatcher(new SubtypeMatcher(CTVBase.class));
        parser.setTypeMapper(typeMapper);
        return parser;
    }

    @Test
    public void testListVariance()
    {
        JSONParser parser = setupParser();
        CTORListVariance o = parser.parse(CTORListVariance.class,
            "{\"value\":[\"Günther\", {\"type\" : \"CTVExtension\"}]}");

        assertThat(o, is(notNullValue()));
        assertThat(o.getValues(), is(notNullValue()));
        assertThat(o.getValues().get(0), is(String.class));
        assertThat(o.getValues().get(1), is(CTVExtension.class));
    }

    @Test
    public void testMapVariance()
    {
        JSONParser parser = setupParser();
        CTORMapVariance o = parser.parse(CTORMapVariance.class,
            "{\"value\":{\"foo\":\"Jon\", \"bar\" : {\"type\" : \"CTVExtension\"}}}");

        assertThat(o,is(notNullValue()));
        assertThat(o.getValues(),is(notNullValue()));
        assertThat(o.getValues().get("foo"),is(String.class));
        assertThat(o.getValues().get("bar"),is(CTVExtension.class));
    }

    @Test
    public void testParams()
    {
        Object o;
        JSONParser parser = setupParser();
//        CtorParams p = parser.parse(CtorParams.class,
//            "{\"foo\":123,\"bar\":[1,2,3]}");
//
//        assertThat(p,is(notNullValue()));
//        assertThat((Long) p.getMap().get("foo"),is(123l));
//        o = p.getMap().get("bar");
//        assertThat(o,is(List.class));
//        assertThat(((List)o).size(),is(3));

        CtorParams2 p2 = parser.parse(CtorParams2.class,
            "{\"foo\":\"abc\",\"bar\":[1,2,3]}");

        assertThat(p2,is(notNullValue()));
        assertThat( p2.getMap().get("foo"),is(nullValue()));
        assertThat( p2.getFoo(),is("abc"));
        o = p2.getMap().get("bar");
        assertThat(o,is(List.class));
        assertThat(((List)o).size(),is(3));
    }
}
