package org.svenson.test;

import org.svenson.JSONParameters;
import org.svenson.JSONTypeHint;

import java.util.Map;

public class InjectorHint
{
    private final Map<String, Injected> injected;

    public InjectorHint(
        @JSONParameters
        @JSONTypeHint(Injected.class)
        Map<String, Injected> injected
    )
    {
        this.injected = injected;
    }

    public Map<String, Injected> getInjected()
    {
        return injected;
    }
}
