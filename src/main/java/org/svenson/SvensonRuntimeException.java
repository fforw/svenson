package org.svenson;

public class SvensonRuntimeException extends RuntimeException
{
    private static final long serialVersionUID = 4226309917508045635L;

    public SvensonRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public SvensonRuntimeException(String message)
    {
        super(message);
    }

    public SvensonRuntimeException(Throwable cause)
    {
        super(cause);
    }

}
