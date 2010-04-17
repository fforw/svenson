package org.svenson.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.svenson.DynamicProperties;
import org.svenson.JSONParseException;
import org.svenson.JSONParser;
import org.svenson.JSONTypeHint;
import org.svenson.ObjectFactory;

/**
 * Contains some util methods to handle bean properties dynamically.
 *
 * @author fforw at gmx dot de
 *
 */
public class JSONBeanUtil
{
    public static final Pattern PATH_PATTERN = Pattern.compile("[\\[\\]\\.]");
    /**
     * Returns the names of all properties of this dynamic properties object including the java bean properties.
     * Note that the method will return the <em>JSON property name</em> of the java bean methods.
     * @param bean     DynamicProperties object
     * @return a set containing all property names, both dynamic and static (JSON) names.
     */
    public static Set<String> getAllPropertyNames(Object bean)
    {

        Set<String> names  = new HashSet<String>( );

        if (bean instanceof DynamicProperties)
        {
            names.addAll(((DynamicProperties)bean).propertyNames());
        }
        if (bean instanceof Map)
        {
            names.addAll(((Map)bean).keySet());
        }
        names.addAll( getBeanPropertyNames(bean));
        return names;
    }

    /**
     * Returns all readable and writable bean property JSON names of the given object.
     * @param bean object
     * @return
     */
    public static Set<String> getBeanPropertyNames(Object bean)
    {
        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(bean.getClass());
        Set<String> names  = new HashSet<String>();
        for (PropertyDescriptor pd : pds)
        {
            Method readMethod = pd.getReadMethod();
            Method writeMethod = pd.getWriteMethod();
            if (readMethod != null && writeMethod != null)
            {
               String name = JSONParser.getJSONPropertyNameFromDescriptor(bean, pd);
               names.add(name);
            }
        }
        return names;
    }

    /**
     * Gets the bean or dynamic property with the given JSON property name. if
     * the class has a bean property with the given name, the value of that
     * property is returned. otherwise, the dynamic property with the given name
     * is returned.
     *
     * @param bean java bean
     * @param name JSON property name
     * @return the property value.
     * @throws IllegalArgumentException if there is no bean property with the
     *             given name on the given dynamicProperties object and the
     *             class of the bean does not implement
     *             {@link DynamicProperties}
     */
    public static Object getProperty(Object bean, String name)
        throws IllegalArgumentException
    {
        try
        {
            String propertyName = JSONParser.getPropertyNameFromAnnotation(bean, name);
            if (propertyName != null && PropertyUtils.isReadable(bean, propertyName))
            {
                return PropertyUtils.getProperty(bean, propertyName);
            }
            else if (bean instanceof DynamicProperties)
            {
                return ((DynamicProperties) bean).getProperty(name);
            }
            else if (bean instanceof Map)
            {
                return ((Map)bean).get(name);
            }
            else
            {
                throw new IllegalArgumentException(bean +
                    " has no JSON property with the name '" + name +
                    "' and does not implements DynamicProperties");
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
    }

    /**
     * XXX: DOESN'T WORK YET
     * 
     * Sets the bean or dynamic property with the given JSON property name to
     * the given value. if the class has a bean property with the given name,
     * the value of that property is overwritten. otherwise, the dynamic
     * property with the given name is overwritten.
     *
     * @param bean bean or dynamic properties instance
     * @param name JSON property name
     * @param value property value
     * @throws IllegalArgumentException if there is no bean property with the
     *             given name on the given dynamicProperties object and the
     *             class of the bean does not implement
     *             {@link DynamicProperties}
     */
    public static void setProperty(Object bean, String name, Object value)
        throws IllegalArgumentException
    {
        try
        {
            String propertyName = JSONParser.getPropertyNameFromAnnotation(bean, name);
            if (propertyName != null && PropertyUtils.isWriteable(bean, propertyName))
            {
                PropertyUtils.setProperty(bean, propertyName, value);
            }
            else if (bean instanceof DynamicProperties)
            {
                ((DynamicProperties) bean).setProperty(name, value);
            }
            else if (bean instanceof Map)
            {
                ((Map)bean).put(name, value);
            }
            else
            {
                throw new IllegalArgumentException(bean +
                    " has no JSON property with the name '" + name +
                    "' and does not implements DynamicProperties");
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
    }
    
    public static void setPropertyPath(Object doc, String path, Object value)
    {
        List<ObjectFactory<?>> emptyList = Collections.emptyList();
        setPropertyPath(doc,path,value, emptyList);
    }
    
    public static void setPropertyPath(Object bean, String path, Object value, List<ObjectFactory<?>> factories)
    {
        if (bean == null)
        {
            throw new IllegalArgumentException("bean cannot be null");
        }
        
        try
        {
            String[] parts = PATH_PATTERN.split(path);
            Object lastDoc = null;
            PropertyDescriptor lastPD = null;
            
            if (parts.length > 1)
            {
                for ( int i=0; i < parts.length - 1; i++)
                {
                    String part = parts[i].trim();
                    if (part.length() == 0)
                    {
                        throw new InvalidPropertyPathException(path, "empty property-path segment");
                    }
                    
                    lastDoc = bean;
                    
                    if (isNumeric(part))
                    {
                        boolean isList = bean instanceof List;
                        boolean isArray = bean.getClass().isArray();
                        
                        if (!isList && !isArray)
                        {
                            throw new InvalidPropertyPathException(path, "numeric index on non-indexable type " + bean);
                        }
                        
                        
                        Object child;
                        int idx  = Integer.parseInt(part);
                        if (isList)
                        {
                            List list = (List)bean;
                            child = list.get(idx);
                            if (child == null)
                            {
                                Class type = null;
                                if (lastPD != null)
                                {
                                    type = getTypeHintFromAnnotation(lastPD);
                                }
                                if (type == null)
                                {
                                    throw new InvalidPropertyPathException(path,"Can't determine type for null value ");
                                }
                                child = createNewObjectOfType(type, factories);

                                setIndexInList(list, idx, child);
                            }
                        }
                        else
                        {
                            child = Array.get(bean, idx);
                            if (child == null)
                            {                                
                                child = createNewObjectOfType(bean.getClass().getComponentType(), factories);
                            }
                            Object newArray = setIndexInList(bean, idx, child);
                            if (newArray != bean)
                            {
                                
                            }
                        }
                        
                        bean = child;
                    }
                    else
                    {
                        part = unquotePart(part);   
                        String propertyName = JSONParser.getPropertyNameFromAnnotation(bean, part);
                        PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(bean, propertyName);
                        Method rm = pd.getReadMethod();
                        
                        Object child = null;
                        if (rm != null)
                        {
                            child = rm.invoke(bean, (Object[])null);
                            
                            if (child == null)
                            {
                                Method wm = pd.getWriteMethod();
                                if (wm == null)
                                {
                                    throw new InvalidPropertyPathException(path, "No write method for null value");
                                }
                                child = createNewObjectOfType(rm.getReturnType(), factories);
                                wm.invoke(bean, child);
                            }
                        }
                        else if (bean instanceof Map)
                        {
                            child = ((Map)bean).get(part);
                            
                            if (child == null)
                            {
                                Class type = null;
                                if (lastPD != null)
                                {
                                    Method wm = lastPD.getWriteMethod();
                                    type = getTypeHintFromAnnotation(lastPD);
                                }
                                else
                                {
                                    if (isNumeric(parts[i+1]))
                                    {
                                        type = ArrayList.class; 
                                    }
                                    else
                                    {
                                        type = HashMap.class; 
                                    }
                                }
                                child = createNewObjectOfType(type, factories);
                                
                                ((Map)bean).put(part, child);
                            }
                            lastPD = null;
                        }
                        else if (bean instanceof DynamicProperties)
                        {
                            child = ((DynamicProperties)bean).getProperty(part);
                            if (child == null)
                            {
                                Class type;
                                if (isNumeric(parts[i+1]))
                                {
                                    type = ArrayList.class; 
                                }
                                else
                                {
                                    type = HashMap.class; 
                                }
                                child = createNewObjectOfType(type, factories);
                                
                                ((DynamicProperties)bean).setProperty(part, child);
                            }
                            lastPD = null;
                        }
                        else
                        {
                            throw new InvalidPropertyPathException(path, "Cannot read property '" + part + "' from " + bean );
                        }
                        
                        
                        bean = child;
                        lastPD = pd;
                    }
                }
            }
            
            String part = unquotePart(parts[parts.length-1]);

            if (isNumeric(part))
            {
                Method writeMethod = lastPD.getWriteMethod();
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
                        bean = newArray;
                        writeMethod.invoke(lastDoc, bean);
                    }
                }
            }
            else
            {
                if ( bean instanceof Map)
                {
                    ((Map)bean).put(part, value);
                }
                else if ( bean instanceof DynamicProperties)
                {
                    ((DynamicProperties)bean).setProperty(part, value);
                }
                else
                {
                    part = unquotePart(part);   
                    String propertyName = JSONParser.getPropertyNameFromAnnotation(bean, part);
                    PropertyDescriptor pd = PropertyUtils.getPropertyDescriptor(bean, propertyName);
                    if (pd == null)
                    {
                        throw new InvalidPropertyPathException(path, "Cannot set property path");
                    }
                    Method wm = pd.getWriteMethod();
                    wm.invoke(bean, value);
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
