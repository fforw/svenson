package org.svenson;

import java.util.HashMap;

import org.svenson.tokenize.JSONTokenizer;
import org.svenson.tokenize.Token;
import org.svenson.tokenize.TokenType;

/**
 * Maps parts of an JSON dataset to a set of configured types based on a field
 * of that part. The class is an example of how to implement a {@link TypeMapper}.
 * <p>
 * If you have the case of JSON dataset containing different possible types that
 * you can tell apart by the contents of a single field inside the types, this class
 * can help you.
 * <p>
 * Imagine you have a JSON dataset like this:
 * <pre><code>{
    "total_rows": 3,
    "offset": 0,
    "rows": [{ "type":"foo", "value":"aaa" },{ "type":"bar", "value":"bbb" },{ "value":"ccc","type":"bar"  }]
}
</code></pre>
You can now parse the objects in the rows array into Foo and Bar instances by setting up a parser like this:
 * <pre><code>
        JSONParser parser = new JSONParser();
        FieldValueBasedTypeMapper mapper = new FieldValueBasedTypeMapper();
        mapper.setParsePathInfo(".rows[]");
        mapper.addFieldValueMapping("foo", Foo.class);
        mapper.addFieldValueMapping("bar", Bar.class);
        parser.setTypeMapper(mapper);
</code></pre>
 *
 * There is also a test case for this class that does implement this example.
 *
 * @author shelmberger
 */
public class FieldValueBasedTypeMapper extends AbstractMapBasedTypeMapper<String>
{
    /**
     * Field whose value is used to tell one type from the other. (default "type")
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
