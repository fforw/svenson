package org.svenson;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps parts of an JSON dataset to a set of configured types based on a field
 * of that part. The class is an example of how to implement a {@link TypeMapper}.
 * <p>
 * If you have the case of JSON dataset containing different possible types that
 * you can tell apart by the contents of a single field inside the types, this class
 * can help you.
 * <p>
 * Imagine you have a JSON dataset like this:
 * <pre><code>{
    "total_rows": 3,
    "offset": 0,
    "rows": [{ "type":"foo", "value":"aaa" },{ "type":"bar", "value":"bbb" },{ "value":"ccc","type":"bar"  }]
}
</code></pre>
You can now parse the objects in the rows array into Foo and Bar instances by setting up a parser like this:
 * <pre><code>
        JSONParser parser = new JSONParser();
        PropertyValueBasedTypeMapper mapper = new PropertyValueBasedTypeMapper();
        mapper.setParsePathInfo(".rows[]");
        mapper.addFieldValueMapping("foo", Foo.class);
        mapper.addFieldValueMapping("bar", Bar.class);
        parser.setTypeMapper(mapper);
</code></pre>
 *
 * There is also a test case for this class that does implement this example.
 *
 * @author fforw at gmx dot de
 */
public class PropertyValueBasedTypeMapper extends AbstractPropertyValueBasedTypeMapper
{
    protected Map<String, Class> typeMap = new HashMap<String, Class>();

    /**
     * Mapps the given field value to the given type.
     *
     * @param value field value
     * @param cls type
     */
    public void addFieldValueMapping(String value, Class cls)
    {
        typeMap.put(value, cls);
    }
    
    /**
     * @param value
     * @return Class or <code>null</code>
     * @throws IllegalStateException if there is no class configured for this
     *             value and {@link #allowUndefined} is false.
     */
    @Override
    protected Class getTypeHintFromTypeProperty(Object value) throws IllegalStateException
    {
        Class cls = typeMap.get(value);
        if (cls == null)
        {
            if (allowUndefined)
            {
                cls = HashMap.class;
            }
            else
            {
                throw new IllegalStateException("There is no class mapped for the value \"" +
                    value + "\" of discriminator field " + value +
                    " and undefined values are not allowed");                        
            }
        }
        return cls;
    }

}
