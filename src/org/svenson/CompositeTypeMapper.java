package org.svenson;

import java.util.List;

import org.svenson.tokenize.JSONTokenizer;

/**
 * Delegates the type mapping decision to a list of type mappers.
 * @author fforw at gmx dot de
 *
 */
public class CompositeTypeMapper implements TypeMapper
{
    private List<TypeMapper> typeMappers;

    /**
     * Sets the type mappers that are consulted for type hints in the order in which they are in the list
     * @param typeMappers
     */
    public void setTypeMappers(List<TypeMapper> typeMappers)
    {
        this.typeMappers = typeMappers;
    }

    /**
     * Returns a type hint if one of the configured type mappers returned a type hint, <code>null</code> otherwise.
     */
    public Class getTypeHint(JSONTokenizer tokenizer, String parsePathInfo, Class typeHint)
    {
        for (TypeMapper typeMapper : typeMappers)
        {
            Class cls = typeMapper.getTypeHint(tokenizer, parsePathInfo, typeHint);
            if (cls != null)
            {
                return cls;
            }
        }
        return null;
    }
}
