package org.svenson.matcher;

/**
 * Matches if all of its child matchers match.
 * 
 * @author fforw at gmx dot de
 *
 */
public class AndMatcher implements PathMatcher
{
    private PathMatcher[] matchers;
    
    public AndMatcher(PathMatcher... matchers)
    {
        this.matchers = matchers;
    }

    public boolean matches(String parsePath, Class typeHint)
    {
        boolean matches = true;
        for (PathMatcher matcher : matchers)
        {
            if (!matcher.matches(parsePath, typeHint))
            {
                matches = false;
                break;
            }
        }
        return matches;

    }
    
    @Override
    public String toString()
    {
        return toString("AND",matchers);
    }

    static String toString(String name, PathMatcher[] matchers)
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append(name).append('(');
        for (PathMatcher m : matchers)
        {
            if (!first)
            {
                sb.append(", ");
            }
            
            sb.append(m);
            first = false;
        }
        sb.append(')');
        return sb.toString();
    }
}
