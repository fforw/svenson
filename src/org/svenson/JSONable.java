package org.svenson;

/**
 * Implemented by classes that want to control their own JSON representation. You can
 * implement this interface to hide data from the JSON view of it or to fine-tune
 * its JSON representation in general. See <a href="http://json.org">JSON
 * homepage</a> for details on JSON.
 *
 * @author Sven Helmberger ( sven dot helmberger at gmx dot de )
 * @version $Id: JSONable.java,v 1.1 2005/12/30 15:54:02 fforw Exp $
 * @license BSD revised
 */
public interface JSONable
{
    String toJSON();
}
