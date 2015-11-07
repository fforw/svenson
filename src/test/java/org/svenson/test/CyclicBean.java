package org.svenson.test;

public class CyclicBean
{
    private Inner inner;


    public void setInner(Inner inner)
    {
        this.inner = inner;
    }


    public Inner getInner()
    {
        return inner;
    }


    public static class Inner
    {
        private final CyclicBean cyclicBean;


        public Inner(CyclicBean cyclicBean)
        {
            this.cyclicBean = cyclicBean;
        }


        public CyclicBean getCyclicBean()
        {
            return cyclicBean;
        }
    }
}
