package org.svenson.info;

import org.svenson.converter.TypeConverter;

public interface JSONPropertyInfo
{

    boolean isIgnore();


    void setIgnore(boolean ignore);


    boolean isIgnoreIfNull();


    void setIgnoreIfNull(boolean ignoreIfNull);


    boolean isReadOnly();


    void setReadOnly(boolean readOnly);


    String getJavaPropertyName();


    void setJavaPropertyName(String javaPropertyName);


    boolean isLinkedProperty();


    String getLinkIdProperty();


    void setLinkIdProperty(String linkIdProperty);


    Class<?> getTypeOfProperty();


    boolean isWriteable();


    boolean isReadable();


    Class<?> getTypeHint();


    void setTypeHint(Class<?> typeHint);


    String getJsonName();


    void setJsonName(String jsonName);


    Object getProperty(Object target);


    void setProperty(Object target, Object value);


    void setTypeConverter(TypeConverter typeConverter);


    TypeConverter getTypeConverter();


    boolean canAdd();


    Class<Object> getAdderType();


    void add(Object target, Object value);


    Class<Object> getType();
}
