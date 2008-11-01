package org.svenson;

import java.util.HashMap;
import java.util.Map;

import org.svenson.tokenize.JSONTokenizer;
import org.svenson.tokenize.Token;
import org.svenson.tokenize.TokenType;

/**
 * Abstract base class for type mappers based on a map with a generic key type.
 *
 * @author shelmberger
 *
 * @param <T> key type of the map used.
 */
public abstract class AbstractMapBasedTypeMapper<T> implements TypeMapper
{
    protected Map<T, Class> typeMap = new HashMap<T, Class>();

    protected boolean allowUndefined;

    /**
     * Fowards the given tokenizer to skips an object value (including all sub objects and arrays) if the
     * tokenizer is on the position <em>after</em> the opening brace.
     * @param tokenizer
     */
    protected void skipObjectValue(JSONTokenizer tokenizer)
    {
        skipComplexValue(tokenizer, TokenType.BRACE_OPEN, TokenType.BRACE_CLOSE);
    }

    /**
     * Fowards the given tokenizer to skips an array value (including all sub objects and arrays) if the
     * tokenizer is on the position <em>after</em> the opening bracket.
     * @param tokenizer
     */
    protected void skipArrayValue(JSONTokenizer tokenizer)
    {
        skipComplexValue(tokenizer, TokenType.BRACKET_OPEN, TokenType.BRACKET_CLOSE);
    }

    /**
     * Skips either an object or an array
     *
     * @param tokenizer
     * @param open
     * @param close
     */
    private void skipComplexValue(JSONTokenizer tokenizer, TokenType open, TokenType close)
    {
        int level = 1;

        Token token;
        TokenType tokenType;
        while ((tokenType = (token = tokenizer.next()).type()) != TokenType.END)
        {
            if (tokenType == open)
            {
                level++;
            }
            else if (tokenType == close)
            {
                level--;
            }

            if (level == 0)
            {
                break;
            }
        }

        if (token.type() == TokenType.END)
        {
            throw new IllegalStateException("Unexpected end");
        }
    }

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
     * @param value
     * @return Class or <code>null</code>
     * @throws IllegalStateException if there is no class configured for this
     *             value and {@link #allowUndefined} is false.
     */
    protected Class getTypeHintFromTypeProperty(T value) throws IllegalStateException
    {
        Class cls = typeMap.get(value);
        if (cls == null)
        {
            if (!allowUndefined)
            {
                throw new IllegalStateException("There is no class mapped for the value \"" +
                    value + "\" of discriminator field " + value +
                    " and undefined values are not allowed");
            }
            cls = HashMap.class;
        }
        return cls;
    }

}
