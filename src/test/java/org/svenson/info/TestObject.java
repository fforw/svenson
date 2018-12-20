package org.svenson.info;

import java.util.ArrayList;
import java.util.List;

public class TestObject extends BaseTestObject<String>  {

    private String override;
    private boolean overrideSwitcher;
    private final List<String> overridePlainValues = new ArrayList<String>();

    @Override
    public String getOverride() {
        return override;
    }

    @Override
    public void setOverride(String override) {
        this.override = override;
    }


    @Override
    public void setOverrideSwitcher(boolean value) {
        overrideSwitcher=value;
    }

    @Override
    public boolean isOverrideSwitcher() {
        return overrideSwitcher;
    }

    @Override
    public List<String> getOverridePlainValues() {
        return overridePlainValues;
    }

    @Override
    public void addOverridePlainValues(String value) {
        overridePlainValues.add(value);
    }
}
