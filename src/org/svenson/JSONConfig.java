package org.svenson;

public class JSONConfig
{
    private JSON jsonGenerator;
    private JSONParser jsonParser;
    
    public JSONConfig()
    {
        this(new JSON(), new JSONParser());
    }
    
    public JSONConfig(JSON json, JSONParser parser)
    {
        this.jsonGenerator = json;
        this.jsonParser = parser;
    }
    
    public JSON getJsonGenerator()
    {
        return jsonGenerator;
    }
    
    public JSONParser getJsonParser()
    {
        return jsonParser;
    }
    
    @Override
    public String toString()
    {
        return super.toString() + ": jsonGenerator = " + jsonGenerator + ", jsonParser = " + jsonParser;
    }
}
