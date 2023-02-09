package org.svenson.info;

import org.svenson.util.IntrospectionUtil;

public abstract class AbstractObjectSupport implements ObjectSupport
{
    protected static String propertyName(String name, int prefixLen)
    {
        return IntrospectionUtil.decapitalize(name.substring(prefixLen));
    }

}
