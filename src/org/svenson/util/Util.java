package org.svenson.util;

/**
 * Contains utility methods.
 * @author shelmberger
 *
 */
public class Util
{
    protected Util()
    {
    }

    /**
     * Safe equals implementation.
     * @param a
     * @param b
     * @return <code>true</code> if a is not <code>null</code> and equals b or if a and b are both <code>null</code>.
     */
    public static boolean equals(Object a, Object b)
    {
        return ((a == null && b == null) || (a != null && a.equals(b)));
    }

    /**
     * Returns the hash code of the given Object or <code>0</code> if the Object is <code>null</code>.
     * @param o
     * @return
     */
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
