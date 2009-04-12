package org.svenson.tokenize;

import org.svenson.JSONParseException;

/**
 * String implementation for the {@link JSONCharacterSource} interface for full in-memory
 * JSON parsing.
 * 
 * @author shelmberger
 */
public class StringJSONSource
    implements JSONCharacterSource
{
    private String json;

    private int index;

    public StringJSONSource(String json)
    {
        this.json = json;
    }

    @Override
    public int getLength()
    {
        return json.length();
    }

    @Override
    public char nextChar()
    {
        try
        {
            return json.charAt(index++);
        }
        catch(StringIndexOutOfBoundsException e)
        {
            throw new JSONParseException("Invalid json position "+(index-1));
        }
    }

    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    public void destroy()
    {
        // nothing to do
    }

}
