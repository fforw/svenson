package org.svenson.matcher;

/**
 * Matches the parse path based on a prefix
 * 
 * @author shelmberger
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
    
    public boolean matches(String parsePath)
    {
        return this.parsePath.startsWith(parsePath);
    }

    @Override
    public String toString()
    {
        return "path starts with '" + parsePath + "'";
    }
}
