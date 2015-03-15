package org.svenson;

import org.junit.Test;
import org.svenson.test.AddImmutables;
import org.svenson.test.Bar;
import org.svenson.test.ConstructorParametrization;
import org.svenson.test.Immutable;
import org.svenson.test.ListOfImmutables;
import org.svenson.test.MapOfImmutables;
import org.svenson.test.TypedListCTOR;
import org.svenson.test.TypedMapCTOR;

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

}
