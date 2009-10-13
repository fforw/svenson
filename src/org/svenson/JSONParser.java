package org.svenson;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.converter.JSONConverter;
import org.svenson.converter.TypeConverter;
import org.svenson.converter.TypeConverterRepository;
import org.svenson.matcher.EqualsPathMatcher;
import org.svenson.matcher.PathMatcher;
import org.svenson.tokenize.JSONCharacterSource;
import org.svenson.tokenize.JSONTokenizer;
import org.svenson.tokenize.Token;
import org.svenson.tokenize.TokenType;
import org.svenson.util.ExceptionWrapper;
import org.svenson.util.TypeConverterCache;
import org.svenson.util.ValueHolder;

/**
 * Converts JSON strings into graphs of java objects. It offers
 * features to support the full spectrum of totally dynamic parsing
 * to parsing into concrete java types, including a mix in between.
 *
 * @author shelmberger
 *
 * @see JSONProperty
 * @see DynamicProperties
 * @see #addTypeHint(String, Class)
 * @see #setTypeHints(Map)
 */
public class JSONParser
{
    protected static Logger log = LoggerFactory.getLogger(JSONParser.class);

    private Map<PathMatcher, Class> typeHints = new HashMap<PathMatcher, Class>();

    private TypeMapper typeMapper;

    private final static JSONParser defaultJSONParser = new JSONParser();

    public static JSONParser defaultJSONParser()
    {
        return defaultJSONParser;
    }

    private Map<Class,Class> interfaceMappings = new HashMap<Class, Class>();

    private List<ObjectFactory> objectFactories = new ArrayList<ObjectFactory>();
    
    private boolean allowSingleQuotes;

    private TypeConverterCache typeConverterCache;
    
    /**
     * Default Interface initializer
     */
    {
        interfaceMappings.put(Collection.class, ArrayList.class);
        interfaceMappings.put(List.class, ArrayList.class);
        interfaceMappings.put(Map.class, HashMap.class);
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
    
    /**
     * Sets the type converter repository used by the parser.
     * 
     * @see JSONConverter
     * @param typeConverterRepository
     */
    public void setTypeConverterRepository(TypeConverterRepository typeConverterRepository)
    {
        this.typeConverterCache = new TypeConverterCache(typeConverterRepository);
    }

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
            Class typeHint = getTypeHint("", tokenizer);

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
                t = (T) createNewTargetInstance(targetType, true);
                parseObjectInto(new ParseContext(t,null), tokenizer);
            }
            else if (type == TokenType.BRACKET_OPEN)
            {
                t = (T) createNewTargetInstance(targetType, false);
                parseArrayInto(new ParseContext(t,null), tokenizer);
            }
            else if (type == TokenType.STRING && Enum.class.isAssignableFrom(targetType) )
            {
                return (T)Enum.valueOf((Class<Enum>)targetType, (String)token.value());
            }
            else
            {
                throw new JSONParseException("unexpected token "+token);
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
            Class typeHint = getTypeHint(cx, cx.getParsePathInfo("[]"), tokenizer, "[]", false);
            if (valueType.isPrimitive())
            {
                value = valueToken.value();

                if(typeHint != null)
                {
                    value = convertValueTo(value, typeHint);
                }
            }
            else
            {
                Object newTarget = null;
                if (valueType == TokenType.BRACE_OPEN)
                {
                    newTarget = createNewTargetInstance(typeHint, true);
                    parseObjectInto(cx.push(newTarget,null,"[]"), tokenizer);
                }
                else if (valueType == TokenType.BRACKET_OPEN)
                {
                    newTarget = createNewTargetInstance(typeHint, false);
                    parseArrayInto(cx.push(newTarget,null,"[]"), tokenizer);
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
            boolean isReadOnlyProperty = false;
            Method addMethod = null;
            if (!containerIsMap)
            {
                name = getPropertyNameFromAnnotation(cx.target, jsonName);
                isProperty = false;
                isReadOnlyProperty = false;
                if (name != null)
                {
                    boolean writeable = PropertyUtils.isWriteable(cx.target, name);
                    isReadOnlyProperty = !writeable && isReadOnlyProperty(cx.target, name);
                    isProperty = writeable || isReadOnlyProperty;
                }
                addMethod = getAddMethod(cx.target, jsonName);
            }

            TypeConverter typeConverter = null;
            
            if (typeConverterCache != null)
            {
                typeConverter = typeConverterCache.getTypeConverter( cx.target, name);
            }
            
            if (!( isProperty || containerIsMap ||containerIsDynAttrs || addMethod != null))
            {
                throw new JSONParseException("Cannot set property "+jsonName+" on "+cx.target.getClass());
            }

            
            if (name == null)
            {
                name = jsonName;
            }

            Class typeHint = getTypeHint( cx, cx.getParsePathInfo(jsonName), tokenizer, name, isProperty);
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

                    if (isProperty)
                    {
                        memberType = getTypeHintFromAnnotation(cx, name);
                    }

                    newTarget = createNewTargetInstance(typeHint, true);
                    parseObjectInto(cx.push(newTarget, memberType, "."+name), tokenizer);
                }
                else if (valueType == TokenType.BRACKET_OPEN)
                {
                    //Class memberType = null;

                    if (isProperty || containerIsMap || containerIsDynAttrs)
                    {
                        
                        Class arrayTypeHint = typeHint;
                        if (isProperty)
                        {
                            if (typeConverterCache != null)
                            {
                                if (typeConverter != null && !List.class.isAssignableFrom(arrayTypeHint))
                                {
                                    arrayTypeHint = List.class;
                                }
                            }
                        }
                        
                        newTarget = createNewTargetInstance(arrayTypeHint, false);
                        Class memberType = getTypeHintFromAnnotation(cx, name);
                        parseArrayInto(cx.push(newTarget,memberType, "."+name), tokenizer);
                    }
                    else
                    {

                        if (addMethod != null)
                        {
                            Class memberType = addMethod.getParameterTypes()[0];
                            List temp = new ArrayList();
                            parseArrayInto(cx.push(temp,memberType, "."+name), tokenizer);

                            for (Object o : temp)
                            {
                                addMethod.invoke(cx.target, o);
                            }
                            continue;
                        }
                        else
                        {
                            throw new JSONParseException("Cannot set array to property "+name+" on "+cx.target);
                        }
                    }
                }
                else
                {
                    throw new JSONParseException("Unexpected token "+valueToken);
                }

                value = newTarget;
            }

            if (typeConverter != null)
            {
                value = typeConverter.fromJSON(value);
            }
            
            if(typeHint != null)
            {
                value = convertValueTo(value, typeHint);
            }
            
            if (isProperty)
            {
                if (!isReadOnlyProperty)
                {
                    try
                    {
                        Class targetClass = PropertyUtils.getPropertyType(cx.target, name);
//                        if (typeConverter != null)
//                        {
//                            value = typeConverter.fromJSON(value);
//                        }
                        PropertyUtils.setProperty(cx.target, name, value);
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

            first = false;
        } // end while
    }

    private boolean isReadOnlyProperty(Object target, String name) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        if (name != null)
        {
            PropertyDescriptor desc = PropertyUtils.getPropertyDescriptor(target, name);
            JSONProperty anno = desc.getReadMethod().getAnnotation(JSONProperty.class);
            return anno != null && anno.readOnly();
        }        
        return false;
    }

    private Class getReplacementForKnownInterface(Class type)
    {
        if (type != null && type.isInterface())
        {
            for (Map.Entry<Class, Class> e : interfaceMappings.entrySet())
            {
                if (e.getKey().isAssignableFrom(type))
                {
                    return e.getValue();
                }
            }

            throw new IllegalArgumentException("No Mapping found for "+type+". cannot instantiate interfaces ");
        }
        return null;
    }

    private static ConcurrentMap<Class, ValueHolder<Map<String,Method>>> classToAddMethods = new ConcurrentHashMap<Class, ValueHolder<Map<String,Method>>>();
    
    private Method getAddMethod(Object bean, String name)
    {
        ValueHolder<Map<String,Method>> holder = new ValueHolder<Map<String,Method>>();
        
        Class cls = bean.getClass();
        ValueHolder<Map<String,Method>> existing = classToAddMethods.putIfAbsent(cls, holder);
        if (existing != null)
        {
            holder = existing;
        }
        
        Map<String,Method> addMethods = holder.getValue();
        if (addMethods == null)
        {
            synchronized (holder)
            {
                addMethods = holder.getValue();
                if (addMethods == null)
                {
                    addMethods = new HashMap<String, Method>();
                    for (Method m : cls.getMethods())
                    {
                        String methodName = m.getName();
                        if (methodName.startsWith("add") && (m.getModifiers() & Modifier.PUBLIC) != 0 && m.getParameterTypes().length == 1 )
                        {
                            String propertyName = Introspector.decapitalize(methodName.substring(3));
                            addMethods.put(propertyName, m);
                        }
                    }
    
                    holder.setValue(addMethods);
                }
            }
        }
        return addMethods.get(name);            
    }

    private Object convertValueTo(Object value, Class targetClass)
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

        if (value instanceof String && Enum.class.isAssignableFrom(targetClass))
        {
            value = Enum.valueOf((Class<Enum>)targetClass, (String)value);
        }
        else if (!targetClass.isAssignableFrom(value.getClass()))
        {
            value = ConvertUtils.convert(value.toString(), targetClass);
        }
        return value;
    }


    private Object createNewTargetInstance(Class typeHint, boolean object)
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
            if (log.isDebugEnabled())
            {
                log.debug("replace null typeHint with "+typeHint);
            }
        }

        if (typeHint.isInterface())
        {
            Class replacement = getReplacementForKnownInterface(typeHint);

            if (replacement != null)
            {
                typeHint = replacement;
                if (log.isDebugEnabled())
                {
                    log.debug("interface replaced with "+typeHint);
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
            return typeHint.newInstance();
        }
        catch (InstantiationException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (IllegalAccessException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
    }

    private Class getTypeHint(ParseContext cx, String parsePathInfo, JSONTokenizer tokenizer, String name, boolean isProperty)
    {
        Class memberType = cx.getMemberType();
        
        if (memberType == null && isProperty)
        {
            Class typeOfProperty = getTypeOfProperty(cx, name);
            if (typeOfProperty != null)
            {
                memberType = typeOfProperty;
            }
        }
        
        if (log.isDebugEnabled())
        {
            log.debug("typeHint = "+memberType+", name = "+name);
        }

        Class cls = getTypeHint( parsePathInfo,tokenizer);

        if (cls != null)
        {
            memberType = cls;
            if (log.isDebugEnabled())
            {
                log.debug("set typeHint to  "+memberType);
            }
        }
        return memberType;
    }

    private Class getTypeOfProperty(ParseContext cx, String name)
    {
        Class result = null;
        Object target = cx.target;
        PropertyDescriptor pd;
        try
        {
            pd = PropertyUtils.getPropertyDescriptor(target, name);

            if (pd != null)
            {
                Method m = pd.getWriteMethod();
                if (m != null)
                {
                    result = m.getParameterTypes()[0];
                }
            }
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
        return result;
    }

    private Class getTypeHint(String parsePathInfo, JSONTokenizer tokenizer)
    {
        Class typeHint = null;
        for (Map.Entry<PathMatcher, Class> e : typeHints.entrySet())
        {
            PathMatcher matcher = e.getKey();
            if (matcher.matches(parsePathInfo, Object.class))
            {
                if (log.isDebugEnabled())
                {
                    log.debug("Parse path '" + parsePathInfo + "' matches " + matcher + ": setting type hint to " + typeHint);
                }
                typeHint = e.getValue();
                break;
            }
        }

        if (typeMapper != null)
        {
            Class typeHintFromTypeMapper = typeMapper.getTypeHint(tokenizer, parsePathInfo, typeHint);

            if (typeHintFromTypeMapper != null)
            {
                typeHint = typeHintFromTypeMapper;
            }
        }

        return typeHint;
    }

    private static ConcurrentMap<Class,ValueHolder<Map<String,Class>>> classToTypeHintFromAnnotation = new ConcurrentHashMap<Class, ValueHolder<Map<String,Class>>>();
        
    private Class getTypeHintFromAnnotation(ParseContext cx, String name)
    {
        Class cls = cx.target.getClass();

        ValueHolder<Map<String,Class>> holder = new ValueHolder<Map<String,Class>>();
        ValueHolder<Map<String,Class>> existing = classToTypeHintFromAnnotation.putIfAbsent(cls, holder);
        if (existing != null)
        {
            holder = existing;
        }

        Map<String,Class> typeHintsFromAnnotation = holder.getValue();
        if (typeHintsFromAnnotation == null)
        {
            synchronized (holder)
            {
                typeHintsFromAnnotation = holder.getValue();
                if (typeHintsFromAnnotation == null)
                {
                    typeHintsFromAnnotation = new HashMap<String, Class>();

                    for (Method m : cls.getMethods())
                    {
                        JSONTypeHint typeHintAnnotation = m.getAnnotation(JSONTypeHint.class);
                        if (typeHintAnnotation != null)
                        {
                            String propertyName = getPropertyNameFromMethod(m);
                            if (propertyName != null)
                            {
                                typeHintsFromAnnotation.put(propertyName, typeHintAnnotation.value());
                            }
                        }
                    }
                    holder.setValue( typeHintsFromAnnotation);
                }
            }
        }
        return typeHintsFromAnnotation.get(name);
    }

    private String getPropertyNameFromMethod(Method m)
    {
        String methodName = m.getName();
        boolean isIsser = methodName.startsWith("is");
        boolean isGetter = isIsser || methodName.startsWith("get");
        boolean isSetter = methodName.startsWith("set");
        if (isGetter || isSetter)
        {
            return Introspector.decapitalize(methodName.substring(isIsser ? 2 : 3));
        }
        return null;
    }

    private class ParseContext
    {
        private Object target;
        private ParseContext parent;
        private Class memberType;
        private String info="";

        public ParseContext(Object target, Class memberType)
        {
            this(target,memberType,null);
        }

        private ParseContext(Object target, Class memberType, ParseContext parent)
        {
            this.target = target;
            this.parent = parent;
            this.memberType = memberType;
        }

        public Class getMemberType()
        {
            return memberType;
        }

        public ParseContext push(Object target, Class memberType, String info)
        {
            ParseContext child = new ParseContext(target, memberType, this);
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
    }

    /**
     * Returns the java bean property name for the given json property name.
     *
     * @param target    bean
     * @param value     json property name
     * @return java bean name, not guaranteed to actually exist.
     *
     */
    public static String getPropertyNameFromAnnotation(Object target, String value)
    {
        for (PropertyDescriptor pd : PropertyUtils.getPropertyDescriptors(target.getClass()))
        {
            String jsonPropertyName = getJSONPropertyNameFromDescriptor(target, pd);

            if (jsonPropertyName.equals(value))
            {
                return pd.getName();
            }
        }

        return null;
    }

    /**
     * Returns the JSON property name for the given property descriptor
     *
     * @param bean
     * @param pd
     */
    public static String getJSONPropertyNameFromDescriptor(Object bean, PropertyDescriptor pd)
    {
        Method readMethod = pd.getReadMethod();
        Method writeMethod = pd.getWriteMethod();

        JSONProperty jsonProperty = null;
        if (readMethod != null)
        {
            jsonProperty = readMethod.getAnnotation(JSONProperty.class);
        }
        if (jsonProperty == null && writeMethod != null)
        {
            jsonProperty = writeMethod.getAnnotation(JSONProperty.class);
        }

        if (jsonProperty != null && !jsonProperty.value().equals("") )
        {
            return jsonProperty.value();
        }
        else
        {
            return pd.getName();
        }
    }
}
