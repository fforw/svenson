package org.svenson.benchmark;

public interface Deserializer {

     <T> T read(String json, Class<T> type);
}
