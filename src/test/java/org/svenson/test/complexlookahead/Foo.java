package org.svenson.test.complexlookahead;

/**
* Created by sven on 21.02.15.
*/
public class Foo
    extends Base
{
    private int bar;

    public int getBar()
    {
        return bar;
    }

    public void setBar(int bar)
    {
        this.bar = bar;
    }


    @Override
    public String toString()
    {
        return super.toString() + ": "
            + "bar = " + bar
            ;
    }
}
