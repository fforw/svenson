package org.svenson;

import java.io.IOException;
import java.io.Writer;

import org.svenson.util.ExceptionWrapper;

public class WriterSink implements JSONCharacterSink
{
    private Writer writer;
    
    public WriterSink(Writer writer)
    {
        this.writer = writer;
    }

    @Override
    public void append(String token)
    {
        try
        {
            writer.write(token);
        }
        catch (IOException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
    }

    @Override
    public void append(char c)
    {
        try
        {
            writer.write(c);
        }
        catch (IOException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
    }

    @Override
    public void append(Object o)
    {
        append(o.toString());
    }
    
}
