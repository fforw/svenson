package org.svenson;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.lang.annotation.Retention;

/**
 * Annotates a bean, collection, array and map property method to not include the full child object but only representative property values, usually IDs of some kind.
 * Properties with {@link JSONReference}s are ignored on parse back. You can use the JSON reference value to identify child values.
 * <p>
 * This annotation was added to support the CouchDB linked document feature in jcouchdb but might also make sense in other environments. 
 *
 * @author fforw at gmx dot de
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Target(value = ElementType.METHOD)
public @interface JSONReference
{
    /**
     * JSON property to use as replacement for the links value(s). Default is "_id", the CouchDB id field.
     * @return
     */
    String idProperty() default "_id";

    /**
     * Generic Reference identifier.
     * @return
     */
    String value() default "";
}
