package org.svenson.test;

import org.svenson.IgnoreOnInvalidProperties;

import java.util.Collection;

@IgnoreOnInvalidProperties
public class BeanIgnoringInvalidProperties {

    private Collection<String> stringArray;

    private Long longValue;

    public Collection<String> getStringArray() {
        return stringArray;
    }

    public void setStringArray(Collection<String> stringArray) {
        this.stringArray = stringArray;
    }

    public Long getLongValue() {
        return longValue;
    }

    public void setLongValue(Long longValue) {
        this.longValue = longValue;
    }
}
