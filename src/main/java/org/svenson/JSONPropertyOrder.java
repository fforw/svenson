package org.svenson;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Comparator;

import org.svenson.info.JSONPropertyInfo;
import org.svenson.info.JSONPropertyPriorityComparator;

/**
 * Marks beans as using an custom JSON property order for generation. The class of the comparator can be given as value.
 * For beans not annotated with this, the default {@link JSONPropertyPriorityComparator} is used.
 * <p>
 * If you replace it, you can feel to misuse {@link JSONProperty#priority()} if you feel like it.
 *
 * @author fforw at gmx dot de
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target(ElementType.TYPE)
@Documented
public @interface JSONPropertyOrder
{
    /**
     * Comparator to use to sort the {@link JSONPropertyInfo} entries for this class.
     * @return  comparator class
     */
    Class<? extends Comparator<JSONPropertyInfo>> value();
}
