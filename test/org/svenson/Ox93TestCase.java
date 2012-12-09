package org.svenson;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class Ox93TestCase
{
    @Test
    public void test() throws IOException
    {
        String file = FileUtils.readFileToString(new File("./test/org/svenson/10571721.json"));
        
        Map map = JSONParser.defaultJSONParser().parse(Map.class, file);
        
        List descriptions = (List)map.get("descriptions");
        Map first = (Map)descriptions.get(0);
        Object value = first.get("value");
        assertThat((String)value, is("<p>Het VARA/VPRO drieluik \u00939/11: de dag die de wereld veranderde\u0094 is een zoektocht naar de voedingsbodem en de achtergronden van de aanslagen van 11 september 2001.</p>"));
        
    }
}
