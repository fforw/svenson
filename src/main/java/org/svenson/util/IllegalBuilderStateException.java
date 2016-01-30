package org.svenson.util;


import org.svenson.SvensonRuntimeException;

public class IllegalBuilderStateException
    extends SvensonRuntimeException
{
    private static final long serialVersionUID = 356614100967539262L;


    public IllegalBuilderStateException(String message)
    {
        super(message);
    }


    public IllegalBuilderStateException(String message, Throwable cause)
    {
        super(message, cause);
    }


    public IllegalBuilderStateException(Throwable cause)
    {
        super(cause);
    }
}
