package org.svenson;

import org.svenson.tokenize.JSONTokenizer;
import org.svenson.tokenize.Token;
import org.svenson.tokenize.TokenType;
import org.svenson.util.TokenUtil;

/**
 * Abstract base class for Typemappers offering some helper methods.
 * 
 * @author fforw at gmx dot de
 *
 */
public abstract class AbstractTypeMapper implements TypeMapper
{

    protected Object getPropertyValueFromTokenStream(JSONTokenizer tokenizer, String propertyName, Token first)
    {
        Token token = getPropertyTokenFromTokenStream(tokenizer, propertyName, first);
        return token != null ? token.value() : null;
    }
    protected Token getPropertyTokenFromTokenStream(JSONTokenizer tokenizer, String propertyName, Token first)
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
                return firstValueToken;
            }
            else
            {
                if (firstValueToken.type() == TokenType.BRACE_OPEN)
                {
                    TokenUtil.skipObjectValue(tokenizer);
                }
                else if (firstValueToken.type() == TokenType.BRACKET_OPEN)
                {
                    TokenUtil.skipArrayValue(tokenizer);
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
