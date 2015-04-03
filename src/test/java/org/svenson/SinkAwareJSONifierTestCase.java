package org.svenson;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import org.junit.Test;
import org.svenson.test.Bean;

import junit.framework.Assert;

public class SinkAwareJSONifierTestCase
{
    @Test
    public void test()
    {
        JSONCharacterSink sink = createMock(JSONCharacterSink.class);
        sink.append("\"foo\"");
        expectLastCall().once();
        
        replay(sink);
        
        JSON json = new JSON();
        json.registerJSONifier(Bean.class, new TestSinkAwareJSONifier());
        json.dumpObject(sink, new Bean());
        verify(sink);
        
    }
    
    private static class TestSinkAwareJSONifier implements SinkAwareJSONifier
    {
        public void writeToSink(JSONCharacterSink sink, Object o)
        {
            sink.append("\"foo\"");
        }

        public String toJSON(Object o)
        {
            Assert.fail("toJSON called on SinkAwareJSONifier");
            return null;
        }
        
    }
}
