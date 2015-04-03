package org.svenson.matcher;

/**
 * Matches the parse path based on a Suffix.
 * 
 * @author fforw at gmx dot de
 *
 */
public class SuffixPathMatcher
    implements PathMatcher
{
    private String pathSuffix;
    
    public SuffixPathMatcher(String pathSuffix)
    {
        this.pathSuffix = pathSuffix;
    }
    
    public boolean matches(String parsePath, Class typeHint)
    {
        return parsePath.endsWith(pathSuffix);
    }

    @Override
    public String toString()
    {
        return "path ends with '" + pathSuffix + "'";
    }
}
