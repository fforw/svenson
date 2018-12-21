package org.svenson.info;

import org.svenson.converter.TypeConverter;
import org.svenson.converter.TypeConverterRepository;

public interface PropertyTypeConverterResolver {
    TypeConverter resolve(TypeConverterRepository typeConverterRepository);
}
