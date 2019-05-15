package org.svenson;

import java.lang.annotation.*;

/**
 * Target to type, inheritable. When one or more properties are not in expected type, ignore setting them on parsing.
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Target(value = ElementType.TYPE)
public @interface IgnoreOnInvalidProperties {

}
