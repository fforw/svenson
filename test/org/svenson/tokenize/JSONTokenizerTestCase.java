package org.svenson.tokenize;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.svenson.JSONParseException;
import org.svenson.JSONParser;
import org.svenson.tokenize.JSONTokenizer;
import org.svenson.tokenize.Token;
import org.svenson.tokenize.TokenType;

public class JSONTokenizerTestCase
{
    @Test
    public void testHex()
    {
        int i=0;
        for (char c = '0' ; c <= '9' ; c++)
        {
            assertThat(JSONTokenizer.hexValue(c), is(i++));
        }

        i=10;
        for (char c = 'a' ; c <= 'f' ; c++)
        {
            assertThat(JSONTokenizer.hexValue(c), is(i++));
        }

        i=10;
        for (char c = 'A' ; c <= 'F' ; c++)
        {
            assertThat(JSONTokenizer.hexValue(c), is(i++));
        }

//        ensureInvalidHex('\0', '0');
//        ensureInvalidHex((char) ('9' + 1), 'A');
//        ensureInvalidHex((char) ('F' + 1), 'a');
//        ensureInvalidHex((char) ('f' + 1), '\uffff');
        
    }

    private void ensureInvalidHex(char start, char end)
    {
        for (char c = start ; c < end ; c++)
        {
            try
            {
                JSONTokenizer.hexValue(c);
                Assert.fail("'"+ c + "' is not a valid hex character");
            }
            catch(NumberFormatException nfe)
            {
            }
        }
    }
    
    
    protected static Logger log = Logger.getLogger(JSONTokenizerTestCase.class);

    private List<Token> tokenize(String json)
    {
        List<Token> tokens = new ArrayList<Token>();

        JSONTokenizer tokenizer = new JSONTokenizer(json, true);
        Token token;
        while ( (token = tokenizer.next()).type() != TokenType.END)
        {
            tokens.add(token);
        }
        return tokens;
    }

    private Token createToken(TokenType type, Object value)
    {
        return Token.getToken(type,value);
    }

    private Token createToken(TokenType type)
    {
        if (type.isClassRestricted())
        {
            return Token.getToken(type, Integer.MIN_VALUE);
        }
        else
        {
            return Token.getToken(type, type.getValidContent());
        }
    }

    @Test
    public void thatTokenizingNumbersWorks()
    {
        assertThat(tokenize(" \n107"), is( Arrays.asList( createToken(TokenType.INTEGER, Long.valueOf(107)))));
        assertThat(tokenize("  -19 \r"), is( Arrays.asList( createToken(TokenType.INTEGER, Long.valueOf(-19)))));

        assertThat(tokenize("3.1415"), is( Arrays.asList( createToken(TokenType.DECIMAL, Double.valueOf(3.1415)))));
        assertThat(tokenize("10e5"), is( Arrays.asList( createToken(TokenType.DECIMAL, Double.valueOf(10e5)))));
        assertThat(tokenize("92233720368547758070"), is( Arrays.asList( createToken(TokenType.DECIMAL, Double.valueOf("92233720368547758070")))));

    }

    @Test
    public void thatTokenizingStringsWorks()
    {
        assertThat(tokenize("\"\""), is( Arrays.asList( createToken(TokenType.STRING, ""))));
        assertThat(tokenize("''"), is( Arrays.asList( createToken(TokenType.STRING, ""))));
        assertThat(tokenize("\"foo bar\""), is( Arrays.asList( createToken(TokenType.STRING, "foo bar"))));
        assertThat(tokenize("'foo bar'"), is( Arrays.asList( createToken(TokenType.STRING, "foo bar"))));
        assertThat(tokenize("\"\\\"\""), is( Arrays.asList( createToken(TokenType.STRING, "\""))));
        assertThat(tokenize("\"\\\\\\\"\""), is( Arrays.asList( createToken(TokenType.STRING, "\\\""))));
        assertThat(tokenize("\"\\r\\n\\f\\b\\/\\u0020\""), is( Arrays.asList( createToken(TokenType.STRING, "\r\n\f\b/ "))));
    }

    @Test
    public void thatObjectTokenizingWorks()
    {
        assertThat(tokenize(" {  \"foo\" :  \"bar\" , \"baz\"  : 42 } "), is( Arrays.asList(
            createToken(TokenType.BRACE_OPEN),
            createToken(TokenType.STRING, "foo"),
            createToken(TokenType.COLON),
            createToken(TokenType.STRING, "bar"),
            createToken(TokenType.COMMA),
            createToken(TokenType.STRING, "baz"),
            createToken(TokenType.COLON),
            createToken(TokenType.INTEGER, Long.valueOf(42)),
            createToken(TokenType.BRACE_CLOSE) )));

        assertThat(tokenize("{}"), is( Arrays.asList(
            createToken(TokenType.BRACE_OPEN),
            createToken(TokenType.BRACE_CLOSE) )));

    }

    @Test
    public void thatArrayTokenizingWorks()
    {
        assertThat(tokenize(" [  \"foo\" ,  1.2 ,  1    ,  true , false  ]  "), is( Arrays.asList(
            createToken(TokenType.BRACKET_OPEN),
            createToken(TokenType.STRING, "foo"),
            createToken(TokenType.COMMA),
            createToken(TokenType.DECIMAL, 1.2),
            createToken(TokenType.COMMA),
            createToken(TokenType.INTEGER, 1l),
            createToken(TokenType.COMMA),
            createToken(TokenType.TRUE, Boolean.TRUE),
            createToken(TokenType.COMMA),
            createToken(TokenType.FALSE, Boolean.FALSE),
            createToken(TokenType.BRACKET_CLOSE) )));

        assertThat(tokenize(" [ ] "), is( Arrays.asList(
            createToken(TokenType.BRACKET_OPEN),
            createToken(TokenType.BRACKET_CLOSE) )));
    }

    @Test(expected = JSONParseException.class)
    public void thatUnclosedQuotesDontWork()
    {
        tokenize("\"");
    }

    @Test(expected = JSONParseException.class)
    public void thatUnbalancedDoubleQuotesDontWork()
    {
        tokenize("\"'");
    }

    @Test(expected = JSONParseException.class)
    public void thatUnbalancedSingleQuotesDontWork()
    {
        tokenize("'\"");
    }

    @Test(expected = JSONParseException.class)
    public void thatUnknownKeywordDoesntWork()
    {
        // wrong keyword with right first char
        tokenize("foo");
    }

    @Test(expected = JSONParseException.class)
    public void thatUnknownKeywordDoesntWork2()
    {
        // wrong keyword with wrong first char
        tokenize("bar");
    }

    @Test(expected = JSONParseException.class)
    public void thatInvalidEscapeSequencesDontWork()
    {
        tokenize("\"\\e\"");
    }

    @Test()
    public void thatKeywordTokenizingWorks()
    {
        assertThat(tokenize("true"), is( Arrays.asList( createToken(TokenType.TRUE, Boolean.TRUE))));
        assertThat(tokenize("false"), is( Arrays.asList( createToken(TokenType.FALSE, Boolean.FALSE))));
        assertThat(tokenize("null"), is( Arrays.asList( createToken(TokenType.NULL))));
    }

    @Test
    public void thatPushBackWorks()
    {
        JSONTokenizer tokenizer = new JSONTokenizer("{\"foo\":[1,1.2,true,false,null]}", false);
        tokenizer.startRecording();
        Token token;
        while ( (token = tokenizer.next()).type() != TokenType.END)
        {
            tokenizer.pushBack(token);
            tokenizer.startRecording();
            Token token2 = tokenizer.next();

            assertThat(token, is(token2));
        }
    }

    @Test
    public void thatSingleQuotesAreNotAllowedByDefault()
    {
        JSONTokenizer t = new JSONTokenizer("[]", false);
        assertThat(t.isAllowSingleQuotes(), is(false));
    }

    @Test(expected = JSONParseException.class)
    public void thatSingleQuotesAreRejectedIfNotAllowed()
    {
        new JSONTokenizer("'foo'", false).next();
    }
    
    @Test
    public void testSimpleValueParsing()
    {
        JSONParser parser = JSONParser.defaultJSONParser();        
        assertThat((String)parser.parse("\"abc\u0020äöüÄÖÜß\""), is("abc äöüÄÖÜß"));
        assertThat((String)parser.parse("\"アカエラミノウミウシ\""), is("アカエラミノウミウシ"));
        assertThat((Long)parser.parse("123"), is(123L));
        assertThat((Double)parser.parse("123.4"), is(123.4));
        assertThat(parser.parse("null"), is(nullValue()));
        assertThat((Boolean)parser.parse("true"), is(true));
        assertThat((Boolean)parser.parse("false"), is(false));
    }
}
