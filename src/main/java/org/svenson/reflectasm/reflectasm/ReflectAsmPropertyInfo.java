package org.svenson.reflectasm.reflectasm;

import com.esotericsoftware.reflectasm.MethodAccess;
import org.svenson.JSONParseException;
import org.svenson.SvensonRuntimeException;
import org.svenson.converter.TypeConverter;
import org.svenson.converter.TypeConverterRepository;
import org.svenson.info.JSONPropertyInfo;

import java.lang.reflect.Method;

/**
 * Created by sven on 26.04.14.
 */
class ReflectAsmPropertyInfo implements JSONPropertyInfo
{
    private MethodAccess methodAccess;

    // temporary fields
    private Method getterMethod, setterMethod, adderMethod;

    private int getterMethodIndex = -1;

    private int setterMethodIndex = -1;

    private int adderMethodIndex = -1;

    private boolean ignore, ignoreIfNull, readOnly;

    private String javaPropertyName;

    private Class<?> typeHint;

    private String jsonName;

    private String linkIdProperty;

    private int priority = 0;
    private String converterName;
    private Class<? extends TypeConverter> converterType;

    public ReflectAsmPropertyInfo(String javaPropertyName, MethodAccess methodAccess)
    {
        this.javaPropertyName = javaPropertyName;
        this.methodAccess = methodAccess;
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
     * @see org.svenson.info.JSONPropertyInfo#isWriteable()
     */
    public boolean isWriteable()
    {
        return setterMethodIndex >= 0;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#isReadable()
     */
    public boolean isReadable()
    {
        return getterMethodIndex >= 0;
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

    public void setGetterMethodIndex(int getterMethodIndex)
    {
        this.getterMethodIndex = getterMethodIndex;
    }

    public void setSetterMethodIndex(int setterMethodIndex)
    {
        this.setterMethodIndex = setterMethodIndex;
    }

    public void setAdderMethodIndex(int adderMethodIndex)
    {
        this.adderMethodIndex = adderMethodIndex;
    }

    public MethodAccess getMethodAccess()
    {
        return methodAccess;
    }

    public int getGetterMethodIndex()
    {
        return getterMethodIndex;
    }

    public int getSetterMethodIndex()
    {
        return setterMethodIndex;
    }

    public int getAdderMethodIndex()
    {
        return adderMethodIndex;
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
        if (getterMethodIndex < 0)
        {
            throw new JSONParseException("Property '" + getJavaPropertyName() + "' in " + target.getClass() + " is not readable.");
        }

        try
        {
            return methodAccess.invoke(target, getterMethodIndex);
        }
        catch (Throwable t)
        {
            throw new SvensonRuntimeException("Error getting value from target " + target + " using index " + getterMethodIndex, t);
        }
    }

    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#setProperty(java.lang.Object, java.lang.Object)
     */
    public void setProperty(Object target, Object value)
    {
        if (setterMethodIndex < 0)
        {
            throw new JSONParseException("Property '" + getJavaPropertyName() + "' in " + target.getClass() + " is not writable.");
        }

        try
        {
            methodAccess.invoke(target, setterMethodIndex, value);
        }
        catch (Throwable t)
        {
            throw new SvensonRuntimeException("Error setting value " + value + " on target " + target + " using " + setterMethodIndex, t);
        }
    }


    @Override
    public String toString()
    {
        return super.toString() + " adderMethod=" + adderMethodIndex + ", getterMethod=" + getterMethodIndex +
            ", ignore=" + ignore + ", ignoreIfNull=" + ignoreIfNull + ", javaPropertyName=" +
            javaPropertyName + ", jsonName=" + jsonName + ", linkIdProperty=" + linkIdProperty +
            ", readOnly=" + readOnly + ", setterMethod=" + setterMethodIndex + ", typeHint=" + typeHint ;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getTypeConverter()
     */
    public TypeConverter getTypeConverter(TypeConverterRepository typeConverterRepository)
    {
        if (typeConverterRepository != null)
        {
            if (converterName != null)
            {
                return typeConverterRepository.getConverterById(converterName);
            }
            else if (converterType != null)
            {
                return typeConverterRepository.getConverterByType(converterType);
            }
        }
        return null;
    }


    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#canAdd()
     */
    public boolean canAdd()
    {
        return adderMethodIndex >= 0;
    }

    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#add(java.lang.Object, java.lang.Object)
     */
    public void add(Object target, Object value)
    {
        try
        {
            methodAccess.invoke(target, adderMethodIndex, value);
        }
        catch (Throwable t)
        {
            throw new SvensonRuntimeException("Error adding value " + value + " to target " + target + " using " + adderMethodIndex, t);
        }
    }

    /* (non-Javadoc)
     * @see org.svenson.info.JSONPropertyInfo#getType()
     */
    @SuppressWarnings("unchecked")
    public Class<Object> getType()
    {
        Class<Object> type;
        if (getterMethodIndex >= 0)
        {
            type = methodAccess.getReturnTypes()[getterMethodIndex];
        }
        else if (setterMethodIndex >= 0)
        {
            type = methodAccess.getParameterTypes()[setterMethodIndex][0];
        }
        else if (adderMethodIndex >= 0)
        {
            type = methodAccess.getParameterTypes()[adderMethodIndex][0];
        }
        else
        {
            throw new IllegalStateException("No method!?");
        }
        return type;
    }


    public void setPriority(int order)
    {
        this.priority = order;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setConverterName(String converterName)
    {
        this.converterName = converterName;
    }

    public void setConverterType(Class<? extends TypeConverter> converterType)
    {
        this.converterType = converterType;
    }

    public Method getGetterMethod()
    {
        return getterMethod;
    }

    public void setGetterMethod(Method getterMethod)
    {
        this.getterMethod = getterMethod;
    }

    public Method getSetterMethod()
    {
        return setterMethod;
    }

    public void setSetterMethod(Method setterMethod)
    {
        this.setterMethod = setterMethod;
    }

    public Method getAdderMethod()
    {
        return adderMethod;
    }

    public void setAdderMethod(Method adderMethod)
    {
        this.adderMethod = adderMethod;
    }

    public void clearMethodReferences()
    {
        getterMethod = null;
        setterMethod = null;
        adderMethod = null;
    }
}
