package org.svenson.info;

import java.util.Comparator;

public class JSONPropertyInfoComparator
    implements Comparator<JSONPropertyInfo>
{

    public int compare(JSONPropertyInfo o1, JSONPropertyInfo o2)
    {
        return o2.getPriority() - o1.getPriority();
    }

}
