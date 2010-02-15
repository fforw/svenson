package org.svenson.matcher;

/**
 * Matches the parse path based on a prefix
 * 
 * @author fforw at gmx dot de
 *
 */
public class PrefixPathMatcher
    implements PathMatcher
{
    private String parsePath;
    
    public PrefixPathMatcher(String parsePath)
    {
        this.parsePath = parsePath;
    }
    
    public boolean matches(String parsePath, Class typeHint)
    {
        return this.parsePath.startsWith(parsePath);
    }

    @Override
    public String toString()
    {
        return "path starts with '" + parsePath + "'";
    }
}
