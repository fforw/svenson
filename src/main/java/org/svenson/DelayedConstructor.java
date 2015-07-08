package org.svenson;

import org.svenson.info.ConstructorInfo;
import org.svenson.info.ParameterInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
    private final int wildCardArgsIndex;

    public DelayedConstructor(ConstructorInfo info)
    {
        this.info = info;
        Class<?>[] parameterTypes = info.getConstructor().getParameterTypes();
        this.args = new Object[parameterTypes.length];

        wildCardArgsIndex = info.getWildCardArgsIndex();
        if (wildCardArgsIndex >= 0)
        {
            args[wildCardArgsIndex] = new HashMap<String,Object>();
        }
    }

    public T construct()
    {
        try
        {
            Constructor<T> constructor = (Constructor<T>) info.getConstructor();
            return constructor.newInstance(args);
        }
        catch (InstantiationException e)
        {
            throw new SvensonRuntimeException("Error constructing instance of " + info.getConstructor().getDeclaringClass().getName(), e);
        }
        catch (IllegalAccessException e)
        {
            throw new SvensonRuntimeException("Error constructing instance of " + info.getConstructor().getDeclaringClass().getName(), e);
        }
        catch (InvocationTargetException e)
        {
            throw new SvensonRuntimeException("Error constructing instance of " + info.getConstructor().getDeclaringClass().getName(), e.getTargetException());
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
            if (wildCardArgsIndex >= 0)
            {
                wildCardMap().put(name, value);
                return;
            }
            else
            {
                throw new IllegalArgumentException("No constructor parameter for JSON property '" + name + "' in " + info.getConstructor().getDeclaringClass());
            }
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

    private Map<String, Object> wildCardMap()
    {
        Object map = args[wildCardArgsIndex];
        if (map instanceof Map)
        {
            return (Map<String, Object>) map;
        }
        else
        {
            throw new SvensonRuntimeException("No map");
        }
    }

    public Object getProperty(String name)
    {
        ParameterInfo paramInfo = info.getParameterInfo(name);
        if (paramInfo == null)
        {
            if (wildCardArgsIndex >= 0)
            {
                return wildCardMap().get(name);
            }
            throw new IllegalArgumentException("No constructor parameter for JSON property '" + name + "' in " + info.getConstructor().getDeclaringClass());
        }
        int index = paramInfo.getIndex();
        return args[index];
    }

    public Set<String> propertyNames()
    {

        Set<String> jsonPropertyNames = info.getJSONPropertyNames();
        if (wildCardArgsIndex < 0)
        {
            // no wildcards at all -> json JSON properties
            return jsonPropertyNames;
        }
        else
        {
            Set<String> wildCardKeys = wildCardMap().keySet();
            if (jsonPropertyNames.size() == 0)
            {
                // no json properties -> return wildCardKeys (potentially empty)
                return wildCardKeys;
            }

            if (wildCardKeys.size() == 0)
            {
                // no wildcards -> json properties names
                return jsonPropertyNames;
            }
            // merge json and wildcard properties
            HashSet<String> set = new HashSet<String>(jsonPropertyNames);
            set.addAll(wildCardKeys);
            return set;
        }
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
            if (wildCardArgsIndex >= 0)
            {
                return wildCardMap().remove(name);
            }

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


    public int getWildCardArgsIndex()
    {
        return wildCardArgsIndex;
    }

    @Override
    public String toString()
    {
        return super.toString() + ": "
            + info.getConstructor().getDeclaringClass().getName() + "(" + Arrays.toString(args) + ")"
            ;
    }
}
