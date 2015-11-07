package org.svenson;

public class CyclicStructureException
    extends SvensonRuntimeException
{
    private static final long serialVersionUID = -4937201415177884428L;


    public CyclicStructureException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public CyclicStructureException(String message)
    {
        super(message);
    }


    public CyclicStructureException(Throwable cause)
    {
        super(cause);
    }
}
