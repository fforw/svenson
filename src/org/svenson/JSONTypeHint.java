package org.svenson;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotates a map or collection getter/setter with the type expected within the collection.
 *
 * @author shelmberger
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JSONTypeHint
{
    Class value();
}
