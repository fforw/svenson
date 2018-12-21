package org.svenson.info;

import org.svenson.converter.TypeConverter;
import org.svenson.converter.TypeConverterRepository;

/**
 * Encapsulates svenson's knowledge about one property inside a class. An instance
 * of this is created for every readable or writeable property and for add* Methods.
 *
 * @author fforw at gmx dot de
 */
public final class JavaObjectPropertyInfo implements JSONPropertyInfo {

    private final Getter getterMethod;

    private final Setter setterMethod;

    private final Adder adderMethod;

    private final boolean ignore, ignoreIfNull, readOnly;

    private final String javaPropertyName;

    private final Class<?> typeHint;

    private final String jsonName;

    private final String linkIdProperty;

    private final int priority;

    private final Class<?> type;
    private final PropertyTypeConverterResolver converterResolver;

    public JavaObjectPropertyInfo(String name, Getter getter, Setter setter, Adder adder, Class<?> type, Class<?> typeHint, boolean ignore, boolean ignoreIfNull, boolean readOnly, String jsonName, String linkIdProperty, int priority, PropertyTypeConverterResolver converterResolver) {
        this.javaPropertyName = name;
        this.getterMethod = getter;
        this.setterMethod = setter;
        this.ignore = ignore;
        this.ignoreIfNull = ignoreIfNull;
        this.readOnly = readOnly;
        this.typeHint = typeHint;
        this.jsonName = jsonName;
        this.linkIdProperty = linkIdProperty;
        this.priority = priority;
        this.adderMethod = adder;
        this.type = type;

        this.converterResolver = converterResolver;
    }


    public boolean isIgnore() {
        return ignore;
    }

    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#isIgnoreIfNull()
     */
    public boolean isIgnoreIfNull() {
        return ignoreIfNull;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#isReadOnly()
     */
    public boolean isReadOnly() {
        return readOnly;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getJavaPropertyName()
     */
    public String getJavaPropertyName() {
        return javaPropertyName;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#isLinkedProperty()
     */
    public boolean isLinkedProperty() {
        return linkIdProperty != null;
    }

    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getLinkIdProperty()
     */
    public String getLinkIdProperty() {
        return linkIdProperty;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#isWriteable()
     */
    public boolean isWriteable() {
        return setterMethod.isWriteable();
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#isReadable()
     */
    public boolean isReadable() {
        return getterMethod.isReadable();
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getTypeHint()
     */
    public Class<Object> getTypeHint() {
        return (Class<Object>) typeHint;
    }


    public String getJsonName() {
        return jsonName;
    }


    public Object getProperty(Object target) {
        return getterMethod.get(target);
    }

    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#setProperty(java.lang.Object, java.lang.Object)
     */
    public void setProperty(Object target, Object value) {
        setterMethod.set(target, value);
    }


    @Override
    public String toString() {
        return super.toString() + " adderMethod=" + adderMethod + ", getterMethod=" + getterMethod +
                ", ignore=" + ignore + ", ignoreIfNull=" + ignoreIfNull + ", javaPropertyName=" +
                javaPropertyName + ", jsonName=" + jsonName + ", linkIdProperty=" + linkIdProperty +
                ", readOnly=" + readOnly + ", setterMethod=" + setterMethod + ", typeHint=" + typeHint;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getTypeConverter()
     */
    public TypeConverter getTypeConverter(TypeConverterRepository typeConverterRepository) {
        return converterResolver.resolve(typeConverterRepository);
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#canAdd()
     */
    public boolean canAdd() {
        return adderMethod.isWriteable();
    }

    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#add(java.lang.Object, java.lang.Object)
     */
    public void add(Object target, Object value) {
        adderMethod.add(target, value);
    }

    @SuppressWarnings("unchecked")
    public Class<Object> getType() {
        return (Class<Object>) type;
    }


    public int getPriority() {
        return priority;
    }
}
