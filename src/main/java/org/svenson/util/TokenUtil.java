package org.svenson.util;

import org.svenson.tokenize.JSONTokenizer;
import org.svenson.tokenize.Token;
import org.svenson.tokenize.TokenType;

public class TokenUtil
{
    /**
     * Fowards the given tokenizer to skips an object value (including all sub objects and arrays) if the
     * tokenizer is on the position <em>after</em> the opening brace.
     * @param tokenizer     tokenizer
     */
    public static void skipObjectValue(JSONTokenizer tokenizer)
    {
        skipComplexValue(tokenizer, TokenType.BRACE_OPEN, TokenType.BRACE_CLOSE);
    }

    /**
     * Fowards the given tokenizer to skips an array value (including all sub objects and arrays) if the
     * tokenizer is on the position <em>after</em> the opening bracket.
     * @param tokenizer     tokenizer
     */
    public static void skipArrayValue(JSONTokenizer tokenizer)
    {
        skipComplexValue(tokenizer, TokenType.BRACKET_OPEN, TokenType.BRACKET_CLOSE);
    }

    /**
     * Skips either an object or an array
     *
     * @param tokenizer     tokenizer
     * @param open          TokenType opening the complex value to skip
     * @param close         TokenType closing the complex value to skip
     */
    private static void skipComplexValue(JSONTokenizer tokenizer, TokenType open, TokenType close)
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
}
