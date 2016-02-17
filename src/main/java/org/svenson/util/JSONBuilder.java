package org.svenson.util;

import org.svenson.JSON;
import org.svenson.JSONCharacterSink;
import org.svenson.StringBuilderSink;

import java.util.Collection;

/**
 * Custom JSON builder helper class for svenson JSON generation.
 * <p>
 * Allows constructing JSON structures piece-by-piece while including
 * normally JSONified objects.
 * </p>
 *
 * @see #buildObject()
 * @see #buildObject(JSON)
 * @see #buildArray()
 * @see #buildArray(JSON)
 */
public class JSONBuilder
{
    private final JSON generator;

    private final JSONCharacterSink sink;

    /**
     * Points to the topmost / most inner object level this builder is currently positioned at.
     */
    private Level topMost;


    private JSONBuilder(JSON generator, JSONCharacterSink sink, boolean isObject)
    {
        this.generator = generator != null ? generator : JSON.defaultJSON();
        this.sink = sink;

        topMost = new Level(null, isObject);
    }


    /**
     * Inserts a comma if the current level is not on its first element and marks it as not first otherwise.
     */
    private void commaUnlessFirst()
    {
        if (!topMost.isFirst())
        {
            sink.append(",");
        }
        else
        {
            topMost.first = false;
        }
    }


    /**
     * Ensures the top level is in "build an JSON object" mode.
     *
     * @throws IllegalBuilderStateException if the builder is in array mode.
     */
    private void ensureObject()
    {
        if (!topMost.isObject())
        {
            throw new IllegalBuilderStateException("Cannot add property to array");
        }
    }


    /**
     * Ensures the top level is in "build an JSON array" mode.
     *
     * @throws IllegalBuilderStateException if the builder is in object mode.
     */
    private void ensureArray()
    {
        if (topMost.isObject())
        {
            throw new IllegalBuilderStateException("Cannot add elements to object");
        }
    }


    /**
     * Defines an object property with the given name, leaving the object open for further building.
     *
     * @param name property name
     * @return the builder itself
     */
    public JSONBuilder objectProperty(String name)
    {
        ensureUnlocked();
        ensureObject();
        commaUnlessFirst();

        generator.quote(sink, name);
        sink.append(":{");

        topMost = new Level(topMost, true);

        return this;
    }


    /**
     * Defines an array property with the given name, leaving the array open for further building.
     *
     * @param name
     * @return the builder itself
     */
    public JSONBuilder arrayProperty(String name)
    {
        ensureUnlocked();
        ensureObject();
        commaUnlessFirst();

        generator.quote(sink, name);
        sink.append(":[");

        topMost = new Level(topMost, false);
        return this;
    }


    /**
     * Defines an object array element, leaving the object open for further building.
     *
     * @return the builder itself
     */
    public JSONBuilder objectElement()
    {
        ensureUnlocked();
        ensureArray();
        commaUnlessFirst();

        sink.append("{");
        topMost = new Level(topMost, true);
        return this;
    }


    /**
     * Defines a new array as array element, leaving the new array open for further building.
     *
     * @return the builder itself
     */
    public JSONBuilder arrayElement()
    {
        ensureUnlocked();
        ensureArray();
        commaUnlessFirst();

        sink.append("[");
        topMost = new Level(topMost, false);
        return this;
    }


    /**
     * Creates a property on the current object level.
     *
     * @param name  property name
     * @param value JSONifiable property value.
     * @return the builder itself
     * @throws IllegalBuilderStateException if builder is in array mode.
     */
    public JSONBuilder property(String name, Object value)
    {
        ensureUnlocked();

        ensureObject();
        commaUnlessFirst();

        generator.quote(sink, name);
        sink.append(':');
        generator.dumpObject(sink, value);

        return this;
    }


    /**
     * Creates a new array element on the current level.
     *
     * @param value JSONifiable value
     * @return the builder itself
     * @throws IllegalBuilderStateException if builder is in object mode.
     */
    public JSONBuilder element(Object value)
    {
        ensureUnlocked();

        ensureArray();
        commaUnlessFirst();

        generator.dumpObject(sink, value);

        return this;

    }


    /**
     * Adds the given varargs as array elements to the current level.
     *
     * @param values JSONifiable values to embed in the current array
     * @return the builder itself
     */
    public JSONBuilder elements(Object... values)
    {
        ensureUnlocked();

        ensureArray();

        for (Object value : values)
        {
            commaUnlessFirst();

            generator.dumpObject(sink, value);
        }

        return this;
    }


    /**
     * Adds the given collection values as array elements to the current level.
     *
     * @param values Collection of JSONifiable values to embed in the current array
     * @return the builder itself
     */
    public JSONBuilder elements(Collection<?> values)
    {
        ensureUnlocked();

        ensureArray();

        for (Object value : values)
        {
            commaUnlessFirst();

            generator.dumpObject(sink, value);
        }

        return this;
    }


    /**
     * Closes one builder level.
     *
     * @return the builder itself .
     * @throws IllegalBuilderStateException if the root level was already closed.
     */
    public JSONBuilder close()
    {
        ensureUnlocked();

        if (topMost.isObject())
        {
            sink.append('}');
        }
        else
        {
            sink.append(']');
        }

        topMost = topMost.getParent();

        return this;
    }


    /**
     * Ensures that the builder is still writable.
     *
     * @throws IllegalBuilderStateException if the build is locked.
     */
    private void ensureUnlocked()
    {
        if (isLocked())
        {
            throw new IllegalBuilderStateException("Invalid operation on locked builder. Root level already closed.");
        }
    }


    /**
     * Returns <code>true</code> if the builder is locked, that is the topmost level has already been closed.
     *
     * @return
     * @see #output()
     */
    public boolean isLocked()
    {
        return topMost == null;
    }


    /**
     * Returns <code>true</code> if the topmost level is in object mode
     *
     * @return
     */
    public boolean isObject()
    {
        return topMost != null && topMost.isObject();
    }


    /**
     * Returns <code>true</code> if the topmost level is still hasn't received it's first element.
     *
     * @return
     */
    public boolean isFirst()
    {
        return topMost != null && topMost.isFirst();
    }


    /**
     * Returns the collected output of this builder closing all open levels if that has not already happened.
     *
     * @return JSON output
     * @throws IllegalBuilderStateException if the underlying JSON sink does not support string context extraction.
     *                                      Just use {@link #closeAll()} in this case and the builder will write
     *                                      out everything to the backing sink.
     */
    public String output()
    {
        closeAll();

        if (!(sink instanceof StringBuilderSink))
        {
            throw new IllegalBuilderStateException("Cannot get output from sink " + sink + ", it is not a " +
                "StringBuilderSink");
        }
        return ((StringBuilderSink) sink).getContent();
    }


    /**
     * Closes all open levels on the JSON builder.
     *
     * @return the builder itself
     */
    public JSONBuilder closeAll()
    {
        while (!isLocked())
        {
            close();
        }

        return this;
    }


    /**
     * Creates a new JSON builder with an object as root.
     *
     * @return new builder
     */
    public static JSONBuilder buildObject()
    {
        return buildObject(null);
    }


    /**
     * Creates a new JSON builder with an object as root using the given JSON generator.
     *
     * @param generator JSON generator
     * @return new builder
     */
    public static JSONBuilder buildObject(JSON generator)
    {
        return buildObject(generator, new StringBuilderSink());
    }


    /**
     * Creates a new JSON builder with an object as root using the given JSON generator.
     *
     * @param generator JSON generator
     * @param sink      JSON sink to append output to.
     * @return new builder
     */
    public static JSONBuilder buildObject(JSON generator, JSONCharacterSink sink)
    {
        sink.append('{');
        return new JSONBuilder(generator, sink, true);
    }


    /**
     * Creates a new JSON builder with an array as object as root.
     *
     * @return new builder
     */
    public static JSONBuilder buildArray()
    {
        return buildArray(null);
    }


    /**
     * Creates a new JSON builder with an array as object as root using the given JSON generator.
     *
     * @param generator JSON generator
     * @return new builder
     */
    public static JSONBuilder buildArray(JSON generator)
    {
        return buildArray(generator, new StringBuilderSink());
    }


    /**
     * Creates a new JSON builder with an array as object as root using the given JSON generator and character sink.
     *
     * @param generator JSON generator
     * @param sink      JSON sink to append output to.
     * @return new builder
     */

    public static JSONBuilder buildArray(JSON generator, JSONCharacterSink sink)
    {
        sink.append('[');
        return new JSONBuilder(generator, sink, false);
    }


    /**
     * Returns the current JSON builder object depth. This can be used in combination with {@link #closeUntil(Level)} to
     * reset the builder to a known deeper level.
     * <p/>
     * <pre>
     *     JSONBuilder.buildObject()
     *         .property("foo",1);
     *
     *     JSONBuilder.Level = builder.getCurrentLevel();
     *
     *     // build sub-structures, not necesessarily closing them.
     *     sub(builder);
     *
     *     // return to initial level
     *     builder.closeUntil(level);
     *
     * </pre>
     *
     * @return depth
     * @see #closeUntil(Level)
     */
    public Level getCurrentLevel()
    {
        return topMost;
    }


    /**
     * Closes all open levels above the given level
     *
     * @param level level to have as current level after this method exists.
     * @return this builder
     */
    public JSONBuilder closeUntil(Level level)
    {
        while (topMost != null && topMost != level)
        {
            close();
        }

        if (topMost == null)
        {
            throw new IllegalBuilderStateException("Unknown level" + level);
        }

        return this;
    }


    /**
     * Encapsulates nested objects within the JSON to be generated.
     */
    public static class Level
    {
        /**
         * parent of <code>null</code> if root object
         */
        private final Level parent;

        /**
         * Set to <code>true</code> if the current level is an object literal, <code>false</code> if it is an array
         * literal
         */
        private final boolean isObject;

        /**
         * Set to <code>true</code> if we haven't yet generated a property / element on the current level.
         */
        private boolean first;


        public Level(Level parent, boolean isObject)
        {
            this.parent = parent;
            this.isObject = isObject;
            this.first = true;

        }


        public Level getParent()
        {
            return parent;
        }


        public boolean isObject()
        {
            return isObject;
        }


        public boolean isFirst()
        {
            return first;
        }
    }
}
