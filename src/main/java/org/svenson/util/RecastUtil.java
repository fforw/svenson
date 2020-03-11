package org.svenson.util;

import org.apache.commons.beanutils.ConvertUtils;
import org.svenson.DelayedConstructor;
import org.svenson.SvensonRuntimeException;
import org.svenson.TypeAnalyzer;
import org.svenson.info.ConstructorInfo;
import org.svenson.info.JSONClassInfo;
import org.svenson.info.JSONPropertyInfo;
import org.svenson.info.JavaObjectSupport;
import org.svenson.info.ObjectSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecastUtil
{

    private final static JSONBeanUtil util = JSONBeanUtil.defaultUtil();

    public static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    private final static ObjectSupport objectSupport = new JavaObjectSupport();


    /**
     * Returns a capacity that is sufficient to keep the map from being resized as long as it grows no
     * larger than expectedSize and the load factor is ≥ its default (0.75).
     */
    static int capacity(int expectedSize)
    {
        if (expectedSize < 3)
        {
            return expectedSize + 1;
        }
        if (expectedSize < MAX_POWER_OF_TWO)
        {
            // This is the calculation used in JDK8 to resize when a putAll
            // happens; it seems to be the most conservative calculation we
            // can make.  0.75 is the default load factor.
            return (int) ((float) expectedSize / 0.75F + 1.0F);
        }
        return Integer.MAX_VALUE; // any large value
    }


    /**
     * Recreates the given JSON object graph as a Java POJO of the same shape using svenson JSON conventions.
     * <p>
     * This can be a useful alternative to hyper-complicated TypeMappers. Performance should be slightly above
     * JSONifying and reparsing and memory use below that.
     * </p>
     * <p>
     * For performance critical code, you might want to get clever type mappers instead.
     * </p>
     *
     * @param cls   Class to recast the JSON object graph into
     * @param input JSON object graph out of maps/lists
     * @param <T>   Target type to cast to
     *
     * @return recast object
     */
    public static <T> T recast(Class<T> cls, Object input)
    {
        return recast(cls, input, objectSupport);

    }


    public static <T> T recast(Class<T> cls, Object input, ObjectSupport objectSupport)
    {
        if (input == null || cls == null || cls.equals(Object.class) || cls.equals(input.getClass()))
        {
            return (T) input;
        }

        if (input instanceof Boolean || input instanceof Number || input instanceof String)
        {
            if (cls.isInstance(input))
            {
                return (T) input;
            }
            else
            {
                return (T) ConvertUtils.convert(input, cls);
            }
        }

        final JSONClassInfo classInfo = TypeAnalyzer.getClassInfo(objectSupport, cls);

        try
        {
            final Object instance;

            final ConstructorInfo constructorInfo = classInfo.getConstructorInfo();
            final boolean isDelayedContruction = constructorInfo != null;
            if (isDelayedContruction)
            {
                instance = new DelayedConstructor<Object>(constructorInfo);
            }
            else
            {
                instance = cls.newInstance();
            }

            for (JSONPropertyInfo info : classInfo.getPropertyInfos())
            {
                if (!info.isIgnore() && ( info.isWriteable() || isDelayedContruction))
                {


                    final Object value = util.getProperty(input, info.getJsonName());

                    final Object recastValue;
                    final boolean isList = List.class.isAssignableFrom(info.getType());
                    final boolean isSet = Set.class.isAssignableFrom(info.getType());
                    if (isList || isSet)
                    {
                        final Collection<Object> out = isSet ? new HashSet<Object>() : new ArrayList<Object>();

                        final Class<Object> typeHint = info.getTypeHint();

                        for (Object elem : (Collection) value)
                        {
                            out.add(
                                recast(typeHint, elem, objectSupport)
                            );
                        }
                        recastValue = out;

                    }
                    else if (Map.class.isAssignableFrom(info.getType()))
                    {
                        final Map<String, Object> inputMap = (Map<String, Object>) value;
                        final Map<String, Object> out = new HashMap<String, Object>(capacity(inputMap.size()));

                        final Class<Object> typeHint = info.getTypeHint();
                        for (Map.Entry<String, Object> e : inputMap.entrySet())
                        {
                            out.put(
                                e.getKey(),
                                recast(
                                    typeHint,
                                    e.getValue(),
                                    objectSupport
                                )
                            );
                        }

                        recastValue = out;

                    }
                    else
                    {
                        recastValue = recast(
                            info.getType(),
                            value,
                            objectSupport
                        );
                    }
                    util.setProperty(instance, info.getJsonName(), recastValue);
                }
            }

            if (isDelayedContruction)
            {
                return (T)((DelayedConstructor)instance).construct();
            }
            else
            {
                return (T)instance;
            }
        }
        catch (InstantiationException e)
        {
            throw new SvensonRuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new SvensonRuntimeException(e);
        }
    }
}
