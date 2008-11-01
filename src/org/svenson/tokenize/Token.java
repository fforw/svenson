package org.svenson.tokenize;

import java.util.Arrays;

import org.svenson.JSONParseException;
import org.svenson.util.Util;

/**
 * A JSON parsing token that wraps the token type and the value of this type.
 *
 * @author shelmberger
 *
 */
public class Token
{
    /**
     * The next token in the token stream or <code>null</code>
     */
    Token next;
    /**
     * The previous token in the token stream or <code>null</code>
     */
    Token prev;

    /**
     * The value of this token.
     */
    private Object value;
    /**
     * The type of this token.
     */
    private TokenType type;

    /**
     * Constructs a token instance of the given token type with a <code>null</code> value
     * @param type  token type
     * @throws IllegalArgumentException if <code>null</code> is not a valid value for the given token type
     */
    public Token(TokenType type) throws IllegalArgumentException
    {
        this(type,null);
    }

    /**
     * Creates a new Token with given value and the given token type
     *
     * @param type      token type
     * @param value     token value
     * @throws IllegalArgumentException if the given type is <code>null</code> or the given value is not valid for the given type.
     */
    public Token(TokenType type, Object value) throws IllegalArgumentException
    {
        if (type == null)
        {
            throw new IllegalArgumentException("type must be given");
        }

        type.checkValue(value);

        this.type = type;
        this.value = value;
    }

    /**
     * Returns the value of the token
     * @return
     */
    public Object value()
    {
        return value;
    }

    /**
     * Returns the type of the token
     * @return
     */
    public TokenType type()
    {
        return type;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Token)
        {
            Token that = (Token)obj;

            return this.type == that.type && Util.equals(this.value, that.value);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return 37+ 17 * type.hashCode() + 17 * Util.safeHashcode(value);
    }

    /**
     * Returns true if the token is of the given type
     * @param type
     * @return
     */
    public boolean isType(TokenType type)
    {
        return this.type == type;
    }

    /**
     * Expects the given token to be of one of the given token types
     *
     * @param tokenizer
     * @param type
     * @return
     * @throws JSONParseException if the expectation is not fulfilled
     */
    public void expect(TokenType... types)
    {
        for (TokenType type : types)
        {
            if (this.type() == type)
            {
                return;
            }
        }
        throw new JSONParseException("Token "+this+" is not of one of the expected types "+Arrays.asList(types));
    }

    @Override
    public String toString()
    {
        return super.toString()+": "+type+" "+value;
    }

}
