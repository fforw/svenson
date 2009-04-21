package org.svenson;

public interface JSONCharacterSink
{
    void append(String token);

    void append(char c);

    void append(Object o);

}
