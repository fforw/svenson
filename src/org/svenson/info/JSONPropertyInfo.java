package org.svenson.info;

import org.svenson.converter.TypeConverter;

public interface JSONPropertyInfo
{

    boolean isIgnore();

    boolean isIgnoreIfNull();

    boolean isReadOnly();

    String getJavaPropertyName();

    boolean isLinkedProperty();

    String getLinkIdProperty();

    Class<Object> getTypeOfProperty();


    boolean isWriteable();


    boolean isReadable();


    Class<Object> getTypeHint();


    String getJsonName();


    Object getProperty(Object target);


    void setProperty(Object target, Object value);

    TypeConverter getTypeConverter();

    boolean canAdd();

    Class<Object> getAdderType();

    void add(Object target, Object value);

    Class<Object> getType();

    int getPriority();
}
