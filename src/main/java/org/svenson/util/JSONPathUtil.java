package org.svenson.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.svenson.DynamicProperties;
import org.svenson.JSONParseException;
import org.svenson.ObjectFactory;
import org.svenson.TypeAnalyzer;
import org.svenson.info.JSONPropertyInfo;
import org.svenson.info.JavaObjectSupport;
import org.svenson.info.ObjectSupport;

/**
 * Utility class that provides support for writing and reading java object graphs
 * based on JavaScript-like path expressions (e.g. "array[5]") or "post.user["email-address"]")
 * 
 * @author fforw at gmx dot de
 *
 */
public class JSONPathUtil
{
    private final static Object GET_VALUE = new Object();
    private static final Pattern PATH_PATTERN = Pattern.compile("[\\[\\.]");

    private List<ObjectFactory<?>> objectFactories = Collections.emptyList();
    
    private boolean grow = true;
    private ObjectSupport objectSupport;
    
    public JSONPathUtil()
    {
        this(new JavaObjectSupport());
    }
    
    public JSONPathUtil(ObjectSupport objectSupport)
    {
        this.objectSupport = objectSupport;
    }

    /**
     * Sets whether growing on path expressions is allowed, that is the implementation will try
     * to fix missing objects or invalid indexes by creating new objects and increasing the size
     * of lists.  
     * 
     * @param grow
     */
    public void setGrow(boolean grow)
    {
        this.grow = grow;
    }
    
    /**
     * Sets the object factories used to create new objects when growing.
     * 
     * @param objectFactories   object factories
     */
    public void setObjectFactories(List<ObjectFactory<?>> objectFactories)
    {
        this.objectFactories = objectFactories;
    }
    
    /**
     * Sets the given property path on the given bean with the given value.
     * 
     * @param bean      root bean of a Java object graph that can consist of java beans, collections and {@link DynamicProperties} objects.
     * @param path      JavaScript-like property path ( e.g. "array[5]") or "post.user["email-address"]")
     * @param value     value to set.
     */
    public void setPropertyPath(Object bean, String path, Object value)
    {
        getOrSetPropertyPath(bean,path,value);
    }
    
    /**
     * Returns the value of the given property path read from the given object graph.
     * @param bean      root bean of a Java object graph that can consist of java beans, collections and {@link DynamicProperties} objects.
     * @param path      JavaScript-like property path e.g. ("array[5]") or "post.user["email-address"]")
     * @return  the value read.
     * @throws InvalidPropertyPathException if the property path was found to be syntactically incorrect.
     * @throws PropertyPathAccessException  if there's a <code>null</code> value or not sufficiently sized list during evaluation of the property path and {@link #grow} is set to <code>false</code> 
     */
    public Object getPropertyPath(Object bean, String path)
    {
        return getOrSetPropertyPath(bean, path, GET_VALUE);
    }

    /**
     * Internal routine for getting or setting a property path value.
     * 
     * The implementation follows the property path to get the last base object on which then the last property
     * path segment is either written or read.
     *  
     * @param bean      JSON bean.
     * @param path      JavaScript like property path expression.
     * @param value     value to set or special value {@link #GET_VALUE} to get instead of set.
     * @return  property path value if value is {@link #GET_VALUE}, <code>null</code> otherwise.
     * @throws InvalidPropertyPathException if the property path was found to be syntactically incorrect.
     * @throws PropertyPathAccessException  if there's a <code>null</code> value or not sufficiently sized list during evaluation of the property path and {@link #grow} is set to <code>false</code> 
     */
    private Object getOrSetPropertyPath(Object bean, String path, Object value) throws InvalidPropertyPathException, PropertyPathAccessException
    {
        if (bean == null)
        {
            throw new IllegalArgumentException("bean cannot be null");
        }
        
        Object rootBean = bean;
        boolean canGrow = grow && value != GET_VALUE;
        
        try
        {
            String[] parts = parsePropertyPath(path);
            Object lastDoc = null;
            JSONPropertyInfo lastPD = null;
            
            if (parts.length > 1)
            {
                for ( int curPart=0; curPart < parts.length - 1; curPart++)
                {
                    String part = parts[curPart].trim();
                    if (part.length() == 0)
                    {
                        throw new InvalidPropertyPathException(path, "empty property-path segment");
                    }
                    
                    if (isNumeric(part))
                    {
                        boolean isList = bean instanceof List;
                        boolean isArray = bean.getClass().isArray();
                        
                        if (!isList && !isArray)
                        {
                            throw new InvalidPropertyPathException(path, "numeric index on non-indexable type " + bean);
                        }
                        
                        
                        Object child = null;
                        int idx  = Integer.parseInt(part);
                        if (isList)
                        {
                            List list = (List)bean;
                            
                            if (idx < list.size())
                            {                           
                                child = list.get(idx);
                            }
                            if (child == null)
                            {
                                if (!canGrow)
                                {
                                    throw new PropertyPathAccessException(path, bean, "List has no child at index " + idx);
                                }
                                
                                Class type = null;
                                if (lastPD != null)
                                {
                                    type = lastPD.getTypeHint();
                                }
                                if (type == null)
                                {
                                    type = isNumeric(parts[curPart + 1]) ? ArrayList.class : HashMap.class;
                                }
                                child = createNewObjectOfType(type, objectFactories);

                                setIndexInList(list, idx, child);
                            }
                            lastPD = null;
                        }
                        else
                        {
                            child = Array.get(bean, idx);
                            if (child == null)
                            {                                
                                if (!canGrow)
                                {
                                    throw new PropertyPathAccessException(path, bean, "Array has no child at index " + idx);
                                }
                                child = createNewObjectOfType(bean.getClass().getComponentType(), objectFactories);
                            }
                            Object newArray = setIndexInList(bean, idx, child);
                            if (newArray != bean)
                            {
                                writeBackArray(rootBean, parts, curPart, newArray);
                            }
                        }
                        
                        lastDoc = bean;
                        bean = child;
                    }
                    else
                    {
                        part = unquotePart(part);   
                        Object child = null;
                        
                        JSONPropertyInfo propertyInfo = null;
                        if (bean instanceof Map)
                        {
                            child = ((Map)bean).get(part);
                            
                            if (child == null)
                            {
                                if (!canGrow)
                                {
                                    throw new PropertyPathAccessException(path, bean, "Map has no value at key '" + part + "'");
                                }
                                Class type = null;
                                if (lastPD != null)
                                {
                                    type = lastPD.getTypeHint();
                                }
                                else
                                {
                                    if (isNumeric(parts[curPart+1]))
                                    {
                                        type = ArrayList.class; 
                                    }
                                    else
                                    {
                                        type = HashMap.class; 
                                    }
                                }
                                child = createNewObjectOfType(type, objectFactories);
                                
                                ((Map)bean).put(part, child);
                            }
                            lastPD = null;
                        }
                        else if ((propertyInfo = TypeAnalyzer.getClassInfo(objectSupport, bean.getClass()).getPropertyInfo(part)) != null && propertyInfo.isReadable())
                        {
                            String propertyName = propertyInfo.getJavaPropertyName();
                            if (propertyName == null)
                            {
                                propertyName = part;
                            }

                            child = propertyInfo.getProperty(bean);

                            if (child == null)
                            {
                                if (!canGrow)
                                {
                                    throw new PropertyPathAccessException(path, bean,  bean + " has no value for property '" + part + "'");
                                }
                                
                                if (!propertyInfo.isWriteable())
                                {
                                    throw new InvalidPropertyPathException(path, "No write method for null value");
                                }
                                child = createNewObjectOfType(propertyInfo.getType(), objectFactories);
                                propertyInfo.setProperty(bean, child);
                            }
                        }
                        else if (bean instanceof DynamicProperties)
                        {
                            child = ((DynamicProperties)bean).getProperty(part);
                            if (child == null)
                            {
                                if (!canGrow)
                                {
                                    throw new PropertyPathAccessException(path, bean,  bean + " has no value for dynamic property '" + part + "'");
                                }
                                Class type;
                                if (isNumeric(parts[curPart+1]))
                                {
                                    type = ArrayList.class; 
                                }
                                else
                                {
                                    type = HashMap.class; 
                                }
                                child = createNewObjectOfType(type, objectFactories);
                                
                                ((DynamicProperties)bean).setProperty(part, child);
                            }
                            lastPD = null;
                        }                
                        else 
                        {
                            throw new InvalidPropertyPathException(path, "Cannot read property '" + part + "' from " + bean );
                        }
                        
                        lastDoc = bean;
                        bean = child;
                        lastPD = propertyInfo;
                    }
                }
            }
            
            String part = unquotePart(parts[parts.length-1]);

            if (value == GET_VALUE)
            {
                if (isNumeric(part))
                {
                    int idx  = Integer.parseInt(part);
                    boolean isList = bean instanceof List;

                    if (!isList && !bean.getClass().isArray())
                    {
                        throw new InvalidPropertyPathException(path, "Path component for numeric parts must be either List or Array");
                    }
                    
                    
                    if (isList)
                    {
                        return ((List)bean).get(idx);
                    }
                    else
                    {
                        return Array.get(bean, idx);
                    }
                }
                else
                {
                    return JSONBeanUtil.defaultUtil().getProperty(bean, part);
                }
            }

            if (isNumeric(part))
            {
                int idx  = Integer.parseInt(part);
                boolean isList = bean instanceof List;

                if (!isList && !bean.getClass().isArray())
                {
                    throw new InvalidPropertyPathException(path, "Path componente for numeric parts must be either List or Array");
                }
                
                
                if (isList)
                {
                    setIndexInList(bean, idx, value);
                }
                else
                {
                    Object newArray = setIndexInList(bean, idx, value);
                    if (newArray != bean)
                    {
                        writeBackArray(rootBean, parts, parts.length - 1, newArray);
                    }
                }
            }
            else
            {
                part = unquotePart(part);
                JSONPropertyInfo propertyInfo;
                if ( bean instanceof Map)
                {
                    ((Map)bean).put(part, value);
                }
                else if ((propertyInfo = TypeAnalyzer.getClassInfo(objectSupport, bean.getClass()).getPropertyInfo(part)) != null && propertyInfo.isWriteable())
                {
                    propertyInfo.setProperty(bean, value);
                }
                else if (bean instanceof DynamicProperties)
                {
                    ((DynamicProperties)bean).setProperty(part, value);
                }
                else
                {
                    throw new InvalidPropertyPathException(path, "Cannot set property path");
                }
            }
        }
        catch (NumberFormatException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (IllegalAccessException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (InstantiationException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        return null;
    }

    private static Object setIndexInList(Object bean, int idx, Object child)
    {
        if (bean instanceof List)
        {
            List l = (List)bean;
            while (l.size() <= idx)
            {
                l.add(null);
            }
            l.set(idx, child);
            return l;
        }
        else if (bean.getClass().isArray())
        {
            int length = Array.getLength(bean);
            if (length <= idx)
            {
                Object newArray = Array.newInstance(bean.getClass().getComponentType(), idx + 1);
                System.arraycopy(bean, 0, newArray, 0, length);
                bean = newArray;
            }
            Array.set(bean, idx, child);
            return bean;
        }
        else
        {
            return null;
        }
    }

    private static Object createNewObjectOfType(Class<?> type, List<ObjectFactory<?>> factories) throws InstantiationException, IllegalAccessException
    {
        Object bean = null;
        for (ObjectFactory factory : factories)
        {
            if (factory.supports(type))
            {
                bean = factory.create(type);
            }
        }
        
        if (bean == null)
        {
            if (Map.class.isAssignableFrom(type))
            {
                bean = new HashMap();
            }
            else if (List.class.isAssignableFrom(type))
            {
                return createNewArrayLike(type, factories, 1);
            }
            else if (type.isArray())
            {
                return createNewArrayLike(type, factories, 1);
            }
            else 
            {
                bean = type.newInstance();
            }
        }
        return bean;
    }

    private static Object createNewArrayLike(Class type, List<ObjectFactory<?>> factories, int size)
    {
        Object bean = null;
        for (ObjectFactory factory : factories)
        {
            if (factory.supports(type))
            {
                bean = factory.create(type);
            }
        }
        
        if (bean == null)
        {
            if (List.class.isAssignableFrom(type))
            {
                bean = new ArrayList(size);
            }
            else if (type.isArray())
            {
                bean = Array.newInstance(type.getComponentType(), size);
            }
        }
        return bean;
    }

    private static String unquotePart(String part)
    {
        char c = part.charAt(0);
        if (c == '"' || c == '\'')
        {
            part = new StringParser(part, 1).parseString(c);
        }
        return part;
    }
        
    private final static Pattern NUMERIC_PATTERN = Pattern.compile("^[0-9]+$");
    private static boolean isNumeric(String part)
    {
        return NUMERIC_PATTERN.matcher(part).matches();
    }

    private final static int HEX_LETTER_OFFSET = 'A' - '9' - 1;

    static int hexValue(char c)
    {
        int n = c;
        if (n >= 'a')
        {
            n = n & ~32;
        }
        
        if ( (n >= '0' && n <= '9') || (n >= 'A' && n <= 'F'))
        {
            n -= '0';
            if (n > 9)
            {
                return n - HEX_LETTER_OFFSET;
            }
            else
            {
                return n;
            }
            
        }
        else
        {
            throw new NumberFormatException("Invalid hex character " + c);
        }
    }
    
    private static class StringParser
    {
        private String string;
        private int pos;

        public StringParser(String s, int i)
        {
            this.string = s;
            this.pos = i;
        }
        
        public int getPos()
        {
            return pos;
        }
        
        public String parseString(char quoteChar)
        {
            StringBuilder sb = new StringBuilder();
            boolean escape = false;
            int c;
            while ((c = nextChar()) >= 0)
            {
                if (c == quoteChar && !escape)
                {
                    return sb.toString();
                }

                if (c == '\\')
                {
                    if (escape)
                    {
                        sb.append('\\');
                    }
                    escape = !escape;
                }
                else if (escape)
                {
                    switch((char)c)
                    {
                        case '\'':
                        case '"':
                        case '/':
                            sb.append((char)c);
                            break;
                        case 'b':
                            sb.append('\b');
                            break;
                        case 'f':
                            sb.append('\f');
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        case 'u':
                            int unicode = (hexValue((char)nextChar()) << 12) + (hexValue((char)nextChar()) << 8) + (hexValue((char)nextChar()) << 4) + hexValue((char)nextChar()); 
                            sb.append((char)unicode);
                            break;
                        default:
                            throw new JSONParseException("Illegal escape character "+c+" / "+Integer.toHexString(c));
                    }
                    escape = false;
                }
                else
                {
                    if (Character.isISOControl(c))
                    {
                        throw new JSONParseException("Illegal control character 0x"+Integer.toHexString(c));
                    }
                    sb.append((char)c);
                }
            }
            throw new JSONParseException("Unclosed quotes");
        }

        private int nextChar()
        {
            return string.charAt(pos++);
        }
    }

    private String[] parsePropertyPath(String path)
    {
        // XXX: very simple, regexp based parsing. might need improvement
        return PATH_PATTERN.split(path.replace("]", ""));
    }

    private void writeBackArray(Object rootBean, String[] parts, int curPart, Object newArray)
    {
        // XXX: use inefficient recursive call.. fuck arrays.. 
        StringBuilder writeBackPathBuf = new StringBuilder();
        for (int i = 0 ; i < curPart; i++ )
        {
            if (i > 0)
            {
                writeBackPathBuf.append(".");
            }
            writeBackPathBuf.append(parts[i]);
        }
        setPropertyPath(rootBean, writeBackPathBuf.toString(), newArray);
    }

}
