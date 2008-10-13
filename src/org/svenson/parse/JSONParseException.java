package org.svenson.parse;

public class JSONParseException
    extends RuntimeException
{
    private static final long serialVersionUID = 2916869311842277595L;

    public JSONParseException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public JSONParseException(String message)
    {
        super(message);
    }
}
