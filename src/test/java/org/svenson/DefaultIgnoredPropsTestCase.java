package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import java.util.Arrays;

import org.junit.Test;
import org.svenson.test.IgnoreTestBean;

public class DefaultIgnoredPropsTestCase
{

    @Test
    public void thatDefaultIgnoringWorks()
    {
        IgnoreTestBean bean = new IgnoreTestBean();
        bean.setFoo("foo");
        bean.setBar("bar");
        bean.setBaz("baz");
        
        JSON json = new JSON();
        json.setIgnoredProperties(Arrays.asList("bar"));
        
        String output = json.forValue(bean);
        assertThat(output, containsString("\"foo\":\"foo\""));
        assertThat(output, containsString("\"baz\":\"baz\""));
        assertThat(output, not(containsString("\"bar\":\"bar\"")));
     
        output = json.forValue(bean, Arrays.asList("baz"));
        assertThat(output, containsString("\"foo\":\"foo\""));
        assertThat(output, containsString("\"bar\":\"bar\""));
        assertThat(output, not(containsString("\"baz\":\"baz\"")));
    }
    
}
