package org.svenson.perftest;

public class Foo
{
    private boolean quux;
    private String bar, baz;
    private int qux;

    public Foo()
    {
    }

    public Foo(String bar, String baz, int qux, boolean quux)
    {
        this.bar = bar;
        this.baz = baz;
        this.qux = qux;
        this.quux = quux;
    }

    public String getBar()
    {
        return bar;
    }

    public void setBar(String bar)
    {
        this.bar = bar;
    }

    public String getBaz()
    {
        return baz;
    }

    public void setBaz(String baz)
    {
        this.baz = baz;
    }

    public int getQux()
    {
        return qux;
    }

    public void setQux(int qux)
    {
        this.qux = qux;
    }

    public boolean isQuux()
    {
        return quux;
    }

    public void setQuux(boolean quux)
    {
        this.quux = quux;
    }
}
