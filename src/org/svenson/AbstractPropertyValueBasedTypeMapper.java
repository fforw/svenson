package org.svenson;

import org.svenson.tokenize.JSONTokenizer;
import org.svenson.tokenize.Token;
import org.svenson.tokenize.TokenType;


/**
 * Abstract base class for type mappers based on a map with a generic key type.
 *
 * @author shelmberger
 *
 */
public abstract class AbstractPropertyValueBasedTypeMapper extends AbstractTypeMapper 
{
    protected boolean allowUndefined;

    /**
     * If set to <code>true</code>, java.util.HashMap will be used for
     * discriminator field values that are mapped to no class. Otherwise an
     * exception is thrown in that case.
     *
     * @param allowUndefined
     */
    public void setAllowUndefined(boolean allowUndefined)
    {
        this.allowUndefined = allowUndefined;
    }

    /**
     * Field whose value is used to tell one type from the other. (default "type")
     */
    protected String discriminatorField = "type";

    /**
     * Parse path info the mapping is applied to.
     */
    private String parsePathInfo;


    /**
     * Sets the parse path info at which the type discrimination is applied.
     *
     * @param parsePathInfo
     */
    public void setParsePathInfo(String parsePathInfo)
    {
        this.parsePathInfo = parsePathInfo;
    }

    /**
     * Sets the property used to discriminate between the different document
     * types
     *
     * @param discriminatorField
     */
    public void setDiscriminatorField(String discriminatorField)
    {
        this.discriminatorField = discriminatorField;
    }

    public Class getTypeHint(JSONTokenizer tokenizer, String parsePathInfo, Class typeHint)
    {
        if (this.parsePathInfo == null)
        {
            throw new IllegalStateException("parse path info not configured.");
        }

        if (this.parsePathInfo.equals(parsePathInfo))
        {
            tokenizer.startRecording();
            Token first = tokenizer.next();

            if (first.type() == TokenType.END)
            {
                throw new IllegalStateException("Unexpected end");
            }
            
            try
            {
                Object value = getPropertyValueFromTokenStream(tokenizer, discriminatorField, first);
                return getTypeHintFromTypeProperty(value);
            }
            finally
            {
                tokenizer.pushBack(first);
            }
        }
        return typeHint;
    }

    protected abstract Class getTypeHintFromTypeProperty(Object value) throws IllegalStateException;
}
