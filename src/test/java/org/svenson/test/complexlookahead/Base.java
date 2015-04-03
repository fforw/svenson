package org.svenson.test.complexlookahead;

import org.svenson.JSONProperty;

/**
* Created by sven on 21.02.15.
*/
public abstract class Base
{
    @JSONProperty(readOnly = true)
    public String getType()
    {
        return this.getClass().getSimpleName();
    }
}
