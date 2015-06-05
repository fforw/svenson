package org.svenson.matcher;

/**
 * Matches the parse path based on a Suffix.
 * 
 * @author fforw at gmx dot de
 *
 */
public class TruePathMatcher
    implements PathMatcher
{
    public boolean matches(String parsePath, Class typeHint)
    {
        return true;
    }

    @Override
    public String toString()
    {
        return "always match path";
    }
}
