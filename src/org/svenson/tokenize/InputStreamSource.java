package org.svenson.tokenize;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.svenson.JSONParseException;
import org.svenson.util.ExceptionWrapper;

public class InputStreamSource
    implements JSONCharacterSource
{

    private Reader reader;

    private int index;
    private int length;

    private boolean close;

    public InputStreamSource(InputStream inputStream, int length, boolean close)
    {
        this.reader = new InputStreamReader(inputStream);
        this.length = length;
        this.close = close;
    }

    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    public int getLength()
    {
        return length;
    }

    @Override
    public char nextChar()
    {
        try
        {
            int result = reader.read();
            if (result < 0)
            {
                throw new JSONParseException("Invalid json position " + index);
            }
            index++;
            return (char)result;
        }
        catch (IOException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
    }

    @Override
    public void destroy()
    {
        if (close)
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
                throw ExceptionWrapper.wrap(e);
            }
        }
    }
}
