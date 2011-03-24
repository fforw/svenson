package org.svenson.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.svenson.JSONParseException;

public class PropertyInfo
{
    private Method getterMethod;

    private Method setterMethod;

    private boolean ignore, ignoreIfNull, readOnly, linkedProperty;
    
    private String javaPropertyName;

    private Class typeHint;
    
    public PropertyInfo(String javaPropertyName, Method getterMethod, Method setterMethod)
    {
        this.javaPropertyName = javaPropertyName;
        this.getterMethod = getterMethod;
        this.setterMethod = setterMethod;
    }


    public Method getSetterMethod()
    {
        return setterMethod;
    }


    public void setSetterMethod(Method setterMethod)
    {
        this.setterMethod = setterMethod;
    }


    public Method getGetterMethod()
    {
        return getterMethod;
    }


    public void setGetterMethod(Method getterMethod)
    {
        this.getterMethod = getterMethod;
    }


    public boolean isIgnore()
    {
        return ignore;
    }


    public void setIgnore(boolean ignore)
    {
        this.ignore = ignore;
    }


    public boolean isIgnoreIfNull()
    {
        return ignoreIfNull;
    }


    public void setIgnoreIfNull(boolean ignoreIfNull)
    {
        this.ignoreIfNull = ignoreIfNull;
    }


    public boolean isReadOnly()
    {
        return readOnly;
    }


    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }


    public String getJavaPropertyName()
    {
        return javaPropertyName;
    }


    public void setJavaPropertyName(String javaPropertyName)
    {
        this.javaPropertyName = javaPropertyName;
    }


    public boolean isLinkedProperty()
    {
        return linkedProperty;
    }


    public void setLinkedProperty(boolean linkedProperty)
    {
        this.linkedProperty = linkedProperty;
    }

    
    public Class getTypeOfProperty()
    {
        if (setterMethod != null)
        {
            return setterMethod.getParameterTypes()[0];
        }
        return null;
    }


    public boolean isWriteable()
    {
        return setterMethod != null;
    }


    public boolean isReadable()
    {
        return getterMethod != null;
    }
    
    
    public Class getTypeHint()
    {
        return typeHint;
    }


    public void setTypeHint(Class typeHint)
    {
        this.typeHint = typeHint;
    }


    public Object getProperty(Object target)
    {
        Method getterMethod = getGetterMethod();
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
    
    public void setProperty(Object target, Object value)
    {
        Method setterMethod = getSetterMethod();
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

    
    
    
}
