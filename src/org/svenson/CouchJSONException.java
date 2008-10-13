package org.svenson;

public class CouchJSONException extends RuntimeException
{
    private static final long serialVersionUID = 4226309917508045635L;

    public CouchJSONException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public CouchJSONException(String message)
    {
        super(message);
    }

    public CouchJSONException(Throwable cause)
    {
        super(cause);
    }

}
