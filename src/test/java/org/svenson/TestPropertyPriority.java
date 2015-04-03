package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;
import org.svenson.test.Priority;
import org.svenson.test.NegativePriority;
import org.svenson.test.PriorityOnBar;
import org.svenson.test.SortedAlpha;
import org.svenson.test.SortedDescAlpha;

public class TestPropertyPriority
{
    @Test
    public void test()
    {
        {
            Priority order = new Priority();
            order.setBar("bar");
            order.setFoo("foo");

            assertThat(JSON.defaultJSON().forValue(order), is("{\"foo\":\"foo\",\"bar\":\"bar\"}"));

        }
        {
            NegativePriority order = new NegativePriority();
            order.setBar("bar");
            order.setFoo("foo");

            assertThat(JSON.defaultJSON().forValue(order), is("{\"bar\":\"bar\",\"foo\":\"foo\"}"));
        }

        {
            PriorityOnBar order = new PriorityOnBar();
            order.setBar("bar");
            order.setFoo("foo");
            
            assertThat(JSON.defaultJSON().forValue(order), is("{\"bar\":\"bar\",\"foo\":\"foo\"}"));
        }

        {
            SortedAlpha order = new SortedAlpha();
            assertThat(JSON.defaultJSON().forValue(order), is("{\"a\":0,\"b\":0,\"c\":0}"));
        }

        {
            SortedDescAlpha order = new SortedDescAlpha();
            assertThat(JSON.defaultJSON().forValue(order), is("{\"c\":0,\"b\":0,\"a\":0}"));
        }

    }
}
