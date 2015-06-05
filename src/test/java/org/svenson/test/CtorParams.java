package org.svenson.test;

import org.svenson.JSONParameters;

import java.util.Collections;
import java.util.Map;

public class CtorParams
{
    private final Map<String, Object> map;

    public CtorParams(
        @JSONParameters Map<String, Object> map
    )
    {
        this.map = map;
    }

    public Map<String, Object> getMap()
    {
        if (map == null)
        {
            return Collections.emptyMap();
        }

        return map;
    }
}
