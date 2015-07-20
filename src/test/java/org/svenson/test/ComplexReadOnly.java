package org.svenson.test;

import org.svenson.JSONProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ComplexReadOnly
{
    @JSONProperty(value = "kid", readOnly = true)
    public ComplexReadOnlyChild getChild()
    {
        return null;
    }

    @JSONProperty(value = "kidArray", readOnly = true)
    public List<ComplexReadOnlyChild> getChildrenArray()
    {
        return Collections.emptyList();
    }
}
