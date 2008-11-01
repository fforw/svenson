package org.svenson;

import java.util.HashMap;

import org.svenson.tokenize.JSONTokenizer;
import org.svenson.tokenize.Token;
import org.svenson.tokenize.TokenType;

/**
 * Uses a discriminator field to convert the documents of a query into
 * configured types. For example if you have documents like
 * <code>{ "type" : "foo" , ... }</code> and
 * <code>{ "type" : "bar" , ... }</code>, you can use the following code to
 * convert them into the appropriate types:
 *
 * <pre><code>
 * JSONParser parser = new JSONParser();
 * FieldBasedDocumentMapper mapper = new FieldBasedDocumentMapper();
 * mapper.addFieldValueMapping(&quot;foo&quot;, Foo.class);
 * mapper.addFieldValueMapping(&quot;bar&quot;, Bar.class);
 * parser.setTypeMapper(mapper);
 *
 * ViewResult&lt;Map&gt; result = db.listDocuments(null, parser);
 * </code></pre>
 *
 * See the TestCase for this class for a full example.
 *
 * @author shelmberger
 */
public class FieldValueBasedDocumentMapper extends AbstractMapBasedTypeMapper<String>
{
    /**
     * Field whose value is used to tell one type from the other.
     */
    String discriminatorField = "type";

    /**
     * Parse path info the mapping is applied to.
     */
    private String parsePathInfo;

    /**
     * Mapps the given field value to the given type.
     *
     * @param value field value
     * @param cls type
     */
    public void addFieldValueMapping(String value, Class cls)
    {
        typeMap.put(value, cls);
    }

    /**
     * Sets the parse path info at which the type discrimination is applied.
     * Default is <code>".rows[].value"</code> which is the parse path info
     * for a couchdb view result transformation. This method is only present for
     * complete configurability of the {@link FieldValueBasedDocumentMapper},
     * use only if you know what you're doing.
     *
     * @param parsePathInfo
     */
    public void setParsePathInfo(String parsePathInfo)
    {
        this.parsePathInfo = parsePathInfo;
    }

    /**
     * Sets the property used to discriminate between the different document
     * types
     *
     * @param discriminatorField
     */
    public void setDiscriminatorField(String discriminatorField)
    {
        this.discriminatorField = discriminatorField;
    }

    public Class getTypeHint(JSONTokenizer tokenizer, String parsePathInfo, Class typeHint)
    {
        if (this.parsePathInfo == null)
        {
            throw new IllegalStateException("parse path info not configured.");
        }

        if (this.parsePathInfo.equals(parsePathInfo))
        {
            Token first = tokenizer.next();

            if (first.type() == TokenType.END)
            {
                throw new IllegalStateException("Unexpected end");
            }
            try
            {
                Token token = first;
                do
                {
                    token.expect(TokenType.STRING);
                    String propertyName = (String) token.value();
                    tokenizer.expectNext(TokenType.COLON);

                    Token firstValueToken = tokenizer.next();

                    if (propertyName.equals(discriminatorField))
                    {
                        firstValueToken.expect(TokenType.STRING);
                        String fieldValue = (String) firstValueToken.value();
                        return getTypeHintFromTypeProperty(fieldValue);
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
                            return HashMap.class;
                        }
                    }
                } while ((token = tokenizer.next()).type() != TokenType.END);
                return null;
            }
            finally
            {
                tokenizer.pushBack(first);
            }
        }
        return typeHint;
    }
}
