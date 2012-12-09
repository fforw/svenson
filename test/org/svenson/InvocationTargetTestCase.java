package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.notNullValue;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Test;

public class InvocationTargetTestCase
{
    @Test
    public void testGetter()
    {
        String error = null;
        try
        {
            JSON.defaultJSON().forValue(new Target());
        }
        catch(Exception e)
        {
            error = getError(e);
        }
        assertThat(error,  notNullValue());
        assertThat(error,  containsString("getter error"));
    }
    
    @Test
    public void testSetter()
    {
        String error = null;
        try
        {
            System.out.println(JSONParser.defaultJSONParser().parse(Target.class, "{\"value\":\"foo\"}"));
        }
        catch(Exception e)
        {
            error = getError(e);
        }
        assertThat(error,  notNullValue());
        assertThat(error,  containsString("setter error foo"));
    }

    @Test
    public void testAdder()
    {
        String error = null;
        try
        {
            JSONParser parser = new JSONParser();
            System.out.println(parser.parse(Target.class, "{\"value\":[\"foo\",\"bar\"]}"));
        }
        catch(Exception e)
        {
            error = getError(e);
        }
        assertThat(error,  notNullValue());
        assertThat(error,  containsString("adder error foo"));
    }
    

    private String getError(Exception e)
    {
        String error;
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        
        error = sw.getBuffer().toString();
        return error;
    }
    
    public static class Target
    {
        public void setValue(String s)
        {
            throw new IllegalArgumentException("setter error " + s);
        }
        public String getValue()
        {
            throw new IllegalArgumentException("getter error");
        }
        
        public void addValue(String s)
        {
            throw new IllegalArgumentException("adder error " + s);
        }
    }

}
