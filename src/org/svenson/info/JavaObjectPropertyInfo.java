package org.svenson.info;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.svenson.JSONParseException;
import org.svenson.converter.TypeConverter;
import org.svenson.util.ExceptionWrapper;

/**
 * Encapsulates svenson's knowledge about one property inside a class. An instance
 * of this is created for every readable or writeable property and for add* Methods. 
 * 
 * @author fforw at gmx dot de
 *
 */
class JavaObjectPropertyInfo implements JSONPropertyInfo
{
    private Method getterMethod;

    private Method setterMethod;

    private Method adderMethod;

    private boolean ignore, ignoreIfNull, readOnly;
    
    private String javaPropertyName;

    private Class<?> typeHint;

    private String jsonName;
    
    private String linkIdProperty;

    private TypeConverter typeConverter;
    
    public JavaObjectPropertyInfo(String javaPropertyName, Method getterMethod, Method setterMethod)
    {
        this.javaPropertyName = javaPropertyName;
        this.getterMethod = getterMethod;
        this.setterMethod = setterMethod;
    }


    Method getSetterMethod()
    {
        return setterMethod;
    }


    public void setSetterMethod(Method setterMethod)
    {
        this.setterMethod = setterMethod;
    }


    Method getGetterMethod()
    {
        return getterMethod;
    }


    public void setGetterMethod(Method getterMethod)
    {
        this.getterMethod = getterMethod;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#isIgnore()
     */
    public boolean isIgnore()
    {
        return ignore;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#setIgnore(boolean)
     */
    public void setIgnore(boolean ignore)
    {
        this.ignore = ignore;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#isIgnoreIfNull()
     */
    public boolean isIgnoreIfNull()
    {
        return ignoreIfNull;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#setIgnoreIfNull(boolean)
     */
    public void setIgnoreIfNull(boolean ignoreIfNull)
    {
        this.ignoreIfNull = ignoreIfNull;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#isReadOnly()
     */
    public boolean isReadOnly()
    {
        return readOnly;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#setReadOnly(boolean)
     */
    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getJavaPropertyName()
     */
    public String getJavaPropertyName()
    {
        return javaPropertyName;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#setJavaPropertyName(java.lang.String)
     */
    public void setJavaPropertyName(String javaPropertyName)
    {
        this.javaPropertyName = javaPropertyName;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#isLinkedProperty()
     */
    public boolean isLinkedProperty()
    {
        return linkIdProperty != null;
    }

    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getLinkIdProperty()
     */
    public String getLinkIdProperty()
    {
        return linkIdProperty;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#setLinkIdProperty(java.lang.String)
     */
    public void setLinkIdProperty(String linkIdProperty)
    {
        this.linkIdProperty = linkIdProperty;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getTypeOfProperty()
     */
    @SuppressWarnings("unchecked")
    public Class<Object> getTypeOfProperty()
    {
        if (setterMethod != null)
        {
            return (Class<Object>) setterMethod.getParameterTypes()[0];
        }
        return null;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#isWriteable()
     */
    public boolean isWriteable()
    {
        return setterMethod != null;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#isReadable()
     */
    public boolean isReadable()
    {
        return getterMethod != null;
    }
    
    
    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getTypeHint()
     */
    public Class<Object> getTypeHint()
    {
        return (Class<Object>) typeHint;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#setTypeHint(java.lang.Class)
     */
    public void setTypeHint(Class<?> typeHint)
    {
        this.typeHint = typeHint;
    }
    
    Method getAdderMethod()
    {
        return adderMethod;
    }

    public void setAdderMethod(Method adderMethod)
    {
        this.adderMethod = adderMethod;
    }

    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getJsonName()
     */
    public String getJsonName()
    {
        return jsonName;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#setJsonName(java.lang.String)
     */
    public void setJsonName(String jsonName)
    {
        this.jsonName = jsonName;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getProperty(java.lang.Object)
     */
    public Object getProperty(Object target)
    {
        if (getterMethod == null)
        {
            throw new JSONParseException("Property '" + getJavaPropertyName() + "' in " + target.getClass() + " is not readable.");
        }
        
        try
        {
            return getterMethod.invoke(target);
        }
        catch (IllegalAccessException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (InvocationTargetException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#setProperty(java.lang.Object, java.lang.Object)
     */
    public void setProperty(Object target, Object value)
    {
        if (setterMethod == null)
        {
            throw new JSONParseException("Property '" + getJavaPropertyName() + "' in " + target.getClass() + " is not writable.");
        }

        try
        {
            setterMethod.invoke(target, value);
        }
        catch (IllegalArgumentException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (IllegalAccessException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (InvocationTargetException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
    }


    @Override
    public String toString()
    {
        return super.toString() + " adderMethod=" + adderMethod + ", getterMethod=" + getterMethod +
            ", ignore=" + ignore + ", ignoreIfNull=" + ignoreIfNull + ", javaPropertyName=" +
            javaPropertyName + ", jsonName=" + jsonName + ", linkIdProperty=" + linkIdProperty +
            ", readOnly=" + readOnly + ", setterMethod=" + setterMethod + ", typeHint=" + typeHint ;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#setTypeConverter(org.svenson.converter.TypeConverter)
     */
    public void setTypeConverter(TypeConverter typeConverter)
    {
        this.typeConverter = typeConverter;
    }
    
    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getTypeConverter()
     */
    public TypeConverter getTypeConverter()
    {
        return typeConverter;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#canAdd()
     */
    public boolean canAdd()
    {
        return adderMethod != null;
    }
    
    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getAdderType()
     */
    public Class<Object> getAdderType()
    {
        Class<Object> adder = (Class<Object>)adderMethod.getParameterTypes()[0];
        return adder;
    }

    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#add(java.lang.Object, java.lang.Object)
     */
    public void add(Object target, Object value)
    {
        try
        {
            adderMethod.invoke(target, value);
        }
        catch (IllegalArgumentException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (IllegalAccessException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (InvocationTargetException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
    }
    
    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getType()
     */
    @SuppressWarnings("unchecked")
    public Class<Object> getType()
    {
        Class<Object> type;
        if (getterMethod != null)
        {
            type = (Class<Object>) getterMethod.getReturnType();
        }
        else if (setterMethod != null)
        {
            type = (Class<Object>) setterMethod.getParameterTypes()[0];
        }
        else if (adderMethod != null)
        {
            type = (Class<Object>) adderMethod.getParameterTypes()[0];
        }
        else
        {
            throw new IllegalStateException("No method!?");
        }
        return type;
    }
}
