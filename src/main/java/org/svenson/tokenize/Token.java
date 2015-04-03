package org.svenson.tokenize;

import java.util.Arrays;

import org.svenson.JSONParseException;
import org.svenson.util.Util;

/**
 * A JSON parsing token that wraps the token type and the value of this type.
 *
 * @author fforw at gmx dot de
 *
 */
public class Token
{
    /**
     * The value of this token.
     */
    private Object value;
    /**
     * The type of this token.
     */
    private TokenType type;

    /**
     * Contains a singleton instance for given token type order position for every token type
     * that has a fixed value, <code>null</code> for all dynamic (class-restricted) token types.
     */
    private final static Token[] SINGLETON_TOKENS = new Token[TokenType.values().length];
    static
    {
        for (TokenType type : TokenType.values())
        {
            if (!type.isClassRestricted())
            {
                SINGLETON_TOKENS[type.ordinal()] = new Token(type, type.getValidContent());
            }
        }
    }

    public static Token getToken(TokenType type)
    {
        return getToken(type, null);
    }
    
    public static Token getToken(TokenType type, Object value)
    {
        if (type.isClassRestricted())
        {
            return new Token(type, value);
        }
        else
        {
            return SINGLETON_TOKENS[type.ordinal()];
        }
    }
    
    /**
     * Creates a new Token with given value and the given token type
     *
     * @param type      token type
     * @param value     token value
     * @throws IllegalArgumentException if the given type is <code>null</code> or the given value is not valid for the given type.
     */
    private Token(TokenType type, Object value) throws IllegalArgumentException
    {
        if (type == null)
        {
            throw new IllegalArgumentException("type must be given");
        }

        //type.checkValue(value);

        this.type = type;
        this.value = value;
    }

    /**
     * Returns the value of the token
     * @return
     */
    public final Object value()
    {
        return value;
    }

    /**
     * Returns the type of the token
     * @return
     */
    public final TokenType type()
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
        return 37 + 17 * type.hashCode() + 17 * Util.safeHashcode(value);
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
     * @param types vararg list of possible types  
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
