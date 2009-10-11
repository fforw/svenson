package org.svenson.matcher;

/**
 * Parse Path Matcher that matches to specific paths inside a JSON object graph.
 * 
 * @author shelmberger
 *
 */
public interface PathMatcher
{
    boolean matches(String parsePath, Class typeHint);
}
