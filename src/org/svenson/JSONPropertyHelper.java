package org.svenson;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import org.apache.commons.beanutils.PropertyUtils;

public class JSONPropertyHelper
{
    public static String getPropertyNameFromAnnotation(Object target, String value)
    {
        for (PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(target.getClass()))
        {
            JSONProperty jsonProperty = null;
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();

            if (readMethod != null)
            {
                jsonProperty = readMethod.getAnnotation(JSONProperty.class);
            }
            if (jsonProperty == null && writeMethod != null)
            {
                jsonProperty = writeMethod.getAnnotation(JSONProperty.class);
            }

            if (jsonProperty != null && jsonProperty.value().equals(value))
            {
                return pd.getName();
            }
        }
        return value;
    }
}
