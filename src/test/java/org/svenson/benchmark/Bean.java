package org.svenson.benchmark;

import com.google.gson.annotations.JsonAdapter;
import org.svenson.JSONProperty;
import org.svenson.converter.JSONConverter;

import java.util.Date;
import java.util.List;

public class Bean {
    private String value;
    private List<String> values;
    private List<Bean> beans;
    private Bean inner;

    @JsonAdapter(TimestampAdapter.class)
    private Date date;

    @JSONProperty(ignoreIfNull = true)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @JSONProperty(ignoreIfNull = true)
    public List<String> getValues() {
        return values;
    }

    @JSONProperty(ignoreIfNull = true)
    public List<Bean> getBeans() {
        return beans;
    }

    public void setBeans(List<Bean> beans) {
        this.beans = beans;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @JSONProperty(ignoreIfNull = true)
    public Bean getInner() {
        return inner;
    }

    public void setInner(Bean inner) {
        this.inner = inner;
    }

    @JSONProperty(ignoreIfNull = true)
    @JSONConverter(type = TimestampTypeConverter.class)
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
