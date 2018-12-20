package org.svenson.info;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseTestObject<D> implements TestObjectGeneric<D> {
    private D generic;
    private boolean switcher;
    private String plain;
    private String overload;
    private final List<String> plainValues = new ArrayList<String>();

    public D getGeneric() {
        return generic;
    }

    public void setGeneric(D generic) {
        this.generic = generic;
    }

    public String getOverride() {
        throw new UnsupportedOperationException();
    }

    public void setOverride(String value) {
        throw new UnsupportedOperationException();
    }

    public boolean isOverrideSwitcher() {
        throw new UnsupportedOperationException();
    }

    public void setOverrideSwitcher(boolean value) {
        throw new UnsupportedOperationException();
    }

    public boolean isSwitcher() {
        return switcher;
    }

    public void setSwitcher(boolean switcher) {
        this.switcher = switcher;
    }

    public String getPlain() {
        return plain;
    }

    public void setPlain(String plain) {
        this.plain = plain;
    }

    public String getOverload() {
        return overload;
    }

    public void setOverload(String overload) {
        this.overload = overload;
    }

    public void setOverload(Map<?,?> anyVal) {
        throw new UnsupportedOperationException();
    }

    public static String getStaticProp() {
        throw new UnsupportedOperationException();
    }

    public static void setStaticProp(String plain) {
        throw new UnsupportedOperationException();
    }

    public List<String> getPlainValues() {
        return plainValues;
    }

    public void addPlainValues(String value){
        plainValues.add(value);
    }

    public List<String> getOverridePlainValues() {
        throw new UnsupportedOperationException();
    }

    public void addOverridePlainValues(String value){
        throw new UnsupportedOperationException();
    }

}
