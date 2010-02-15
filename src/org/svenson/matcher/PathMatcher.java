package org.svenson.matcher;

/**
 * Parse Path Matcher that matches to specific paths inside a JSON object graph.
 * 
 * @author fforw at gmx dot de
 *
 */
public interface PathMatcher
{
    boolean matches(String parsePath, Class typeHint);
}
