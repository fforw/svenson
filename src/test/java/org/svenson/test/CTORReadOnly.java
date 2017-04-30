package org.svenson.test;

import org.svenson.JSONParameter;
import org.svenson.JSONProperty;

public class CTORReadOnly
{
    private final String field;

    private String meta = "meta value";

    public CTORReadOnly(
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


    @JSONProperty(value = "meta", readOnly = true)
    public String getMeta()
    {
        return meta;
    }

}
