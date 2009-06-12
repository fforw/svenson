package org.svenson.converter;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that marks a Bean setter or getter method as being subject to conversion when generating or parsing JSON.
 * 
 * @see TypeConverter
 * @author shelmberger
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Target(value = ElementType.METHOD)
public @interface JSONConverter
{
    String name() default "";
    Class<? extends TypeConverter> type() default TypeConverter.class;
}
