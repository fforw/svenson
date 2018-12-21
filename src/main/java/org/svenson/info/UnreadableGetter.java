package org.svenson.info;

import org.svenson.JSONParseException;

public class UnreadableGetter implements Getter {
    private final String name;

    public UnreadableGetter(String name) {
        this.name = name;
    }

    public boolean isReadable() {
        return false;
    }

    public Object get(Object target) {
            throw new JSONParseException("Property '" + name + "' in " + target.getClass() + " is not readable.");
    }
}
