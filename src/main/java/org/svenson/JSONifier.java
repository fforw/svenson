package org.svenson;

/**
 * Provides a method that converts another object into a JSON dump. See <a
 * href="http://json.org">JSON homepage</a> for details on JSON.
 *
 * @author fforw at gmx dot de
 *
 * @see JSON#registerJSONifier(Class, JSONifier)
 */
public interface JSONifier
{
    String toJSON(Object o);
}
