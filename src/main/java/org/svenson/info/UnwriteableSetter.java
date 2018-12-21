package org.svenson.info;

import org.svenson.JSONParseException;

public class UnwriteableSetter implements Setter {
    private final String name;

    public UnwriteableSetter(String name) {
        this.name = name;
    }

    public boolean isWriteable() {
        return false;
    }

    public void set(Object target, Object value) {
        throw new JSONParseException("Property '" + name + "' in " + target.getClass() + " is not writable.");
    }
}
