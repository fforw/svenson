package org.svenson.test;

import java.util.concurrent.atomic.AtomicInteger;

public class ComplexReadOnlyChild
{
    private final static AtomicInteger count = new AtomicInteger(0);

    public ComplexReadOnlyChild()
    {
        count.incrementAndGet();
    }

    public static void reset()
    {
        count.set(0);
    }

    public static int getCount()
    {
        return count.get();
    }
}
