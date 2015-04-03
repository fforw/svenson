package org.svenson.tokenize;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.util.ExceptionWrapper;

public class InputStreamSource
    implements JSONCharacterSource
{
    private Reader reader;

    private int index;

    private boolean close;

    /**
     * Creates an input stream source from the given input stream which must deliver UTF-8 encoded data
     * 
     * @param inputStream   input stream
     * @param close         if <code>true</code>, the input stream is closed when reaching the end
     */
    public InputStreamSource(InputStream inputStream, boolean close)
    {
        try
        {
            this.reader = new BufferedReader( new InputStreamReader(inputStream, "UTF-8"));
        }
        catch (UnsupportedEncodingException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        this.close = close;
    }

    public int getIndex()
    {
        return index;
    }

    public int nextChar()
    {
        try
        {
            int result = reader.read();
            index++;
            return result;
        }
        catch (IOException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
    }

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
