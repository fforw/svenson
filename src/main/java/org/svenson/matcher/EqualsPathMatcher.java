package org.svenson.matcher;

/**
 * Matches the parse path if it's equals to a special string. 
 * 
 * @author fforw at gmx dot de
 *
 */
public class EqualsPathMatcher
    implements PathMatcher
{
    private String parsePath;
    
    public EqualsPathMatcher(String parsePath)
    {
        this.parsePath = parsePath;
    }
    
    public boolean matches(String parsePath, Class typeHint)
    {
        return this.parsePath.equals(parsePath);
    }
    
    @Override
    public String toString()
    {
        return "path equals '" + parsePath + "'";
    }
}
