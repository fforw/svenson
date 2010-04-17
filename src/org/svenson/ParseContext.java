package org.svenson;

public interface ParseContext
{
    ParseContext push(Object target, Class memberType, String info);


    ParseContext pop();


    String getParsePathInfo(String name);


    /**
     * Determines if target should be treated as an ordered collection of other
     * objects.
     * 
     * @return true if target type is a collection, and {@link #doAdd(Object)}
     *         needs to be used to populate target; false otherwise
     */
    boolean isCollection();


    /**
     * Determines if target should be treated as a map of other objects.
     * 
     * @return true if target type is a map, and {@link #doPut(String, Object)}
     *         needs to be used to populate it; false otherwise
     */
    boolean isMap();


    /**
     * Adds member of JSON array to target, which is an instance of a class
     * implementing java.util.Collection; used when {@link #isCollection()}
     * above returns true.
     * 
     * @param value value object, as parsed from underlying JSON array
     */
    void doAdd(Object value);


    /**
     * Puts member of JSON hash to target, which is an instance of a class
     * implementing java.util.Map; used when {@link #isMap()} above returns
     * true.
     * 
     * @param name key of member object in JSON hash
     * @param value value object, as parsed from underlying JSON hash
     */
    void doPut(String name, Object value);


    // accessors & mutators below.
    Object getTarget();


    ParseContext getParent();


    void setParent(ParseContext parent);


    String getInfo();


    void setInfo(String i);


    Class getMemberType();
}
