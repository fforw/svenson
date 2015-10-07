package org.svenson;

import org.junit.Test;
import org.svenson.test.PostConstructBean;
import org.svenson.test.PostConstructContainer;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

public class PostConstructTestCase
{
    @Test
    public void testPostConstruct() throws Exception
    {
        JSONParser parser = JSONParser.defaultJSONParser();
        PostConstructBean bean = parser.parse(PostConstructBean.class, "{\"foo\":12}");
        assertThat(bean.getFoo(), is(12));
        assertThat(bean.isInitialized(), is(true));


        PostConstructContainer container = parser.parse(PostConstructContainer.class, "{\n" +
            "  \"list\" : [{\"foo\" : 1},{\"foo\" : 2}],\n" +
            "  \"map\" : {\n" +
            "    \"three\" : {\"foo\" : 3},\n" +
            "    \"four\" : {\"foo\" : 4}\n" +
            "  }\n" +
            "}");

        assertThat(container.getList().get(0).getFoo(),is(1));
        assertThat(container.getList().get(1).getFoo(),is(2));
        assertThat(container.getMap().get("three").getFoo(),is(3));
        assertThat(container.getMap().get("four").getFoo(),is(4));

        assertThat(container.getList().get(0).isInitialized(),is(true));
        assertThat(container.getList().get(1).isInitialized(),is(true));
        assertThat(container.getMap().get("three").isInitialized(),is(true));
        assertThat(container.getMap().get("four").isInitialized(),is(true));
    }
}
