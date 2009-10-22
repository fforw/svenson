package org.svenson;

public interface SinkAwareJSONifier extends JSONifier
{
    void writeToSink(JSONCharacterSink sink, Object o);
}
