package org.svenson.test;

import org.svenson.IgnoreOnInvalidProperties;

import java.util.Collection;

@IgnoreOnInvalidProperties
public class BeanIgnoringInvalidProperties {

    private Collection<String> stringCollection;

    private Long longValue;

    public Collection<String> getStringCollection() {
        return stringCollection;
    }

    public void setStringCollection(Collection<String> stringCollection) {
        this.stringCollection = stringCollection;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }
}
