package org.svenson.info;

public interface Setter {
    boolean isWriteable();
    void set(Object object, Object value);
}
