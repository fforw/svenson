package org.svenson;

import org.svenson.tokenize.JSONTokenizer;

/**
 * Inspects the current position of a tokenizer and decides what type to use at that position.
 *
 * @author shelmberger
 */
public interface TypeMapper
{
    /**
     * Returns the type to use for the current tokenizer position or <code>null</code>, if the type mapper does not want to
     * change the type used at this position. The type mapper must reset the tokenizer to a token stream position that allows
     * the parsing to continue. this will usually be the tokenizer position on method entry. (i.e. the first token received by the
     * type mapper needs to be fed to {@link JSONTokenizer#pushBack(org.svenson.tokenize.Token)}
     *
     * @param tokenizer         tokenizer to get the tokens from. needs to set to a position behind the inspected value to continue
     *                          parsing correctly.
     *
     * @param parsePathInfo     the current parsing path within the root object
     * @param typeHint          initial type hint as configured by {@link JSONParser#addTypeHint(String, Class)}, can also be <code>null</code>.
     *
     * @return type hint or <code>null</code>, if the type mapper does not want to
     * change the type used at this position.
     */
    Class getTypeHint(JSONTokenizer tokenizer, String parsePathInfo, Class typeHint);
}
