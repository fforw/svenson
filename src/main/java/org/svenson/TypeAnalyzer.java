package org.svenson;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.svenson.info.ClassInfoHolder;
import org.svenson.info.JSONClassInfo;
import org.svenson.info.ObjectSupport;

/**
 * Entry point for the internal type knowledge.
 * @author fforw at gmx dot de
 *
 */
public class TypeAnalyzer
{
    protected static ConcurrentMap<Class<?>, ClassInfoHolder> holders;

    static
    {
        clear();
    }
  
    /**
     * Returns a class info for the given {@link ObjectSupport} and type.
     * 
     * @param objectSupport
     * @param cls
     * @return
     */
    public static JSONClassInfo getClassInfo(ObjectSupport objectSupport, Class<?> cls)
    {
        if (cls == null)
        {
            return null;
        }

        ClassInfoHolder holder = new ClassInfoHolder(objectSupport, cls);
        ClassInfoHolder existing = holders.putIfAbsent(cls, holder);
        if (existing != null)
        {
            holder = existing;
        }
        
        return holder.getClassInfo();
    }

    /**
     * Releases/clears all class infos.
     */
    public static void clear()
    {
        holders = new ConcurrentHashMap<Class<?>, ClassInfoHolder>();
    }
}
