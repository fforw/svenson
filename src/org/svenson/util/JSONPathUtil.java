package org.svenson.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.svenson.DynamicProperties;
import org.svenson.JSONParseException;
import org.svenson.JSONParser;
import org.svenson.JSONTypeHint;
import org.svenson.ObjectFactory;

public class JSONPathUtil
{
    private final static Object GET_VALUE = new Object();
    private static final Pattern PATH_PATTERN = Pattern.compile("[\\[\\.]");

    private List<ObjectFactory<?>> objectFactories = Collections.emptyList();
    
    private boolean grow = true;
    
    /**
     * Sets whether growing on path expressions is allowed, that is the implementation will try
     * to fix missing objects or invalid indizes by creating new objects and increasing the size
     * of arrays.  
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
    
    
    public void setPropertyPath(Object doc, String path, Object value)
    {
        getOrSetPropertyPath(doc,path,value);
    }
    
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
     */
    private Object getOrSetPropertyPath(Object bean, String path, Object value)
    {
        if (bean == null)
        {
            throw new IllegalArgumentException("bean cannot be null");
        }
        
        Object rootBean = bean;
        boolean canGrow = grow && value != GET_VALUE;
        
        try
        {
            String[] parts = PATH_PATTERN.split(path.replace("]", ""));
            Object lastDoc = null;
            PropertyDescriptor lastPD = null;
            
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
                                    type = getTypeHintFromAnnotation(lastPD);
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
                        
                        PropertyDescriptor pd = null;
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
                                    Method wm = lastPD.getWriteMethod();
                                    type = getTypeHintFromAnnotation(lastPD);
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
                        else 
                        {
                            String propertyName = JSONParser.getPropertyNameFromAnnotation(bean, part);
                            if (propertyName == null)
                            {
                                propertyName = part;
                            }
                            pd = PropertyUtils.getPropertyDescriptor(bean, propertyName);
                            if (pd != null && pd.getReadMethod() != null)
                            {
                                Method rm = pd.getReadMethod();
                                child = rm.invoke(bean, (Object[])null);

                                if (child == null)
                                {
                                    if (!canGrow)
                                    {
                                        throw new PropertyPathAccessException(path, bean,  bean + " has no value for property '" + part + "'");
                                    }
                                    Method wm = pd.getWriteMethod();
                                    if (wm == null)
                                    {
                                        throw new InvalidPropertyPathException(path, "No write method for null value");
                                    }
                                    child = createNewObjectOfType(rm.getReturnType(), objectFactories);
                                    wm.invoke(bean, child);
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
                        }
                        
                        lastDoc = bean;
                        bean = child;
                        lastPD = pd;
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
                    return JSONBeanUtil.getProperty(bean, part);
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
                if ( bean instanceof Map)
                {
                    ((Map)bean).put(part, value);
                }
                else
                {
                    part = unquotePart(part);   
                    String propertyName = JSONParser.getPropertyNameFromAnnotation(bean, part);
                    
                    if (propertyName == null)
                    {
                        propertyName = part;
                    }
                    
                    PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(bean, propertyName);
                    boolean written = false;
                    if (pd != null)
                    {
                        Method wm = pd.getWriteMethod();
                        if (wm != null)
                        {
                            wm.invoke(bean, value);
                            written = true;
                        }
                    }
                    
                    if (!written && bean instanceof DynamicProperties)
                    {
                        ((DynamicProperties)bean).setProperty(part, value);
                        written = true;
                    }
                    
                    if (!written)
                    {
                        throw new InvalidPropertyPathException(path, "Cannot set property path");
                    }
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
        catch (InvocationTargetException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (NoSuchMethodException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        catch (InstantiationException e)
        {
            throw ExceptionWrapper.wrap(e);
        }
        return null;
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

    private static Class getTypeHintFromAnnotation(PropertyDescriptor propertyDesc)
    {
        Method rm = propertyDesc.getReadMethod();
        Method wm = propertyDesc.getWriteMethod();
        
        JSONTypeHint typeAnno = null;
        if (rm != null)
        {
            typeAnno = rm.getAnnotation(JSONTypeHint.class);
        }
        if (typeAnno == null && wm != null)
        {
            typeAnno = wm.getAnnotation(JSONTypeHint.class);
        }
        if (typeAnno != null)
        {
            return typeAnno.value();
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

}
