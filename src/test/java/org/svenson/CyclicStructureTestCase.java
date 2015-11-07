package org.svenson;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.notification.Failure;
import org.svenson.test.CyclicBean;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class CyclicStructureTestCase
{
    @Test
    public void testCycle() throws Exception
    {

        CyclicBean bean = new CyclicBean();
        bean.setInner(new CyclicBean.Inner(bean));

        try
        {
            JSON.defaultJSON().forValue(bean);

        }
        catch(CyclicStructureException e)
        {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));

            // make sure the stacktrace shows this method
            assertThat(sw.getBuffer().toString(),containsString("org.svenson.CyclicStructureTestCase.testCycle"));
            return;
        }
        Assert.fail("Should have thrown a CyclicStructureException");
    }
}
