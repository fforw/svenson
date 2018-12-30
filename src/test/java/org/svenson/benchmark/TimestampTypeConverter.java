package org.svenson.benchmark;

import org.svenson.converter.TypeConverter;

import java.util.Date;

class TimestampTypeConverter implements TypeConverter {
    @Override
    public Object fromJSON(Object in) {
        if(in instanceof Date){
            Date date = (Date) in;
            return date.getTime();
        }
        return null;
    }

    @Override
    public Object toJSON(Object in) {
        if(in instanceof Number){
            return new Date(((Number) in).longValue());
        }
        return null;
    }
}
