package org.svenson.tokenize;

import java.util.ArrayList;
import java.util.List;

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
    private JSONCharacterSource source;

    private boolean isDecimal;

    private char pushedBack;
    
    private List<Token> recordedTokens = new ArrayList<Token>();
    
    private boolean allowSingleQuotes = false;

    private boolean recording;

    private boolean tokenPushedBack;

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

        this.source = new StringJSONSource(json);
        //this.source = new InputStreamSource( new ByteArrayInputStream(json.getBytes()), json.length() );
        this.allowSingleQuotes = allowSingleQuotes;
    }

    /**
     * Constructs a new tokenizer instance for the given InputStream and length. If allowSingleQuotes
     * is <code>true</code>, the parser will also allow the JSON to contain quoted string that are
     * quoted with single quotes.
     *
     * @param json
     * @param allowSingleQuotes
     */
    public JSONTokenizer(JSONCharacterSource source, boolean allowSingleQuotes)
    {
        if (source == null)
        {
            throw new IllegalArgumentException("character source cannot be null.");
        }
        this.source = source;
        this.allowSingleQuotes = allowSingleQuotes;
    }
    
    public void destroy()
    {
        source.destroy();
    }

    public boolean isAllowSingleQuotes()
    {
        return allowSingleQuotes;
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
        int length = word.length();
        for (int i = 0; i < length ; i++)
        {
            if (nextChar() != word.charAt(i))
            {
                throw new JSONParseException("invalid keyword at index "+source.getIndex()+" (should be '" + word + "')");
            }
        }
    }

    /**
     * Returns the next token.
     * If there are no more tokens, a token with {@link TokenType#END} will be returned
     * @return
     */
    public final Token next()
    {
        if (tokenPushedBack)
        {
            Token token = recordedTokens.remove(0);
            if (recordedTokens.size() == 0)
            {
                tokenPushedBack = false;
            }
            return token;
        }

        skipWhiteSpace();

        if (source.getIndex() >= source.getLength() && pushedBack == 0)
        {
            return Token.getToken(TokenType.END);
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
                token = Token.getToken(TokenType.BRACKET_OPEN, "[");
                break;
            case ']':
                token = Token.getToken(TokenType.BRACKET_CLOSE, "]");
                break;
            case '{':
                token = Token.getToken(TokenType.BRACE_OPEN, "{");
                break;
            case '}':
                token = Token.getToken(TokenType.BRACE_CLOSE, "}");
                break;
            case ':':
                token = Token.getToken(TokenType.COLON, ":");
                break;
            case ',':
                token = Token.getToken(TokenType.COMMA, ",");
                break;
            case 't':
                ensureKeywordSuffix("rue");
                token = Token.getToken(TokenType.TRUE, Boolean.TRUE);
                break;
            case 'f':
                ensureKeywordSuffix("alse");
                token = Token.getToken(TokenType.FALSE, Boolean.FALSE);
                break;
            case 'n':
                ensureKeywordSuffix("ull");
                token = Token.getToken(TokenType.NULL);
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

                throw new JSONParseException("Unexpected character '" + c1 + "'");
            }
        }

        if (recording)
        {
            recordedTokens.add(token);
        }

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
        int index = recordedTokens.indexOf(oldToken);
        
        if (index < -1)
        {
            throw new IllegalStateException("Can't rollback to non-recorded token " + oldToken);
        }
        else if (index > 0)
        {
            recordedTokens = recordedTokens.subList(index, recordedTokens.size());
        }
        tokenPushedBack = true;
        recording = false;
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

        StringBuilder sb = new StringBuilder();
        sb.append(c1);
        
        char c;
        final int length = source.getLength();
        while (source.getIndex() < length)
        {
            c = nextChar();
            if (!isNumberCharacter(c))
            {
                pushBack(c);
                break;
            }
            sb.append(c);
        } 

        String number = sb.toString();

        if (isDecimal)
        {
            return parseDecimal(number);
        }
        else
        {
            try
            {
                long l = Long.parseLong(number );
                return Token.getToken(TokenType.INTEGER, l);
            }
            catch(NumberFormatException nfe)
            {
                // must be a integer greater than Long.MAX_VALUE
                // convert to decimal
                return parseDecimal(number);
            }
        }
    }

    private void pushBack(char c)
    {
        pushedBack = c;
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
            return Token.getToken(TokenType.DECIMAL, d);
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
        try
        {
            StringBuilder sb = new StringBuilder();
            boolean escape = false;
            char c;
            while ((c = nextChar()) != quoteChar || escape)
            {
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
                            int unicode = (hexValue(nextChar()) << 12) + (hexValue(nextChar()) << 8) + (hexValue(nextChar()) << 4) + hexValue(nextChar()); 
                            sb.append((char)unicode);
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
    
            return Token.getToken(TokenType.STRING, sb.toString());
        }
        catch(StringIndexOutOfBoundsException e)
        {
            throw new JSONParseException("Error parsing json",e);
        }
    }
    
    private final static int HEX_LETTER_OFFSET = 'A' - '9' - 1;
    
    static int hexValue(char c)
    {
        int n = c;
        if (n >= 'a')
        {
            n = n & ~32;
        }
        
        if ( (n >= '0' && n <= '9') || (n >= 'A' && n <= 'F'))
        {
            n -= '0';
            if (n > 9)
            {
                return n - HEX_LETTER_OFFSET;
            }
            else
            {
                return n;
            }
            
        }
        else
        {
            throw new NumberFormatException("Invalid hex character " + c);
        }
    }

    /**
     * Returns an info string containing the line and column of the current parsing position.
     * @return
     */
    private String info()
    {
        return "at character offset " + source.getIndex();
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
        if (pushedBack != 0)
        {
            char c = pushedBack;
            pushedBack = 0;
            return c;
        }
        else
        {
            return source.nextChar();
        }
    }

    /**
     * Skips all white-space at the current parsing position; will set the
     * position to the first non-whitespace character.
     */
    private void skipWhiteSpace()
    {
        final int length = source.getLength();
        while (source.getIndex() < length)
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
                    pushBack(c);
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

    public void startRecording()
    {
        recording = true;
    }

    public Token peekToken()
    {
        boolean wasRecording = recording;
        startRecording();
        Token token = next();
        pushBack(token);
        if (wasRecording)
        {
            startRecording();
        }
        return token;
    }
}
