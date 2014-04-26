package org.svenson.info;

import org.svenson.converter.TypeConverter;
import org.svenson.converter.TypeConverterRepository;

public interface JSONPropertyInfo
{

    boolean isIgnore();

    boolean isIgnoreIfNull();

    boolean isReadOnly();

    String getJavaPropertyName();

    boolean isLinkedProperty();

    String getLinkIdProperty();

    boolean isWriteable();


    boolean isReadable();


    Class<Object> getTypeHint();


    String getJsonName();


    Object getProperty(Object target);


    void setProperty(Object target, Object value);

    TypeConverter getTypeConverter(TypeConverterRepository typeConverterRepository);

    boolean canAdd();

    void add(Object target, Object value);

    Class<Object> getType();

    int getPriority();
}
