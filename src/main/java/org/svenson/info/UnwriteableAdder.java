package org.svenson.info;

import org.svenson.JSONParseException;

public class UnwriteableAdder implements Adder {
    private final String name;

    public UnwriteableAdder(String name) {
        this.name = name;
    }

    public boolean isWriteable() {
        return false;
    }

    public void add(Object target, Object value) {
        throw new JSONParseException("Property '" + name + "' in " + target.getClass() + " is not writable.");
    }
}
