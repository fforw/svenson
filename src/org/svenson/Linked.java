package org.svenson;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.lang.annotation.Retention;

/**
 * Annotates property methods as having another JSON name or as being either generally ignored or being ignored
 * when containing a <code>null</code> value.
 *
 * @author fforw at gmx dot de
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Target(value = ElementType.METHOD)
public @interface Linked
{
    /**
     * JSON property to use as replacement for the links value(s).
     * @return
     */
    String idProperty() default "_id";
}
