package org.svenson.tokenize;

import org.svenson.JSONParseException;

/**
 * JSON Tokenizer. Parses the json text into {@link Token}s. Tokens can be
 * pushed back into the Tokenizer for resetting the token stream to a previous
 * position. The Tokenizer is stateful and not thread-safe.
 *
 * @author shelmberger
 *
 */
public class JSONTokenizer
{
    private char[] json;
    private int index;
    private boolean isDecimal;

    private Token headToken = new Token(TokenType.NULL, null);
    private Token curToken = headToken;

    private boolean allowSingleQuotes = false;

    /**
     * Constructs a new tokenizer instance for the given JSON string.
     *
     * @param json
     */
    public JSONTokenizer(String json)
    {
        this(json, false);
    }

    public boolean isAllowSingleQuotes()
    {
        return allowSingleQuotes;
    }

    /**
     * Constructs a new tokenizer instance for the given JSON string. If allowSingleQuotes
     * is <code>true</code>, the parser will also allow the JSON to contain quoted string that are
     * quoted with single quotes.
     *
     * @param json
     * @param allowSingleQuotes
     */
    public JSONTokenizer(String json, boolean allowSingleQuotes)
    {
        if (json == null)
        {
            throw new IllegalArgumentException("json string cannot be null.");
        }

        this.json = json.toCharArray();
        this.allowSingleQuotes = allowSingleQuotes;
    }

    /**
     * Returns <code>true</code> if the given character is a number character.
     */
    private boolean isNumberCharacter(char c )
    {
        switch(c)
        {
            case '.':
            case '-':
            case '+':
            case 'E':
            case 'e':
                isDecimal = true;
                return true;
            default:
                return c >= '0' && c <= '9';
        }
    }

    /**
     * Ensures that the token stream stand on the given identifier suffix. This
     * is used to e.g. check if "rue" is really following an initial 't'.
     * @param word
     */
    private void ensureKeywordSuffix(String word)
    {
        if (index + word.length() > json.length || !new String(json,index,word.length()).equals(word) )
        {
            throw new JSONParseException("invalid keyword "+info()+" (should be "+word+")");
        }
        index += word.length();
    }
    /**
     * Returns the next token.
     * If there are no more tokens, a token with {@link TokenType#END} will be returned
     * @return
     */
    public Token next()
    {
        if (curToken != null && curToken.next != null)
        {
            curToken = curToken.next;
            return curToken;
        }

        skipWhiteSpace();

        if (index >= json.length)
        {
            return new Token(TokenType.END);
        }

        isDecimal = false;

        Token token ;

        char c1 = nextChar();
        switch(c1)
        {
            case '"':
            {
                token = parseString(c1);
                break;
            }
            case '[':
                token = new Token(TokenType.BRACKET_OPEN, "[");
                break;
            case ']':
                token = new Token(TokenType.BRACKET_CLOSE, "]");
                break;
            case '{':
                token = new Token(TokenType.BRACE_OPEN, "{");
                break;
            case '}':
                token = new Token(TokenType.BRACE_CLOSE, "}");
                break;
            case ':':
                token = new Token(TokenType.COLON, ":");
                break;
            case ',':
                token = new Token(TokenType.COMMA, ",");
                break;
            case 't':
                ensureKeywordSuffix("rue");
                token = new Token(TokenType.TRUE, Boolean.TRUE);
                break;
            case 'f':
                ensureKeywordSuffix("alse");
                token = new Token(TokenType.FALSE, Boolean.FALSE);
                break;
            case 'n':
                ensureKeywordSuffix("ull");
                token = new Token(TokenType.NULL);
                break;
            default:
            {
                if ( isNumberCharacter(c1))
                {
                    token = parseNumber(c1);
                    break;
                }

                if (c1 == '\'' && allowSingleQuotes)
                {
                    token = parseString(c1);
                    break;
                }

                throw new JSONParseException("Unexpected character '"+c1+"'");
            }
        }

        curToken.next = token;
        token.prev = curToken;
        curToken = token;

        return token;
    }

    /**
     * Pushes back the given Token. This will reset the tokenizer to the index before the
     * token was encountered and the next {@link #next()} call will return the same token again.
     *
     * @param   t
     */
    public void pushBack(Token oldToken)
    {
        if (oldToken.prev == null)
        {
            throw new IllegalStateException("oldToken.prev cannot be null");
        }

        curToken = oldToken.prev;
    }

    /**
     * Resets the tokenizer to the first parsing position.
     */
    public void reset()
    {
        curToken = headToken;
    }

    /**
     * Parses the current parsing stream position into a token with the type {@link TokenType#INTEGER} or {@link TokenType#DECIMAL}.
     * @param c1
     * @return
     */
    private Token parseNumber(char c1)
    {
        if ( c1 == '-')
        {
            isDecimal = false;
        }

        int start = index-1;
        while( index < json.length)
        {
            char c = nextChar();
            if (!isNumberCharacter(c))
            {
                back();
                break;
            }
        }

        String number = new String(json, start, index-start);

        if (isDecimal)
        {
            return parseDecimal(number);
        }
        else
        {
            try
            {
                long l = Long.parseLong(number );
                return new Token(TokenType.INTEGER, l);
            }
            catch(NumberFormatException nfe)
            {
                // must be a integer greater than Long.MAX_VALUE
                // convert to decimal
                return parseDecimal(number);
            }
        }
    }

    /**
     * Parses the given string into a token with the type {@link TokenType#DECIMAL}.
     * @param number
     * @return
     */
    private Token parseDecimal(String number)
    {
        try
        {
            double d = Double.parseDouble(number);
            return new Token(TokenType.DECIMAL, d);
        }
        catch(NumberFormatException nfe)
        {
            throw new JSONParseException("Error parsing double "+number);
        }
    }

    /**
     * Parses the current position into a quoted string quoted by the given
     * quote char.
     * @param quoteChar character that starts and ends this quoted string. must be a single or a double quote.
     * @return
     */
    private Token parseString(char quoteChar)
    {
        StringBuilder sb = new StringBuilder();
        boolean escape = false;
        boolean endOfString = false;
        while (index < json.length)
        {
            char c = nextChar();

            if ((endOfString = (c == quoteChar && !escape)))
            {
                break;
            }

            if (c == '\\')
            {
                if (escape)
                {
                    sb.append('\\');
                }
                escape = !escape;
            }
            else if (escape)
            {
                switch(c)
                {
                    case '\'':
                    case '"':
                    case '/':
                        sb.append(c);
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        if (index + 4 > json.length)
                        {
                            throw new JSONParseException("unexpected end of unicode sequence");
                        }
                        char unicode = (char) Integer.parseInt(new String(json,index,4), 16);
                        index += 4;
                        sb.append(unicode);
                        break;
                    default:
                        throw new JSONParseException("Illegal escape character "+c+" / "+Integer.toHexString(c));
                }
                escape = false;
            }
            else
            {
                if (Character.isISOControl(c))
                {
                    throw new JSONParseException("Illegal control character 0x"+Integer.toHexString(c));
                }
                sb.append(c);
            }
        }

        if (endOfString)
        {
            return new Token(TokenType.STRING, sb.toString());
        }
        else
        {
            throw new JSONParseException("Unexpected end of string, missing quote "+info());
        }
    }

    /**
     * Returns an info string containing the line and column of the current parsing position.
     * @return
     */
    private String info()
    {
        int column = 1, line = 1;

        boolean isCR,wasCR = false;

        for (int i=0; i < json.length; i++)
        {
            char c = json[i];
            isCR = isCR(c);
            if (wasCR && !isCR)
            {
                line++;
                column = 0;
            }
            else
            {
                column++;
            }
            wasCR = isCR;
        }


        return "at line "+line+", column "+column;
    }

    /**
     * Returns <code>true</code> if the given character is either a carriage return or linefeed
     * @param c
     * @return
     */
    private boolean isCR(char c)
    {
        return c == '\r' || c == '\n';
    }

    /**
     * Returns the next character.
     * @return
     */
    private char nextChar()
    {
        return json[index++];
    }

    /**
     * Goes back one char.
     */
    private void back()
    {
        index--;
    }

    /**
     * Skips all white-space at the current parsing position; will set the
     * position to the first non-whitespace character.
     */
    private void skipWhiteSpace()
    {
        while (index < json.length)
        {
            char c = nextChar();

            switch(c)
            {
                case ' ':
                case '\r':
                case '\b':
                case '\n':
                case '\t':
                    break;
                default:
                    back();
                    return;
            }
        }
    }

    /**
     * Expects the next token to be of one of the given token types
     *
     * @param tokenizer
     * @param types
     * @return
     * @throws JSONParseException if the expectation is not fulfilled
     */
    public Token expectNext(TokenType... types)
    {
        Token t = next();
        t.expect(types);
        return t;
    }
}
