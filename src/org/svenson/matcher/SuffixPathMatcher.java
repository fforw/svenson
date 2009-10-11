package org.svenson.matcher;

/**
 * Matches the parse path based on a Suffix.
 * 
 * @author shelmberger
 *
 */
public class SuffixPathMatcher
    implements PathMatcher
{
    private String parsePath;
    
    public SuffixPathMatcher(String parsePath)
    {
        this.parsePath = parsePath;
    }
    
    public boolean matches(String parsePath, Class typeHint)
    {
        return this.parsePath.endsWith(parsePath);
    }

    @Override
    public String toString()
    {
        return "path ends with '" + parsePath + "'";
    }
}
