package org.svenson.parse;

public enum TokenType
{
    BRACE_OPEN("{"),
    BRACE_CLOSE("}"),
    COLON(":"),
    BRACKET_OPEN("["),
    BRACKET_CLOSE("]"),
    COMMA(","),
    STRING(String.class),
    INTEGER(Long.class),
    DECIMAL(Double.class),
    TRUE(Boolean.TRUE),
    FALSE(Boolean.FALSE),
    NULL(null),
    END(null);

    private Object defaultContent;

    private TokenType(Object defaultContent)
    {
        this.defaultContent = defaultContent;
    }

    public void checkValue(Object value) throws IllegalArgumentException
    {
        if (isClassRestricted())
        {
            Class cls = (Class)defaultContent;
            if ( !cls.isAssignableFrom(value.getClass()))
            {
                throw new IllegalArgumentException("Values for "+this.name()+" must be a "+cls);
            }
        }
        else
        {
            if (defaultContent == null)
            {
                if (value != null)
                {
                    throw new IllegalArgumentException("Only null values allowed for "+this.name());
                }
            }
            else
            {
                if (!defaultContent.equals(value))
                {
                    throw new IllegalArgumentException("Value for "+this.name()+" must be "+defaultContent);
                }
            }
        }
    }

    public boolean isClassRestricted()
    {
        return defaultContent instanceof Class;
    }

    public Object getDefaultContent()
    {
        return defaultContent;
    }

    public boolean isPrimitive()
    {
        return this == TRUE || this == FALSE || this == NULL || this == STRING || this == INTEGER || this == DECIMAL;
    }
}
