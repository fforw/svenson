package org.svenson.tokenize;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.svenson.util.ExceptionWrapper;

public class InputStreamSource
    implements JSONCharacterSource
{
    private Reader reader;

    private int index;

    private boolean close;

    public InputStreamSource(InputStream inputStream, boolean close)
    {
        this.reader = new InputStreamReader(inputStream);
        this.close = close;
    }

    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    public int nextChar()
    {
        try
        {
            int result = reader.read();
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
