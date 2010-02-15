package org.svenson.matcher;

/**
 * Matches if the preexisting type hint is assignable to a given type.
 * @author fforw at gmx dot de
 *
 */
public class SubtypeMatcher implements PathMatcher
{
    private Class cls;

    public SubtypeMatcher(Class cls)
    {
        if (cls == null)
        {
            throw new IllegalArgumentException("class can't be null");
        }
        this.cls = cls;
    }
    
    public Class getMatchClass()
    {
        return cls;
    }

    public boolean matches(String parsePath, Class typeHint)
    {
        return typeHint != null && cls.isAssignableFrom(typeHint);
    }

}
