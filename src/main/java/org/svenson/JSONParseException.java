package org.svenson;


/**
 * Is thrown when an error happens during JSON parsing.
 *
 * @author fforw at gmx dot de
 *
 */
public class JSONParseException
    extends SvensonRuntimeException
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
