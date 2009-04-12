package org.svenson;

public interface ObjectFactory<T>
{
    boolean supports(Class<T> cls);
    T create(Class<T> typeHint);
}
