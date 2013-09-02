package org.svenson.info;

import java.util.Comparator;

import org.svenson.JSONProperty;

/**
 * Sorts {@link JSONPropertyInfo} by their priority, highest first, which reflects the priority attribute of the marking {@link JSONProperty} or 
 * a default of <code>0</code>.
 * 
 * @author fforw at gmx dot de
 *
 */
public class JSONPropertyPriorityComparator
    implements Comparator<JSONPropertyInfo>
{

    public int compare(JSONPropertyInfo o1, JSONPropertyInfo o2)
    {
        return o2.getPriority() - o1.getPriority();
    }

}
