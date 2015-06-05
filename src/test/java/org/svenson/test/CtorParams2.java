package org.svenson.test;

import org.svenson.JSONParameter;
import org.svenson.JSONParameters;

import java.util.Collections;
import java.util.Map;

public class CtorParams2
{
    private final Map<String, Object> map;

    private final String foo;

    public CtorParams2(
        @JSONParameter("foo") String foo,
        @JSONParameters Map<String, Object> map
    )
    {
        this.map = map;
        this.foo = foo;
    }

    public Map<String, Object> getMap()
    {
        if (map == null)
        {
            return Collections.emptyMap();
        }

        return map;
    }

    public String getFoo()
    {
        return foo;
    }
}
