package tutorial;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.svenson.JSONParser;
import org.svenson.matcher.OrMatcher;
import org.svenson.matcher.PrefixPathMatcher;
import org.svenson.matcher.RegExPathMatcher;

public class CollectionsTestCase
{
    @Test
    public void testParsingList()
    {
        String json = "[{\"field\":\"value1\"},{\"field\":\"value2\"}]";
        
        JSONParser jsonParser = new JSONParser();
        jsonParser.addTypeHint("[]", Something.class);
        List<Something> someThings = jsonParser.parse(List.class, json);
        
        assertThat(someThings.get(0).getField(), is("value1"));
        assertThat(someThings.get(1).getField(), is("value2"));
        
    }

    @Test
    public void testParsingMap()
    {
        String json = "{\"str\":\"aaa\",\"f1\":{\"field\":\"value1\"},\"f2\":{\"field\":\"value2\"}}";
        
        JSONParser jsonParser = new JSONParser();
        jsonParser.addTypeHint(new RegExPathMatcher("\\.(f1|f2)"), Something.class);
        Map<String,Object> someThings = jsonParser.parse(Map.class, json);
        
        assertThat((String)someThings.get("str"), is("aaa"));
        
        Something st = (Something)someThings.get("f1");
        assertThat(st.getField(), is("value1"));
        st = (Something)someThings.get("f2");
        assertThat(st.getField(), is("value2"));
        
    }

    @Test
    public void testParsingMap2()
    {
        String json = "{\"str\":\"aaa\",\"f1\":{\"field\":\"value1\"},\"f2\":{\"field\":\"value2\"}}";
        
        JSONParser jsonParser = new JSONParser();
        jsonParser.addTypeHint(new OrMatcher(
            new PrefixPathMatcher(".f1"),
            new PrefixPathMatcher(".f2")), Something.class);
        Map<String,Object> someThings = jsonParser.parse(Map.class, json);
        
        assertThat((String)someThings.get("str"), is("aaa"));
        
        Something st = (Something)someThings.get("f1");
        assertThat(st.getField(), is("value1"));
        st = (Something)someThings.get("f2");
        assertThat(st.getField(), is("value2"));
        
    }
    
    public static class Something
    {
        private String field;

        public String getField()
        {
            return field;
        }

        public void setField(String field)
        {
            this.field = field;
        }
        
    }
}
