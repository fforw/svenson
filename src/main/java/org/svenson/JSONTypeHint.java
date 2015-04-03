package org.svenson;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a map or collection getter/setter with the type expected within the collection.
 *
 * @author fforw at gmx dot de
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Documented
public @interface JSONTypeHint
{
    Class value();
}
