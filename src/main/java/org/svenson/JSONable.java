package org.svenson;

/**
 * Implemented by classes that want to control their own JSON representation. You can
 * implement this interface to hide data from the JSON view of it or to fine-tune
 * its JSON representation in general. See <a href="http://json.org">JSON
 * homepage</a> for details on JSON.
 *
 */
public interface JSONable
{
    String toJSON();
}
