package org.svenson;

import org.apache.log4j.Logger;
import org.svenson.TypeMapper;
import org.svenson.tokenize.JSONTokenizer;
import org.svenson.tokenize.Token;

public class TokenLogger
    implements TypeMapper
{
    protected static Logger log = Logger.getLogger(TokenLogger.class);
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
