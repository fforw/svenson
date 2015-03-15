package org.svenson;

import org.svenson.info.ConstructorInfo;
import org.svenson.info.ParameterInfo;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Set;

/**
 * Implements dynamic properties to store JSON property values being used
 * in @JSONParameter annotated constructors.
 *
 * @param <T>
 */
public class DelayedConstructor<T>
    implements DynamicProperties
{
    private final ConstructorInfo info;
    private final Object[] args;

    public DelayedConstructor(ConstructorInfo info)
    {
        this.info = info;
        Class<?>[] parameterTypes = info.getConstructor().getParameterTypes();
        this.args = new Object[parameterTypes.length];
    }

    public T construct()
    {
        try
        {
            Constructor<T> constructor = (Constructor<T>) info.getConstructor();
            return constructor.newInstance(args);
        }
        catch (Exception e)
        {
            throw new SvensonRuntimeException("Error constructing " + this, e);
        }
    }

    public static <E> E unwrap(E t)
    {
        if (t instanceof DelayedConstructor)
        {
            return (E)((DelayedConstructor)t).construct();
        }
        return t;
    }

    public void setProperty(String name, Object value)
    {
        ParameterInfo paramInfo = info.getParameterInfo(name);
        if (paramInfo == null)
        {
            throw new IllegalArgumentException("No constructor parameter for JSON property '" + name + "' in " + info.getConstructor().getDeclaringClass());
        }
        int index = paramInfo.getIndex();

        if (value != null)
        {
            Class<?>[] parameterTypes = info.getConstructor().getParameterTypes();
            Class<?> targetClass = parameterTypes[index];
            if (!targetClass.equals(value.getClass()))
            {
                value = JSONParser.convertValueTo(value, targetClass, null);
            }
        }

        args[index] = unwrap(value);
    }

    public Object getProperty(String name)
    {
        ParameterInfo paramInfo = info.getParameterInfo(name);
        if (paramInfo == null)
        {
            throw new IllegalArgumentException("No constructor parameter for JSON property '" + name + "' in " + info.getConstructor().getDeclaringClass());
        }
        int index = paramInfo.getIndex();
        return args[index];
    }

    public Set<String> propertyNames()
    {
        return info.getJSONPropertyNames();
    }

    public boolean hasProperty(String name)
    {
        return propertyNames().contains(name);
    }

    public Object removeProperty(String name)
    {
        ParameterInfo paramInfo = info.getParameterInfo(name);
        if (paramInfo == null)
        {
            throw new IllegalArgumentException("No constructor parameter for JSON property '" + name + "' in " + info.getConstructor().getDeclaringClass());
        }
        int index = paramInfo.getIndex();
        Object old = args[index];
        args[index] = null;
        return old;
    }

    public ConstructorInfo getConstructorInfo()
    {
        return info;
    }


    @Override
    public String toString()
    {
        return super.toString() + ": "
            + info.getConstructor().getDeclaringClass().getName() + "(" + Arrays.toString(args) + ")"
            ;
    }
}
