package org.svenson.info;

import org.svenson.JSONParameter;
import org.svenson.JSONTypeHint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ConstructorInfo
{
    private final Constructor constructor;
    private final Map<String,ParameterInfo> indexMap;

    public ConstructorInfo(Constructor constructor)
    {
        this.constructor = constructor;

        Map<String, ParameterInfo> map = new HashMap<String, ParameterInfo>();
        Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();
        for (int i = 0; i < parameterAnnotations.length; i++)
        {
            Annotation[] annotations = parameterAnnotations[i];

            String name = null;
            int index = -1;
            Class typeHint = null;

            for (Annotation annotation : annotations)
            {
                if (annotation instanceof JSONParameter)
                {
                    name = ((JSONParameter)annotation).value();
                    index = i;
                }
                if (annotation instanceof JSONTypeHint)
                {
                    typeHint = ((JSONTypeHint)annotation).value();
                }
            }

            if (name != null)
            {
                map.put(name, new ParameterInfo(i, typeHint));
            }
        }

        indexMap = Collections.unmodifiableMap(map);
    }

    public ParameterInfo getParameterInfo(String name)
    {
        return indexMap.get(name);
    }

    public Constructor<?> getConstructor()
    {
        return constructor;
    }

    public Set<String> getJSONPropertyNames()
    {
        return indexMap.keySet();
    }
}
