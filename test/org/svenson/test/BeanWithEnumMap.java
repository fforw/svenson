package org.svenson.test;

import java.util.HashMap;
import java.util.Map;

import org.svenson.JSONTypeHint;

public class BeanWithEnumMap
{
    private Map<String, SomeEnum> enums = new HashMap<String, SomeEnum>();

    public Map<String, SomeEnum> getEnums()
    {
        return enums;
    }

    @JSONTypeHint(SomeEnum.class)
    public void setEnums(Map<String, SomeEnum> enums)
    {
        this.enums = enums;
    }

}
