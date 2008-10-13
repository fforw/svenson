package org.svenson.util;


public class Util
{
    public static boolean equals(Object a, Object b)
    {
        return ((a == null && b == null) || (a != null && a.equals(b)));
    }

    public static int safeHashcode(Object o)
    {
        if (o == null)
        {
            return 0;
        }
        else
        {
            return o.hashCode();
        }
    }

}
