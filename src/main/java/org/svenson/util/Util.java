package org.svenson.util;

/**
 * Contains utility methods.
 * @author fforw at gmx dot de
 *
 */
public class Util
{
    protected Util()
    {
    }

    /**
     * Null-safe equals implementation.
     *
     * @param a     first object
     * @param b     second object
     *              
     * @return <code>true</code> if a is not <code>null</code> and equals b or if a and b are both <code>null</code>.
     */
    public static boolean equals(Object a, Object b)
    {
        return ((a == null && b == null) || (a != null && a.equals(b)));
    }

    /**
     * Returns the hash code of the given Object or <code>0</code> if the Object is <code>null</code>.
     * @param o     object
     *
     * @return  hash code
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
