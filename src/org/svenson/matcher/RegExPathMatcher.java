package org.svenson.matcher;

import java.util.regex.Pattern;

/**
 * Matches the parse path based on a regular expression.
 * 
 * @author fforw at gmx dot de
 *
 */
public class RegExPathMatcher
    implements PathMatcher
{
    private Pattern pattern;
    
    public RegExPathMatcher(String regex)
    {
        this(regex,0);
    }
    
    public RegExPathMatcher(String regex, int flags)
    {
        this.pattern = Pattern.compile(regex, flags);
    }
    
    public boolean matches(String parsePath, Class typeHint)
    {
        return this.pattern.matcher(parsePath).matches();
    }
    
    @Override
    public String toString()
    {
        return "path matches '" + pattern + "'";
    }
}
