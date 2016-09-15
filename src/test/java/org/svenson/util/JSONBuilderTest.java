package org.svenson.util;

import org.junit.After;
import org.junit.Test;
import org.svenson.JSON;
import org.svenson.StringBuilderSink;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

import static org.svenson.util.JSONBuilder.*;

public class JSONBuilderTest
{
    @Test
    public void testObject() throws Exception
    {
        String json = buildObject().property("foo", 12).output();
        assertThat(json, is("{\"foo\":12}"));
        assertThat(buildObject().property("foo", 12).property( "bar", "abc").output(), is("{\"foo\":12,\"bar\":\"abc\"}"));
    }

    @Test
    public void testArray() throws Exception
    {
        assertThat(buildArray().element(12).output(), is("[12]"));
        assertThat(buildArray().elements(12,"abc").output(), is("[12,\"abc\"]"));
        assertThat(buildArray().elements(12).element("abc").output(), is("[12,\"abc\"]"));
        assertThat(buildArray().elements(Arrays.asList(1,2,3)).output(), is("[1,2,3]"));
        assertThat(buildArray().element(Arrays.asList(1,2,3)).output(), is("[[1,2,3]]"));
    }

    @Test
    public void testNesting() throws Exception
    {
        assertThat(buildObject().objectProperty("foo").property("bar", 1).output(), is("{\"foo\":{\"bar\":1}}"));
        assertThat(buildObject().arrayProperty("numbers").elements(1,2,3).output(), is("{\"numbers\":[1,2,3]}"));
        assertThat(
            buildObject()
                .arrayProperty("people")
                    .objectElement()
                        .property("name", "Anna")
                    .close()
                    .objectElement()
                        .property("name", "Bob")
                    // We don't need to close all levels, they're autoclosed on output
                    .output(), is("{\"people\":[{\"name\":\"Anna\"},{\"name\":\"Bob\"}]}"));
    }

    @Test
    public void testComplex() throws Exception
    {

        assertThat(
            buildObject()
                .property("complex", new Bean("Hugo"))
                .output(), is("{\"complex\":{\"name\":\"Hugo\"}}"));

    }

    @Test
    public void testDepth() throws Exception
    {

        JSONBuilder b = buildObject();

        JSONBuilder.Level lvl = b.getCurrentLevel();
        sub(b);
        b.closeUntil(lvl);
        b.property("appended", true);

        assertThat(b.output(), is("{\"value\":{\"s1\":{\"s2\":{\"v\":true}}},\"appended\":true}"));

    }


    @Test
    public void testInclude() throws Exception
    {

        assertThat(
            buildObject()
                .property("foo", 1)
                .includeProperty("included", "{\"bar\":2}")
                .output(), is("{\"foo\":1,\"included\":{\"bar\":2}}"));

        assertThat(
            buildArray()
                .objectElement()
                .property("bar", 1)
                .close()
                .includeElement("{\"bar\":2}")
                .output(), is("[{\"bar\":1},{\"bar\":2}]"));

    }


    @Test
    public void testAllEntryPoints() throws Exception
    {
        {
            assertThat(JSONBuilder.buildObject().output(), is("{}"));
            assertThat(JSONBuilder.buildObject(new JSON()).output(), is("{}"));
            StringBuilderSink sink = new StringBuilderSink();
            sink.append("!");
            assertThat(JSONBuilder.buildObject(new JSON(), sink).output(), is("!{}"));
        }

        {
            assertThat(JSONBuilder.buildArray().output(), is("[]"));
            assertThat(JSONBuilder.buildArray(new JSON()).output(), is("[]"));
            StringBuilderSink sink = new StringBuilderSink();
            sink.append("!");
            assertThat(JSONBuilder.buildArray(new JSON(), sink).output(), is("![]"));
        }
    }


    @Test
    public void testPropertyUnlessNull() throws Exception
    {
        assertThat(buildObject().propertyUnlessNull("foo", null).output(), is("{}"));
        assertThat(buildObject().propertyUnlessNull("foo", 1).output(), is("{\"foo\":1}"));

    }


    private void sub(JSONBuilder b)
    {
        b.objectProperty("value")
            .objectProperty("s1")
                .objectProperty("s2")
                    .property("v", true);
    }


    public static class Bean
    {
        private final String name;


        public Bean(String name)
        {
            this.name = name;
        }


        public String getName()
        {
            return name;
        }
    }

    @Test(expected = IllegalBuilderStateException.class)
    public void testLockedProperty() throws Exception
    {

        buildObject().close().property("a",1);
    }


    @Test(expected = IllegalBuilderStateException.class)
    public void testLockedClose() throws Exception
    {

        buildObject().close().close();
    }

    @Test(expected = IllegalBuilderStateException.class)
    public void testLockedElement() throws Exception
    {

        buildObject().close().element(0);
    }

    @Test(expected = IllegalBuilderStateException.class)
    public void testLockedObjectProperty() throws Exception
    {

        buildObject().close().objectProperty("a");
    }

    @Test(expected = IllegalBuilderStateException.class)
    public void testLockedObjectElement() throws Exception
    {

        buildObject().close().objectElement();
    }

    @Test(expected = IllegalBuilderStateException.class)
    public void testLockedArrayProperty() throws Exception
    {

        buildObject().close().arrayProperty("a");
    }

    @Test(expected = IllegalBuilderStateException.class)
    public void testLockedArrayElement() throws Exception
    {

        buildObject().close().arrayElement();
    }
}
