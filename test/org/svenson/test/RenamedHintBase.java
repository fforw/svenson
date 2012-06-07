package org.svenson.test;

import java.util.ArrayList;
import java.util.List;

import org.svenson.JSONProperty;
import org.svenson.JSONTypeHint;

public class RenamedHintBase
{

    private List<RenamedHintChild> renamedHintChildList = new ArrayList<RenamedHintChild>();


    @JSONProperty(value = "test", ignoreIfNull = true)
    @JSONTypeHint(RenamedHintChild.class)
    public List<RenamedHintChild> getRenamedHintChildList()
    {
        return renamedHintChildList;
    }


    public void setRenamedHintChildList(List<RenamedHintChild> renamedHintChildList)
    {
        this.renamedHintChildList = renamedHintChildList;
    }

}
