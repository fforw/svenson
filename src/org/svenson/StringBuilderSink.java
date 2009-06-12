package org.svenson;

public class StringBuilderSink implements JSONCharacterSink
{
    private StringBuilder sb = new StringBuilder();

    final public void append(String s)
    {
        sb.append(s);
    }

    final public void append(char c)
    {
        sb.append(c);
    }

    final public void append(Object o)
    {
        sb.append(o);
    }
    
    final public String getContent()
    {
        return sb.toString();
    }

}
