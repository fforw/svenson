package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.svenson.test.Priority;
import org.svenson.test.NegativePriority;
import org.svenson.test.PriorityOnBar;

public class TestPropertyPriority
{
    @Test
    public void test()
    {
        Priority order = new Priority();
        order.setBar("bar");
        order.setFoo("foo");
        
        assertThat(JSON.defaultJSON().forValue(order), is("{\"foo\":\"foo\",\"bar\":\"bar\"}"));

        NegativePriority order2 = new NegativePriority();
        order2.setBar("bar");
        order2.setFoo("foo");
        
        assertThat(JSON.defaultJSON().forValue(order2), is("{\"bar\":\"bar\",\"foo\":\"foo\"}"));

        PriorityOnBar order3 = new PriorityOnBar();
        order3.setBar("bar");
        order3.setFoo("foo");
        
        assertThat(JSON.defaultJSON().forValue(order3), is("{\"bar\":\"bar\",\"foo\":\"foo\"}"));

    }
}
