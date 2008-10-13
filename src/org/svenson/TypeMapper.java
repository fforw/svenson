package org.svenson;

import org.svenson.parse.JSONTokenizer;

/**
 * Inspects the current position of a tokenizer and decides what type to use for that object.
 *
 * @author shelmberger
 */
public interface TypeMapper
{
    /**
     * Returns the type to use for the current tokenizer position
     *
     * @param tokenizer         tokenizer to get the tokens from. needs to set to a position behind the inspected value to continue
     *                          parsing correctly.
     *
     * @param parsePathInfo     the current parsing path within the root object
     * @param typeHint          initial type hint or <code>null</code>
     *
     * @return type hint or <code>null</code>
     */
    Class getTypeHint(JSONTokenizer tokenizer, String parsePathInfo, Class typeHint);
}
