package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.Test;
import org.svenson.matcher.SubtypeMatcher;


public class ClassNameBasedTypeMapperTestCase
{

    @Test
    public void test()
    {
        JSONParser parser = new JSONParser();
        ClassNameBasedTypeMapper mapper = new ClassNameBasedTypeMapper();
        mapper.setParsePathInfo("[]");
        parser.setTypeMapper(mapper);
        
        List foos = parser.parse(List.class, "[{\"type\":\"org.svenson.ClassNameBasedTypeMapperTestCase$Foo\"},{\"type\":\"org.svenson.ClassNameBasedTypeMapperTestCase$Bar\"}]");
        assertThat(foos.size(), is(2));
        assertThat(foos.get(0), isA(Foo.class));
        assertThat(foos.get(1), isA(Bar.class));
    }

    @Test
    public void testWithBasePackage()
    {
        JSONParser parser = new JSONParser();
        ClassNameBasedTypeMapper mapper = new ClassNameBasedTypeMapper();
        mapper.setBasePackage("org.svenson");
        mapper.setParsePathInfo("[]");
        parser.setTypeMapper(mapper);
        
        List foos = parser.parse(List.class, "[{\"type\":\"ClassNameBasedTypeMapperTestCase$Foo\"},{\"type\":\"ClassNameBasedTypeMapperTestCase$Bar\"}]");
        assertThat(foos.size(), is(2));
        assertThat(foos.get(0), isA(Foo.class));
        assertThat(foos.get(1), isA(Bar.class));
    }
    
    @Test
    public void testSubTypeMatching()
    {
        JSONParser parser = new JSONParser();
        ClassNameBasedTypeMapper mapper = new ClassNameBasedTypeMapper();
        mapper.setBasePackage("org.svenson");
        mapper.setPathMatcher(new SubtypeMatcher(Foo.class));
        parser.setTypeMapper(mapper);
        
        Foo foo = parser.parse(Foo.class, "{\"type\":\"ClassNameBasedTypeMapperTestCase$Bar\"}");
        assertThat(foo,isA(Bar.class));
        
    }
    
    public static class Foo
    {
        private String type;
        public void setType(String type)
        {
            this.type = type;
        }
        public String getType()
        {
            return type;
        }
    }

    public static class Bar extends Foo
    {
        
    }
    
    
}
