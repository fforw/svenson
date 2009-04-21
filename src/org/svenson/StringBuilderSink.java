package org.svenson;

public class StringBuilderSink implements JSONCharacterSink
{
    private StringBuilder sb = new StringBuilder();

    @Override
    final public void append(String s)
    {
        sb.append(s);
    }

    @Override
    final public void append(char c)
    {
        sb.append(c);
    }

    @Override
    final public void append(Object o)
    {
        sb.append(o);
    }
    
    final public String getContent()
    {
        return sb.toString();
    }

}
