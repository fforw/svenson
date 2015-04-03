package org.svenson;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates constructor parameters to receive JSON object values. There can be at most one constructor annotated
 * with @JSONParameter annotations on a type. That type will only be constructed with that constructor and the
 * not setters or adders will be called.
 * 
 * @author fforw at gmx dot de
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Target(value = ElementType.PARAMETER)
public @interface JSONParameter
{
    /**
     * JSON property name to use for this constructor parameter.
     * @return
     */
    String value();
}
