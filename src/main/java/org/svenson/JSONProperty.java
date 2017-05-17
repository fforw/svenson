package org.svenson;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.lang.annotation.Retention;

/**
 * Annotates property methods to influence how they're converted to and from JSON.
 * 
 * You can mark properties as having another JSON name or as being either generally ignored or being ignored
 * when containing a <code>null</code> value.
 *
 * @author fforw at gmx dot de
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Target(value = ElementType.METHOD)
public @interface JSONProperty
{
    /**
     * JSON property name to use for this property. if not given, the java property name is used.
     * @return  JSON property name
     */
    String value() default "";

    /**
     * if <code>true</code>, always ignore the property.
     * @return <code>true</code> if the property is always ignored
     */
    boolean ignore() default false;

    /**
     * if <code>true</code>, ignore the property if it contains a <code>null</code> value.
     * @return  <code>true</code> if the property should be ignored in case it is <code>null</code>.
     */
    boolean ignoreIfNull() default false;
    
    /**
     * Marks a property as read-only which means the JSON generator will generate a property for 
     * such a method but the JSON parser should ignore it.
     *  
     * @return  <code>true</code> if the property is read-only.
     */
    boolean readOnly() default false;

    /**
     * Returns the priority for this property. Larger priorities appear before smaller.
     *
     * @return  priority value
     */
    int priority() default 0;
}
