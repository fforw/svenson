package org.svenson.tokenize;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.JSONParseException;

/**
 * JSON Tokenizer. Parses the json text into {@link Token}s. Tokens can be
 * pushed back into the Tokenizer for resetting the token stream to a previous
 * position. The Tokenizer is stateful and not thread-safe.
 *
 * @author fforw at gmx dot de
 *
 */
public class JSONTokenizer
{
    private static Logger log = LoggerFactory.getLogger(JSONTokenizer.class);
    
    private JSONCharacterSource source;

    private boolean isDecimal;

    private char pushedBack;
    
    private List<Token> recordedTokens = new ArrayList<Token>();
    
    private boolean allowSingleQuotes = false;

    private boolean recording;

    private boolean tokenPushedBack;

    private boolean reachedEndOfJSON;

    private int pushBackIndex;

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
     * @param source                character source to use
     * @param allowSingleQuotes     if <code>true</code>, single quotes ('\'') is allowed as quoting character, too
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
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return true;
            default:
                return false;
        }
    }

    /**
     * Ensures that the token stream stand on the given identifier suffix. This
     * is used to e.g. check if "rue" is really following an initial 't'.
     * @param word
     */
    private void ensureKeywordSuffix(String word)
    {
        String suffix = word.substring(1);
        int length = suffix.length();
        for (int i = 0; i < length ; i++)
        {
            if (nextChar() != suffix.charAt(i))
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
            Token token;
            
            if (recording)
            {
                token = recordedTokens.get(pushBackIndex++);
                
                if (recordedTokens.size() == pushBackIndex)
                {
                    tokenPushedBack = false;
                }
            }
            else
            {
                token = recordedTokens.remove(0);
                if (recordedTokens.size() == 0)
                {
                    tokenPushedBack = false;
                }
            }

            log.trace("token = {}", token);

            return token;
        }

        skipWhiteSpace();

        if (reachedEndOfJSON && pushedBack == 0)
        {
            return Token.getToken(TokenType.END);
        }

        isDecimal = false;

        Token token ;

        int c1 = nextChar();
        switch((char)c1)
        {
            case '"':
            {
                token = parseString((char)c1);
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
                ensureKeywordSuffix("true");
                token = Token.getToken(TokenType.TRUE, Boolean.TRUE);
                break;
            case 'f':
                ensureKeywordSuffix("false");
                token = Token.getToken(TokenType.FALSE, Boolean.FALSE);
                break;
            case 'n':
                ensureKeywordSuffix("null");
                token = Token.getToken(TokenType.NULL);
                break;
            default:
            {
                if ( isNumberCharacter((char)c1))
                {
                    token = parseNumber((char)c1);
                    break;
                }

                if (c1 == '\'' && allowSingleQuotes)
                {
                    token = parseString((char)c1);
                    break;
                }

                throw new JSONParseException("Unexpected character '" + (char)c1 + "'");
            }
        }

        if (recording)
        {
            recordedTokens.add(token);
        }
        
        log.trace("token = {}", token);

        return token;
    }

    /**
     * Pushes back the given Token. This will reset the tokenizer to the index before the
     * token was encountered and the next {@link #next()} call will return the same token again.
     *
     * @param  oldToken     token to push back
     */
    public void pushBack(Token oldToken)
    {
        int index = recordedTokens.indexOf(oldToken);
        
        if (index < 0)
        {
            throw new IllegalStateException("Can't rollback to non-recorded token " + oldToken);
        }
        else if (index > 0)
        {
            recordedTokens = recordedTokens.subList(index, recordedTokens.size());
        }
        tokenPushedBack = true;
        recording = false;
        pushBackIndex = 0;
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
        
        int c;
        while ((c = nextChar()) > -1)
        {
            if (!isNumberCharacter((char)c))
            {
                pushBack((char)c);
                break;
            }
            sb.append((char)c);
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
            BigDecimal d =  new BigDecimal(number);
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
        StringBuilder sb = new StringBuilder();
        boolean escape = false;
        int c;
        while ((c = nextChar()) >= 0)
        {
            if (c == quoteChar && !escape)
            {
                return Token.getToken(TokenType.STRING, sb.toString());
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
                switch((char)c)
                {
                    case '\'':
                    case '"':
                    case '/':
                        sb.append((char)c);
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
                        int unicode = (hexValue((char)nextChar()) << 12) + (hexValue((char)nextChar()) << 8) + (hexValue((char)nextChar()) << 4) + hexValue((char)nextChar()); 
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
                sb.append((char)c);
            }
        }
        throw new JSONParseException("Unclosed quotes");
    }
    
    private final static int HEX_LETTER_OFFSET = 'A' - '9' - 1;
    
    public static int hexValue(char c)
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
    private int nextChar()
    {
        if (pushedBack != 0)
        {
            char c = pushedBack;
            pushedBack = 0;
            return c;
        }
        else
        {
            int c = source.nextChar();
            
            if (c < 0)
            {
                reachedEndOfJSON = true;
            }
            
            return c;
        }
    }

    /**
     * Skips all white-space at the current parsing position; will set the
     * position to the first non-whitespace character.
     */
    private void skipWhiteSpace()
    {
        int c;
        while ((c = nextChar()) != -1)
        {
            switch(c)
            {
                case ' ':
                case '\r':
                case '\b':
                case '\n':
                case '\t':
                    break;
                default:
                    pushBack((char)c);
                    return;
            }
        } 
    }

    /**
     * Expects the next token to be of one of the given token types
     *
     * @param types varg list of types to expect
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
