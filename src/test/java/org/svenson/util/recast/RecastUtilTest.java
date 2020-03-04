package org.svenson.util.recast;

import org.junit.Test;
import org.svenson.JSONParser;
import org.svenson.util.RecastUtil;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class RecastUtilTest
{

    @Test
    public void testRecastingPrimitives()
    {
        assertThat(recast(Object.class, "null"), is(nullValue()));
        assertThat((Long) recast(Object.class, "387"), is(387L));
        assertThat((String) recast(Object.class, "\"abc\""), is("abc"));
        assertThat((Boolean) recast(Object.class, "true"), is(true));
    }


    @Test
    public void testRecastingPOJOs()
    {
        //language=JSON
        final RecastTarget result = recast(RecastTarget.class, "{\n" +
            "    \"name\" : \"Target-01\",\n" +
            "    \"num\" : 9348,\n" +
            "    \"components\": [\n" +
            "        {\n" +
            "            \"name\" : \"Component-01\"\n" +
            "        " +
            "},\n" +
            "        {\n" +
            "            \"name\" : \"Component-02\"\n" +
            "        }\n" +
            "    ]\n" +
            "}");

        assertThat(result.getName(), is("Target-01"));
        assertThat(result.getNum(), is(9348));
        assertThat(result.getComponents().size(), is(2));
        assertThat(result.getComponents().get(0).getName(), is("Component-01"));
        assertThat(result.getComponents().get(1).getName(), is("Component-02"));

    }

    @Test
    public void testRecastingPOJOsWithMapProps()
    {
        //language=JSON
        final RecastMapPropTarget result = recast(RecastMapPropTarget.class, "{\n" +
            "    \"components\": {\n" +
            "        \"doo\": {\n" +
            "            \"name\": \"Component-03\"\n" +
            "        },\n" +
            "        \"dee\": {\n" +
            "            \"name\": \"Component-04\"\n" +
            "        }\n" +
            "    " +
            "}\n" +
            "}");

        assertThat(result.getComponents().size(), is(2));
        assertThat(result.getComponents().get("doo").getName(), is("Component-03"));
        assertThat(result.getComponents().get("dee").getName(), is("Component-04"));

    }


    private <T> T recast(Class<T> cls, String json)
    {
        // intentionally parse into generic JSON graph
        final Object jsonGraph = JSONParser.defaultJSONParser().parse(json);
        // .. and then recast
        return RecastUtil.recast(cls, jsonGraph);
    }
}
