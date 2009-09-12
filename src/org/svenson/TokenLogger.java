package org.svenson;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.TypeMapper;
import org.svenson.tokenize.JSONTokenizer;
import org.svenson.tokenize.Token;

/**
 * TypeMapper that just logs the calls
 *
 * @author shelmberger
 *
 */
public class TokenLogger
    implements TypeMapper
{
    protected static Logger log = LoggerFactory.getLogger(TokenLogger.class);
    public Class getTypeHint(JSONTokenizer tokenizer, String parsePathInfo, Class typeHint)
    {
        if (log.isDebugEnabled())
        {
            Token token = tokenizer.next();
            log.debug("token = "+token+", parsePathInfo = "+parsePathInfo+", typeHint = "+typeHint);

        }

        return typeHint;
    }

}
