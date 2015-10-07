package org.svenson;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.converter.DefaultTypeConverterRepository;
import org.svenson.converter.TypeConverter;
import org.svenson.info.ConstructorInfo;
import org.svenson.info.JSONClassInfo;
import org.svenson.info.JSONPropertyInfo;
import org.svenson.info.JavaObjectSupport;
import org.svenson.info.ObjectSupport;
import org.svenson.info.ParameterInfo;
import org.svenson.matcher.EqualsPathMatcher;
import org.svenson.matcher.PathMatcher;
import org.svenson.tokenize.JSONCharacterSource;
import org.svenson.tokenize.JSONTokenizer;
import org.svenson.tokenize.Token;
import org.svenson.tokenize.TokenType;
import org.svenson.util.ExceptionWrapper;
import org.svenson.util.TokenUtil;

/**
 * Converts JSON strings into graphs of java objects. It offers
 * features to support the full spectrum of totally dynamic parsing
 * to parsing into concrete java types, including a mix in between.
 * <p>
 * A parser instance can be used from different threads at the same time. Keep
 * in mind though that the type hint, type mapper etc configuration of a JSONParser
 * is its state, meaning that you have to be careful to not reconfigure JSONParsers
 * while they might be used elsewhere.
 * <p>
 * To ease this, use the {@link #JSONParser(JSONParser)} copy constructor to create
 * local copies of global JSON parsers with the same config. A copied JSONParser config
 * gets its own copy of all config so that changing it will not affect the original 
 * JSONParser.
 *
 * @author fforw at gmx dot de
 *
 * @see JSONProperty
 * @see DynamicProperties
 * @see #addTypeHint(String, Class)
 * @see #setTypeHints(Map)
 */
public class JSONParser
{
    protected static Logger log = LoggerFactory.getLogger(JSONParser.class);

    private final static JSONParser defaultJSONParser = new JSONParser();

    private Map<PathMatcher, Class> typeHints = new HashMap<PathMatcher, Class>();

    private TypeMapper typeMapper;

    private Map<Class,Class> interfaceMappings;

    private List<ObjectFactory> objectFactories = new ArrayList<ObjectFactory>();
    
    private boolean allowSingleQuotes;

    private Map<Class,TypeConverter> typeConvertersByClass;

    private ObjectSupport objectSupport;

    private DefaultTypeConverterRepository typeConverterRepository;

    public JSONParser()
    {
        interfaceMappings = new HashMap<Class, Class>();
        interfaceMappings.put(Collection.class, ArrayList.class);
        interfaceMappings.put(Set.class, HashSet.class);
        interfaceMappings.put(List.class, ArrayList.class);
        interfaceMappings.put(Map.class, HashMap.class);
        this.objectSupport = new JavaObjectSupport();
    }
    
    public void setObjectSupport(ObjectSupport objectSupport)
    {
        this.objectSupport = objectSupport;
    }
    
    /**
     * Copy constructor
     * @param src   JSONParser to be copied or <code>null</code> to create a default parser.
     */
    public JSONParser(JSONParser src)
    {
        this();
     
        if (src != null)
        {
            typeHints = new HashMap<PathMatcher, Class>(src.typeHints);
    
            this.typeMapper = src.typeMapper;
    
            this.interfaceMappings = new HashMap<Class, Class>(src.interfaceMappings);
    
            this.objectFactories = new ArrayList<ObjectFactory>(src.objectFactories);
            
            this.allowSingleQuotes = src.allowSingleQuotes;
    
            if (src.typeConvertersByClass != null)
            {
                this.typeConvertersByClass = new HashMap<Class, TypeConverter>(src.typeConvertersByClass);
            }
            
            this.objectSupport = src.objectSupport;
        }
    }

    public static JSONParser defaultJSONParser()
    {
        return defaultJSONParser;
    }
    
    /**
     * Sets a {@link TypeMapper} to use on the token streams parsed
     * by this parser.
     * @param typeMapper
     */
    public void setTypeMapper(TypeMapper typeMapper)
    {
        this.typeMapper = typeMapper;
    }

    /**
     * Registers a type converter to provide conversion for bean properties.
     *  
     * @param cls           target bean property type
     * @param converter     converter
     */
    public void registerTypeConversion(Class cls, TypeConverter converter)
    {
        if (typeConvertersByClass == null)
        {
            typeConvertersByClass = new HashMap<Class, TypeConverter>();
        }
        typeConvertersByClass.put(cls, converter);
    }

    /**
     * Sets the type hint map that maps a parse path info to a type to use
     * for this parse path location.
     *
     * ( e.g. <code>".value[]"</code> mapped to <code>FooBean.class</code> would
     * make the parser create FooBean instances for all array elements inside the value property of the root object )
     *
     * @param typeHints
     */
    public void setTypeHints(Map<String, Class> typeHints)
    {
        this.typeHints = new HashMap<PathMatcher, Class>();
        for (Map.Entry<String,Class> e : typeHints.entrySet())
        {
            this.typeHints.put(new EqualsPathMatcher(e.getKey()), e.getValue());
        }
        
    }

    /**
     * Makes it possible to define which implementation is to be used for an interface.
     * Per default {@link Collection} and {@link List} are mapped to {@link ArrayList} and {@link Map} is mapped to {@link HashMap}.
     * @param interfaceMappings
     */
    public void setInterfaceMappings(Map<Class, Class> interfaceMappings)
    {
        for (Map.Entry<Class, Class> e : interfaceMappings.entrySet())
        {
            Class iface = e.getKey();
            Class cls = e.getValue();
            if (!iface.isInterface())
            {
                throw new IllegalArgumentException("The key "+iface+" must be an interface that is mapped to a class type.");
            }
            if (cls.isInterface())
            {
                throw new IllegalArgumentException("The value "+cls+" must be a class type.");
            }
            if (!iface.isAssignableFrom(cls))
            {
                throw new IllegalArgumentException("The class "+cls+" does not implement the interface "+iface);
            }
        }

        this.interfaceMappings = interfaceMappings;
    }

    public void addObjectFactory(ObjectFactory objectFactory)
    {
        if (objectFactory == null)
        {
            throw new IllegalArgumentException("objectFactory can't be null");
        }
        
        objectFactories.add(objectFactory);
    }
    
    /**
     * Sets a type hint for a given parsing path location.
     * 
     * The parse path location is built by appending "[]" whenever
     * the parser enters an array and ".propertyName" whenever the
     * parser enters the property value of an object. 
     * 
     *  <p>
     *  for example: using a type hint
     *  <br><br>
     *  <code>parser.setTypeHint(".foos[]", Foo.class);</code>
     *  <br><br>
     *   would map a json string like:
     *
     *  <pre>
     *  {
     *      "foos" : [ &hellip; ]
     *  }
     *  </pre>
     *
     *  so that the values inside the array of the foo property
     *  of the root object are mapped to Foo instances.
     *
     */
    public void addTypeHint(String key, Class typeHint)
    {
        this.typeHints.put(new EqualsPathMatcher(key), typeHint);
    }

    /**
     * Adds a new type hint based on the given path matcher. 
     * 
     * @param pathMatcher   path matcher
     * @param typeHint      type to use when the matcher matches
     */
    public void addTypeHint(PathMatcher pathMatcher, Class typeHint)
    {
        this.typeHints.put(pathMatcher, typeHint);
    }
    
//    /**
//     * Sets the type converter repository used by the parser.
//     * 
//     * @see JSONConverter
//     * @param typeConverterRepository
//     */
//    public void setTypeConverterRepository(TypeConverterRepository typeConverterRepository)
//    {
//        this.support.setTypeConverterRepository(typeConverterRepository);
//    }

    public final Object parse( String json)
    {
        JSONTokenizer tokenizer = new JSONTokenizer(json, allowSingleQuotes);
        try
        {
            return parse(tokenizer);
        }
        finally
        {
            tokenizer.destroy();
        }
    }

    public final Object parse( JSONCharacterSource source)
    {
        JSONTokenizer tokenizer = new JSONTokenizer(source , allowSingleQuotes);
        try
        {
            return parse(tokenizer);
        }
        finally
        {
            tokenizer.destroy();
        }
    }
    
    private Object parse(JSONTokenizer tokenizer)
    {
        Token token = tokenizer.peekToken();
        
        if (token.isType(TokenType.BRACKET_OPEN))
        {
            return parse( ArrayList.class, tokenizer);
        }
        else if (token.isType(TokenType.BRACE_OPEN))
        {
            // regard type hints on root type decision
            Class typeHint = getTypeHint("", tokenizer, null, true);

            if (typeHint != null)
            {
                return parse(typeHint, tokenizer);
            }

            return parse( HashMap.class, tokenizer);
        }
        else if (token.isType(TokenType.NULL) || token.isType(TokenType.FALSE) || token.isType(TokenType.TRUE) || token.isType(TokenType.INTEGER) || token.isType(TokenType.DECIMAL) || token.isType(TokenType.STRING))
        {
            return token.value();
        }
        else
        {
            throw new JSONParseException("Invalid start token "+token);
        }
    }

    
    /**
     * Parses a JSON String
     * @param <T> The type to parse the root object into
     * @param targetType   Runtime class for <T>
     * @param json  json string
     * @return the resulting object
     */
    final public <T> T parse(Class<T> targetType, String json)
    {

        if (targetType == null)
        {
            throw new IllegalArgumentException("target type cannot be null");
        }

        if (json == null)
        {
            throw new IllegalArgumentException("json string cannot be null");
        }

        JSONTokenizer tokenizer = new JSONTokenizer(json, allowSingleQuotes);
        try
        {
            return parse(targetType, tokenizer);
        }
        finally
        {
            tokenizer.destroy();
        }
    }

    /**
     * Parses a JSON String
     * @param <T>           The type to parse the root object into
     * @param targetType    Runtime class for <T>
     * @param source        json character source to parse
     * @return the resulting object
     */
    final public <T> T parse(Class<T> targetType, JSONCharacterSource source)
    {

        if (targetType == null)
        {
            throw new IllegalArgumentException("target type cannot be null");
        }

        if (source == null)
        {
            throw new IllegalArgumentException("character source cannot be null");
        }

        JSONTokenizer tokenizer = new JSONTokenizer(source, allowSingleQuotes);
        try
        {
            return parse(targetType, tokenizer);
        }
        finally
        {
            tokenizer.destroy();
        }
    }

    private <T> T parse(Class<T> targetType, JSONTokenizer tokenizer)
    {
        T t;
        try
        {
            Token token = tokenizer.next();
            TokenType type = token.type();
            if (type == TokenType.BRACE_OPEN)
            {
                Class typeHint = getTypeHint("", tokenizer, targetType, true);
                if (typeHint != null)
                {
                    targetType = typeHint;
                }

                JSONClassInfo newClassInfo = TypeAnalyzer.getClassInfo(objectSupport, targetType);
                t = (T) createNewTargetInstance(targetType, newClassInfo, true);
                parseObjectInto(new ParseContext(t,null,newClassInfo), tokenizer);
                t = DelayedConstructor.unwrap(t);

                if (newClassInfo != null)
                {
                    Method postConstructMethod = newClassInfo.getPostConstructMethod();
                    if (postConstructMethod != null)
                    {
                        postConstructMethod.invoke(t);
                    }
                }

            }
            else if (type == TokenType.BRACKET_OPEN)
            {
                JSONClassInfo newClassInfo = TypeAnalyzer.getClassInfo(objectSupport, targetType);
                t = (T) createNewTargetInstance(targetType, newClassInfo, false);
                parseArrayInto(new ParseContext(t,null,newClassInfo), tokenizer);

                if (newClassInfo != null)
                {
                    Method postConstructMethod = newClassInfo.getPostConstructMethod();
                    if (postConstructMethod != null)
                    {
                        postConstructMethod.invoke(t);
                    }
                }
            }
            else if (type == TokenType.STRING && Enum.class.isAssignableFrom(targetType) )
            {
                return (T)Enum.valueOf((Class<Enum>)targetType, (String)token.value());
            }
            else if (type == TokenType.NULL)
            {
                return null;
            }
            else
            {
                throw new JSONParseException("unexpected token " + token);
            }
            return t;
        }
        catch (InstantiationException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (IllegalAccessException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (InvocationTargetException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (NoSuchMethodException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
    }

    
    /**
     * Allows single quotes to be used for quoting JSON strings.
     *
     * @param allowSingleQuotes
     */
    public void setAllowSingleQuotes(boolean allowSingleQuotes)
    {
        this.allowSingleQuotes = allowSingleQuotes;
    }

    /**
     * Expects the next object of the given tokenizer to be an array and parses it into the given {@link ParseContext}
     * @param cx
     * @param tokenizer
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    private void parseArrayInto(ParseContext cx, JSONTokenizer tokenizer) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        boolean containerIsCollection = Collection.class.isAssignableFrom(cx.target.getClass());

        boolean first = true;
        while(true)
        {
            Token valueToken = tokenizer.next();
            TokenType valueType = valueToken.type();
            if (valueType == TokenType.BRACKET_CLOSE)
            {
                break;
            }

            if (!first)
            {
                valueToken.expect(TokenType.COMMA);
                valueToken = tokenizer.next();
                valueType = valueToken.type();
            }

            Object value;
            Class typeHint = getTypeHint(cx, cx.getParsePathInfo("[]"), tokenizer, "[]", false, valueType.isPrimitive());
            if (valueType.isPrimitive())
            {
                value = valueToken.value();

                if(typeHint != null)
                {
                    value = convertValueTo(value, typeHint, typeConvertersByClass);
                }
            }
            else
            {
                Object newTarget = null;
                if (valueType == TokenType.BRACE_OPEN)
                {
                    JSONClassInfo classInfo = TypeAnalyzer.getClassInfo(objectSupport, typeHint);
                    newTarget = createNewTargetInstance(typeHint, classInfo, true);
                    parseObjectInto(cx.push(newTarget,null,"[]",classInfo), tokenizer);
                    newTarget = DelayedConstructor.unwrap(newTarget);

                    if (classInfo != null)
                    {
                        Method postConstructMethod = classInfo.getPostConstructMethod();
                        if (postConstructMethod != null)
                        {
                            postConstructMethod.invoke(newTarget);
                        }
                    }

                }
                else if (valueType == TokenType.BRACKET_OPEN)
                {
                    JSONClassInfo classInfo = TypeAnalyzer.getClassInfo(objectSupport, typeHint);
                    newTarget = createNewTargetInstance(typeHint, classInfo, false);
                    parseArrayInto(cx.push(newTarget,null,"[]",classInfo), tokenizer);
                    newTarget = DelayedConstructor.unwrap(newTarget);
                    if (classInfo != null)
                    {
                        Method postConstructMethod = classInfo.getPostConstructMethod();
                        if (postConstructMethod != null)
                        {
                            postConstructMethod.invoke(newTarget);
                        }
                    }
                }
                else
                {
                    throw new JSONParseException("Unexpected token "+valueToken);
                }
                value = newTarget;
            }

            if (containerIsCollection)
            {
                ((Collection)cx.target).add(value);
            }
            else 
            {
                throw new JSONParseException("Cannot add value "+value+" to "+cx.target+" ( "+cx.target.getClass()+" )");
            }

            first = false;
        }
    }

    private void parseObjectInto(ParseContext cx, JSONTokenizer tokenizer) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        boolean containerIsMap = Map.class.isAssignableFrom(cx.target.getClass());
        boolean containerIsDynAttrs = cx.target instanceof DynamicProperties;

        boolean first = true;
        while (true)
        {
            Token key ;
            if (first)
            {
                key = tokenizer.expectNext(TokenType.STRING, TokenType.BRACE_CLOSE);
            }
            else
            {
                key = tokenizer.expectNext( TokenType.COMMA, TokenType.BRACE_CLOSE);

            }
            if (key.type() == TokenType.BRACE_CLOSE)
            {
                break;
            }

            if (!first)
            {
                key = tokenizer.expectNext( TokenType.STRING);
            }
            first = false;

            String jsonName = (String)key.value();
            if (jsonName.length() == 0)
            {
                throw new JSONParseException("Invalid empty property name");
            }
            
            String name = null;

            tokenizer.expectNext(TokenType.COLON);
            Token valueToken = tokenizer.next();
            TokenType valueType = valueToken.type();

            boolean isProperty = false;
            boolean isIgnoredOnParse = false;
            JSONClassInfo classInfo = null;
            JSONPropertyInfo propertyInfo = null;
            if (!containerIsMap)
            {
                classInfo = cx.getClassInfo();
                propertyInfo = classInfo.getPropertyInfo(jsonName);
                if (propertyInfo != null)
                {
                    name = propertyInfo.getJavaPropertyName();
                }
                isProperty = false;
                isIgnoredOnParse = false;
                if (name != null)
                {
                    boolean writeable = propertyInfo.isWriteable();
                    isIgnoredOnParse = (!writeable && propertyInfo.isReadOnly());

                    if (isIgnoredOnParse)
                    {
                        if (valueType == TokenType.BRACE_OPEN)
                        {
                            TokenUtil.skipObjectValue(tokenizer);
                            continue;
                        }

                        if (valueType == TokenType.BRACKET_OPEN)
                        {
                            TokenUtil.skipArrayValue(tokenizer);
                            continue;
                        }
                    }
                    
                    if (propertyInfo.isLinkedProperty())
                    {
                        // XXX: make target/name combination and the ignored value available to the caller? how?
                        isIgnoredOnParse = true;
                    }
                    isProperty = writeable || isIgnoredOnParse;
                }
                
            }

            TypeConverter typeConverter = propertyInfo == null ? null : propertyInfo.getTypeConverter(typeConverterRepository);
            
            if (!( isProperty || containerIsMap ||containerIsDynAttrs || (propertyInfo != null && propertyInfo.canAdd())))
            {
                    throw new JSONParseException("Cannot set property "+jsonName+" on "+cx.target.getClass());
            }

            
            if (name == null)
            {
                name = jsonName;
            }

            Class typeHint = getTypeHint( cx, cx.getParsePathInfo(jsonName), tokenizer, jsonName, isProperty, valueType.isPrimitive());
            Object value;
            if (valueType.isPrimitive())
            {
                value = valueToken.value();
            }
            else
            {
                Object newTarget = null;
                if (valueType == TokenType.BRACE_OPEN)
                {
                    Class memberType = null;

                    if (cx.target instanceof DelayedConstructor)
                    {
                        DelayedConstructor target = (DelayedConstructor) cx.target;
                        ConstructorInfo constructorInfo = target.getConstructorInfo();
                        ParameterInfo parameterInfo = constructorInfo
                            .getParameterInfo(name);
                            Class ctorTypeHint;

                        Class<?> parameterType;
                        if (parameterInfo != null)
                        {
                            parameterType = constructorInfo.getConstructor().getParameterTypes()[parameterInfo
                                .getIndex()];

                            ctorTypeHint = parameterInfo.getTypeHint();
                        }
                        else
                        {
                            int wildCardArgsIndex = target.getWildCardArgsIndex();
                            if (wildCardArgsIndex >= 0)
                            {
                                parameterType = constructorInfo.getConstructor().getParameterTypes()[wildCardArgsIndex];
                                ctorTypeHint = constructorInfo.getCtorTypeHint();
                            }
                            else
                            {
                                parameterType = null;
                                ctorTypeHint = null;
                            }
                        }

                        if (ctorTypeHint != null)
                        {
                            if (parameterInfo != null && (
                                Collection.class.isAssignableFrom(parameterType) ||
                                Map.class.isAssignableFrom(parameterType)
                            ))
                            {
                                memberType = ctorTypeHint;
                            }
                            else
                            {
                                typeHint = getTypeHint( cx.info, tokenizer, ctorTypeHint, true);
                            }
                        }
                        else
                        {
                            typeHint = parameterType;
                        }
                    }

                    if (isProperty)
                    {
                        if (propertyInfo != null && propertyInfo.getTypeHint() != null)
                        {
                            memberType = typeHint != null && !Map.class.isAssignableFrom(typeHint) ? null : propertyInfo.getTypeHint();
                        }
                    }


                    JSONClassInfo newClassInfo = TypeAnalyzer.getClassInfo(objectSupport, typeHint);
                    newTarget = createNewTargetInstance(typeHint, newClassInfo, true);

                    parseObjectInto(cx.push(newTarget, memberType, "." + name, newClassInfo), tokenizer);
                    newTarget = DelayedConstructor.unwrap(newTarget);

                    if (newClassInfo != null)
                    {
                        Method postConstructMethod = newClassInfo.getPostConstructMethod();
                        if (postConstructMethod != null)
                        {
                            postConstructMethod.invoke(newTarget);
                        }
                    }
                }
                else if (valueType == TokenType.BRACKET_OPEN)
                {
                    //Class memberType = null;

                    if (propertyInfo != null && propertyInfo.canAdd())
                    {
                        List<?> temp = new ArrayList<Object>();
                        JSONClassInfo newClassInfo = TypeAnalyzer.getClassInfo(objectSupport, temp.getClass());
                        parseArrayInto(cx.push(temp, propertyInfo.getTypeHint(), "."+name, newClassInfo), tokenizer);
                        // XXX: unwrap

                        for (Object o : temp)
                        {
                            o = DelayedConstructor.unwrap(o);
                            propertyInfo.add(cx.target, o);
                        }
                        continue;
                    }
                    else if (isProperty || containerIsMap || containerIsDynAttrs)
                    {
                        
                        Class<?> arrayTypeHint = typeHint;
                        if (isProperty)
                        {
                            if (typeConverter != null && !List.class.isAssignableFrom(arrayTypeHint))
                            {
                                arrayTypeHint = List.class;
                            }
                        }

                        JSONClassInfo newClassInfo = TypeAnalyzer.getClassInfo(objectSupport, arrayTypeHint);
                        newTarget = createNewTargetInstance(arrayTypeHint, newClassInfo, false);
                        
                        Class<?> memberType = null;
                        if (propertyInfo != null)
                        {
                            memberType = propertyInfo.getTypeHint();
                        }

                        if (cx.target instanceof DelayedConstructor)
                        {
                            ParameterInfo parameterInfo = ((DelayedConstructor) cx.target).getConstructorInfo()
                                .getParameterInfo(name);
                            if (parameterInfo != null)
                            {
                                Class ctorTypeHint = parameterInfo.getTypeHint();
                                if (ctorTypeHint != null)
                                {
                                    memberType = ctorTypeHint;
                                }
                            }
                        }

                        parseArrayInto(cx.push(newTarget,memberType, "."+name,newClassInfo), tokenizer);
                        newTarget = DelayedConstructor.unwrap(newTarget);

                        if (newClassInfo != null)
                        {
                            Method postConstructMethod = newClassInfo.getPostConstructMethod();
                            if (postConstructMethod != null)
                            {
                                postConstructMethod.invoke(newTarget);
                            }
                        }
                    }
                    else
                    {
                        throw new JSONParseException("Cannot set array to property "+name+" on "+cx.target);
                    }
                }
                else
                {
                    throw new JSONParseException("Unexpected token "+valueToken);
                }

                value = newTarget;
            }

            if (typeConverter != null && !isIgnoredOnParse)
            {
                value = typeConverter.fromJSON(value);
            }
            
            if (typeHint == null && propertyInfo != null)
            {
                typeHint = propertyInfo.getType();
            }
            
            
            if(typeHint != null && !isIgnoredOnParse)
            {
                value = convertValueTo(value, typeHint, typeConvertersByClass);
            }
            
            if (isProperty)
            {
                if (!isIgnoredOnParse)
                {
                    propertyInfo.setProperty(cx.target,value);
                }
            }
            else if (containerIsMap)
            {
                ((Map)cx.target).put( name, value);
            }
            else if (containerIsDynAttrs)
            {
                ((DynamicProperties)cx.target).setProperty(name, value);
            }
            else
            {
                throw new JSONParseException("Cannot set property "+name+" on "+cx.target);
            }

        } // end while
    }


    private Class getReplacementForKnownInterface(Class type)
    {
        if (type != null && type.isInterface())
        {
            int leastTypeDistance = Integer.MAX_VALUE;
            Class best = null;
            for (Map.Entry<Class, Class> e : interfaceMappings.entrySet())
            {
                Class curInterface = e.getKey();
                if (type.isAssignableFrom(curInterface))
                {
                    if (type.getName().equals(curInterface.getName()))
                    {
                        // we already found the minimum
                        return e.getValue();
                    }
                    // .. searching for best match
                    int distance = getTypeDistance(type, curInterface, 1);
                    if (distance < leastTypeDistance)
                    {
                        leastTypeDistance = distance;
                        best = curInterface;
                    }
                }
            }

            if (best == null)
            {
                throw new IllegalArgumentException("No Mapping found for "+type+". cannot instantiate interfaces ");
            }
            
            return best;
        }
        return null;
    }

    /**
     * Finds the declaration distance between the two non-equal interfaces
     * @param type          target interface
     * @param ifaceClass    interface that is assignable from target, but not equal to it
     * @param dist          current checking distance
     * @return distance between interfaces or <code>null</code> if the target was not found 
     */
    static Integer getTypeDistance(Class type, Class ifaceClass, int dist)
    {
        for (Class cls : ifaceClass.getInterfaces())
        {
            if (type.getName().equals(cls.getName()))
            {
                return dist;
            }
            
            Integer d2 = getTypeDistance(type, cls, dist + 1 );
            if (d2 != null)
            {
                return d2;
            }
        }
        return null;
    }

    static Object convertValueTo(Object value, Class targetClass, Map<Class,TypeConverter> typeConvertersByClass)
    {
        if (targetClass == null)
        {
            throw new IllegalArgumentException("target class is null");
        }
        if (value == null)
        {
            return null;
        }
        if (targetClass.equals(Object.class))
        {
            return value;
        }

        Object convertedValue = null;

        if (targetClass.isAssignableFrom(value.getClass()))
        {
            convertedValue = value;
        }
        else if (value instanceof String && Enum.class.isAssignableFrom(targetClass))
        {
            convertedValue = Enum.valueOf((Class<Enum>)targetClass, (String)value);
        }
        else 
        {
            TypeConverter typeConverter = null;
            if (typeConvertersByClass != null)
            {
                typeConverter = typeConvertersByClass.get(targetClass);
            }
            
            if (typeConverter != null)
            {
                convertedValue = typeConverter.fromJSON(value);
            }
            else if (List.class.isInstance(value))
            {
                List list = (List)value;
                
                if (targetClass.isArray())
                {
                    convertedValue = Array.newInstance(targetClass.getComponentType(), list.size());
                    int idx=0;
                    for (Object o : list)
                    {
                        Array.set(convertedValue, idx++, o);
                    }
                }
                else if (targetClass.isAssignableFrom(HashSet.class))
                {
                    convertedValue = new HashSet(list);
                }
                else if (targetClass.isAssignableFrom(LinkedHashSet.class))
                {
                    convertedValue = new LinkedHashSet(list);
                }
                else if (targetClass.isAssignableFrom(TreeSet.class))
                {
                    convertedValue = new TreeSet(list);
                }
            }
            if (convertedValue == null)
            {
                convertedValue = ConvertUtils.convert(value, targetClass);
            }
        }
        return convertedValue;
    }


    private Object createNewTargetInstance(Class typeHint, JSONClassInfo classInfo, boolean object)
    {
        if (typeHint == null || typeHint.equals(Object.class))
        {
            if (object)
            {
                typeHint = Map.class;
            }
            else
            {
                typeHint = List.class;
            }
            if (log.isTraceEnabled())
            {
                log.trace("replace null typeHint with "+typeHint);
            }
        }

        if (typeHint.isInterface())
        {
            Class replacement = getReplacementForKnownInterface(typeHint);

            if (replacement != null)
            {
                typeHint = replacement;
                if (log.isTraceEnabled())
                {
                    log.trace("interface replaced with "+typeHint);
                }
            }
        }

        try
        {
            for (ObjectFactory factory : objectFactories)
            {
                if (factory.supports(typeHint))
                {
                    return factory.create(typeHint);
                }
            }
            
            if (typeHint.isArray())
            {
                return new ArrayList();
            }
            else
            {
                if (classInfo != null)
                {
                    ConstructorInfo ctorInfo = classInfo.getConstructorInfo();
                    if (ctorInfo != null)
                    {
                        return new DelayedConstructor(ctorInfo);
                    }
                }

                return typeHint.newInstance();
            }
        }
        catch (InstantiationException e)
        {
            throw new SvensonRuntimeException("Error creating new instance of " + typeHint.getName(), e);
        }
        catch (IllegalAccessException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
    }

    private Class getTypeHint(ParseContext cx, String parsePathInfo, JSONTokenizer tokenizer, String name, boolean isProperty, boolean primitive)
    {
        Class memberType = cx.getMemberType();

        if (memberType == null && isProperty)
        {
            JSONPropertyInfo propertyInfo = cx.getClassInfo().getPropertyInfo(name);
            if (propertyInfo != null)
            {
                Class typeOfProperty = propertyInfo.getType();
                if (typeOfProperty != null)
                {
                    Class<Object> typeHint = propertyInfo.getTypeHint();
                    if (typeHint != null && typeOfProperty.isAssignableFrom(typeHint))
                    {
                        memberType = typeHint;
                    }
                    else
                    {
                        memberType = typeOfProperty;
                    }
                }
            }
            
        }
        
        if (log.isTraceEnabled())
        {
            log.trace("typeHint = "+memberType+", name = "+name);
        }

        Class cls = getTypeHint( parsePathInfo,tokenizer, memberType, !primitive);

        if (cls != null)
        {
            memberType = cls;
            if (log.isTraceEnabled())
            {
                log.trace("set typeHint to  "+memberType);
            }
        }

        return memberType;
    }


    private Class getTypeHint(String parsePathInfo, JSONTokenizer tokenizer, Class typeHint, boolean consultTypeMapper)
    {
        for (Map.Entry<PathMatcher, Class> e : typeHints.entrySet())
        {
            PathMatcher matcher = e.getKey();
            if (matcher.matches(parsePathInfo, Object.class))
            {
                if (log.isTraceEnabled())
                {
                    log.trace("Parse path '" + parsePathInfo + "' matches " + matcher + ": setting type hint to " + typeHint);
                }
                typeHint = e.getValue();
                break;
            }
        }

        if (typeMapper != null && consultTypeMapper)
        {
            Class typeHintFromTypeMapper = typeMapper.getTypeHint(tokenizer, parsePathInfo, typeHint);

            if (typeHintFromTypeMapper != null)
            {
//                if (typeHint != null && !typeHint.isAssignableFrom(typeHintFromTypeMapper))
//                {
//                    throw new JSONParseException("Cannot refined existing type " + typeHint + " to type mapper hint decision" + typeHintFromTypeMapper);
//                }
                
                typeHint = typeHintFromTypeMapper;
            }
        }

        return typeHint;
    }

    private class ParseContext
    {
        private final Object target;
        private final ParseContext parent;
        private final Class memberType;
        private final JSONClassInfo classInfo;
        private String info="";

        public ParseContext(Object target, Class memberType, JSONClassInfo classInfo)
        {
            this(target,memberType,null, classInfo);
        }

        private ParseContext(Object target, Class memberType, ParseContext parent, JSONClassInfo classInfo)
        {
            this.target = target;
            this.parent = parent;
            this.memberType = memberType;
            this.classInfo = classInfo;

        }

        public Class getMemberType()
        {
            return memberType;
        }

        public ParseContext push(Object target, Class memberType, String info, JSONClassInfo classInfo)
        {
            ParseContext child = new ParseContext(target, memberType, this, classInfo);
            child.info = this.info + info;
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
                parsePathInfo = info+name;
            }
            else
            {
                parsePathInfo = info+"."+name;
            }
            return parsePathInfo;
        }

        @Override
        public String toString()
        {
            return super.toString()+", target = "+target+", memberType = "+memberType+", info = "+info;
        }

        public JSONClassInfo getClassInfo()
        {
            return classInfo;
        }
    }

    public void setTypeConverterRepository(DefaultTypeConverterRepository typeConverterRepository)
    {
        this.typeConverterRepository = typeConverterRepository;
    }
}
