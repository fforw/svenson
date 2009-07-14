package org.svenson;

import org.svenson.matcher.EqualsPathMatcher;
import org.svenson.matcher.PathMatcher;
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
    private PathMatcher pathMatcher;


    /**
     * Sets the parse path info at which the type discrimination is applied.
     *
     * @param parsePathInfo
     */
    public void setParsePathInfo(String parsePathInfo)
    {
        this.pathMatcher = new EqualsPathMatcher(parsePathInfo);
    }
    
    public void setPathMatcher(PathMatcher pathMatcher)
    {
        this.pathMatcher = pathMatcher;
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
        if (this.pathMatcher == null)
        {
            throw new IllegalStateException("path matcher not configured.");
        }

        if (pathMatcher.matches(parsePathInfo))
        {
            tokenizer.startRecording();
            Token first = tokenizer.next();

            if (first.type() == TokenType.END)
            {
                throw new IllegalStateException("Unexpected end");
            }
            
            try
            {
                Token token = first;
                if (first.type() == TokenType.BRACE_OPEN)
                {
                    token = tokenizer.next();
                }
                
                Object value = getPropertyValueFromTokenStream(tokenizer, discriminatorField, token);
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
