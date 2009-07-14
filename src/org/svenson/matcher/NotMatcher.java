package org.svenson.matcher;

public class NotMatcher implements PathMatcher
{
    private PathMatcher matcher;
    
    public NotMatcher(PathMatcher matcher)
    {
        this.matcher = matcher;
    }

    public boolean matches(String parsePath)
    {
        return !matcher.matches(parsePath);
    }
    
    @Override
    public String toString()
    {
        
        return "NOT "+matcher;
    }
}
