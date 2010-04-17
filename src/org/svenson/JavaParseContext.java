package org.svenson;

import java.util.Collection;
import java.util.Map;

/**
 * Default implementation of ParseContext that behaves just like any other
 * Svenson ParseContext up to and including version 1.3.6.
 */
public class JavaParseContext
    implements ParseContext
{
    protected JSONParser parser;

    protected Object target;

    protected ParseContext parent;

    protected Class memberType;

    protected String info = "";


    public JavaParseContext(JSONParser parser, Object target, Class memberType)
    {
        this(parser, target, memberType, null);
    }


    public JavaParseContext(JSONParser parser, Object target, Class memberType, ParseContext parent)
    {
        this.parser = parser;
        this.target = target;
        this.parent = parent;
        this.memberType = memberType;
    }


    public Object getTarget()
    {
        return target;
    }


    public ParseContext getParent()
    {
        return parent;
    }


    public void setParent(ParseContext parent)
    {
        this.parent = parent;
    }


    public Class getMemberType()
    {
        return memberType;
    }


    public String getInfo()
    {
        return info;
    }


    public void setInfo(String info)
    {
        this.info = info;
    }


    public ParseContext push(Object target, Class memberType, String info)
    {
        ParseContext child = parser.getParseContext(target, memberType);
        child.setInfo(this.getInfo() + info);
        child.setParent(this);
        return child;

    }


    public ParseContext pop()
    {
        return parent;
    }


    public String getParsePathInfo(String name)
    {
        String parsePathInfo;
        if (name.equals("[]"))
        {
            parsePathInfo = info + name;
        }
        else
        {
            parsePathInfo = info + "." + name;
        }
        return parsePathInfo;
    }


    public boolean isCollection()
    {
        return Collection.class.isAssignableFrom(target.getClass());
    }


    public void doAdd(Object value)
    {
        ((Collection) target).add(value);
    }


    public boolean isMap()
    {
        return Map.class.isAssignableFrom(target.getClass());
    }


    public void doPut(String name, Object value)
    {
        ((Map) target).put(name, value);
    }


    @Override
    public String toString()
    {
        return super.toString() + ", target = " + target + ", memberType = " + memberType +
            ", info = " + info;
    }
}
