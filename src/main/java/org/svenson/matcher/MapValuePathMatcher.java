package org.svenson.matcher;

import java.util.regex.Pattern;

/**
 * Matches all field in root map of a JSON dataset.
 * 
 * @author fforw at gmx dot de
 *
 */
public class MapValuePathMatcher
    implements PathMatcher
{
    final static Pattern MAP_VALUE_PATTERN = Pattern.compile("^\\.[^\\.\\[]+$", Pattern.CASE_INSENSITIVE ); 

    public boolean matches(String parsePath, Class typeHint)
    {
        return MAP_VALUE_PATTERN.matcher(parsePath).matches();
    }
}
