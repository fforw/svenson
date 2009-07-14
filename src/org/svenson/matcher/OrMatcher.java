package org.svenson.matcher;

/**
 * Matches if at least one of its child matchers matches.
 * 
 * @author shelmberger
 *
 */
public class OrMatcher implements PathMatcher
{
    private PathMatcher[] matchers;
    
    public OrMatcher(PathMatcher... matchers)
    {
        this.matchers = matchers;
    }

    public boolean matches(String parsePath)
    {
        for (PathMatcher matcher : matchers)
        {
            if (matcher.matches(parsePath))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
        return AndMatcher.toString("OR",matchers);
    }
}
