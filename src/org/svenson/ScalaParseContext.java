package org.svenson;

import java.util.Map;
import java.util.Collection;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Scala-compatible ParseContext implementation. It's a subclass of
 * JavaParseContext, thus it's suitable for seamless use together with other
 * (pure Java) software that utilizes Svenson's JSONParser class in the same
 * JVM. Most methods that do Scala-specific stuff fall back to JavaParseContext
 * if they encounter something that isn't Scala-specific (e.g. a
 * java.util.Collection instance, etc).
 */
public class ScalaParseContext
    extends JavaParseContext
{
    private static Logger log = LoggerFactory.getLogger(ScalaParseContext.class);

    /**
     * Scala's mutable collection implementation is called a "buffer". All
     * buffer classes mix in the Buffer trait.
     */
    public static Class BUFFER_CLASS = null;

    /**
     * Likewise, mutable maps mix in this Map trait. Note, however, that this is
     * the mutable Map trait (scala.collection.mutable.Map), not the same as
     * Predef.Map, which is immutable and what you get by default in the Scala
     * REPL.
     */
    public static Class MAP_CLASS = null;

    /**
     * This field (accessed through {@link #runningWithScala()}) is set to true
     * when Scala's buffer and mutable map classes are available in the JVM;
     * false (the default) otherwise.
     */
    private static boolean runningWithScala = false;

    /*
     * Attempt to load Scala's buffer and immutable map classes, then proceed to
     * set runningWithScala to true if no exception is thrown. Otherwise (e.g.
     * if a ClassNotFoundException is thrown), the default false stays.
     */
    static
    {
        try
        {
            BUFFER_CLASS = Class.forName("scala.collection.mutable.Buffer");
            MAP_CLASS = Class.forName("scala.collection.mutable.Map");
            runningWithScala = true;
        }
        catch (Throwable t)
        {
        }
    }


    /**
     * Used by {@link JSONParser#JSONParser()} to determine whether a
     * Scala-compatible ParseContext impl should be used.
     * 
     * @return true if running with Scala, false otherwise
     */
    public static boolean runningWithScala()
    {
        return runningWithScala;
    }


    public ScalaParseContext(JSONParser parser, Object t, Class mt)
    {
        super(parser, t, mt);
    }


    public ScalaParseContext(JSONParser parser, Object t, Class mt, ParseContext p)
    {
        super(parser, t, mt, p);
    }


    @Override
    public boolean isCollection()
    {
        if (log.isDebugEnabled())
        {
            log.info("checking: {} of {} ({})", new Object[] { target, target.getClass(),
                BUFFER_CLASS.isAssignableFrom(target.getClass()) });
        }
        return (super.isCollection() || (target != null) &&
            BUFFER_CLASS.isAssignableFrom(target.getClass()));
    }


    @Override
    public void doAdd(Object value)
    {
        if (super.isCollection())
        {
            super.doAdd(value);
        }

        try
        {
            Class[] argTypes = new Class[] { Object.class };
            Method adder;

            if (target instanceof Collection)
            {
                adder = Collection.class.getMethod("add", argTypes);
            }
            else
            {
                adder = BUFFER_CLASS.getMethod("$plus", argTypes);
            }

            adder.invoke(target, value);
        }
        catch (Throwable t)
        {
            throw new RuntimeException("failed to invoke 'append' from '" + BUFFER_CLASS +
                "' on '" + target + "' with '" + value + "'", t);
        }
    }


    @Override
    public boolean isMap()
    {
        if (log.isDebugEnabled())
        {
            log.info("checking: {} of {} ({})", new Object[] { target, target.getClass(),
                MAP_CLASS.isAssignableFrom(target.getClass()) });
        }
        return (super.isMap() || (target != null) && MAP_CLASS.isAssignableFrom(target.getClass()));
    }


    @Override
    public void doPut(String name, Object value)
    {
        if (super.isMap())
        {
            super.doPut(name, value);
        }

        try
        {
            Class[] argTypes = new Class[] { Object.class, Object.class };
            Method putter;

            if (target instanceof Map)
            {
                putter = Map.class.getMethod("put", argTypes);
            }
            else
            {
                putter = MAP_CLASS.getMethod("put", argTypes);
            }

            putter.invoke(target, name, value);
        }
        catch (Throwable t)
        {
            throw new RuntimeException("failed to invoke 'put' from '" + MAP_CLASS + "' on '" +
                target + "' (" + target.getClass() + ") with '" + name + " => " + value + "'", t);
        }
    }
}
