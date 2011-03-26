package org.svenson.info;

import org.svenson.converter.TypeConverterRepository;

public interface ObjectSupport
{
    JSONClassInfo forClass(Class<?> cls);
}