package org.svenson.info;

public interface Getter {
    boolean isReadable();
    Object get(Object o);
}
