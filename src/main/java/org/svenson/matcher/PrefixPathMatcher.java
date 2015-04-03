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
    private String pathPrefix;
    
    public PrefixPathMatcher(String parsePath)
    {
        this.pathPrefix = parsePath;
    }
    
    public boolean matches(String parsePath, Class typeHint)
    {
        return parsePath.startsWith(pathPrefix);
    }

    @Override
    public String toString()
    {
        return "path starts with '" + pathPrefix + "'";
    }
}
