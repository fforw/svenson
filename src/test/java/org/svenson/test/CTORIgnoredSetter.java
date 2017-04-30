package org.svenson.test;

import org.svenson.JSONParameter;
import org.svenson.JSONProperty;

/**
 * Created by sven on 30.04.17.
 */
public class CTORIgnoredSetter
{

    private final String field;

    private String meta;

    public CTORIgnoredSetter(
        @JSONParameter("field")
            String field
    )
    {
        this.field = field;
    }


    public String getField()
    {
        return field;
    }


    public String getMeta()
    {
        return meta;
    }

    @JSONProperty(ignore = true)
    public void setMeta(String meta)
    {
        this.meta = meta;
    }
}
