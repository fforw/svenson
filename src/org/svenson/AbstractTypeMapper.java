package org.svenson;

import org.svenson.tokenize.JSONTokenizer;
import org.svenson.tokenize.Token;
import org.svenson.tokenize.TokenType;

/**
 * Abstract base class for Typemappers offering some helper methods.
 * 
 * @author fforw at gmx dot de
 *
 */
public abstract class AbstractTypeMapper implements TypeMapper
{
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

    protected Object getPropertyValueFromTokenStream(JSONTokenizer tokenizer, String propertyName, Token first)
    {
        Token token = first;
        do
        {
            token.expect(TokenType.STRING);
            String currentPropertyName = (String) token.value();
            tokenizer.expectNext(TokenType.COLON);

            Token firstValueToken = tokenizer.next();

            if (currentPropertyName.equals(propertyName))
            {
                return firstValueToken.value();
            }
            else
            {
                if (firstValueToken.type() == TokenType.BRACE_OPEN)
                {
                    skipObjectValue(tokenizer);
                }
                else if (firstValueToken.type() == TokenType.BRACKET_OPEN)
                {
                    skipArrayValue(tokenizer);
                }

                Token next = tokenizer.expectNext(TokenType.COMMA, TokenType.BRACE_CLOSE);
                if (next.type() == TokenType.BRACE_CLOSE)
                {
                    return null;
                }
            }
        }
        while((token = tokenizer.next()).type() != TokenType.END);

        return null;
    }    
}
