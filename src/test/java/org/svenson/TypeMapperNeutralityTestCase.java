package org.svenson;

import org.junit.Test;
import org.svenson.matcher.PrefixPathMatcher;
import org.svenson.matcher.TruePathMatcher;

/**
 * Created by sven on 07.10.15.
 */
public class TypeMapperNeutralityTestCase
{

    @Test
    public void testName() throws Exception
    {
        JSONParser parser = new JSONParser();
        Mapper typeMapper = new Mapper();
        typeMapper.setPathMatcher(new TruePathMatcher());

        parser.setTypeMapper(typeMapper);

        parser.parse("{\"foo\":1}");

    }

    private static class Mapper extends AbstractPropertyValueBasedTypeMapper
    {
        @Override
        protected Class getTypeHintFromTypeProperty(Object value) throws IllegalStateException
        {
            return null;
        }
    }
}
