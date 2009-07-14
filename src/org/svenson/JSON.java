package org.svenson;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.svenson.converter.TypeConverter;
import org.svenson.converter.TypeConverterRepository;
import org.svenson.util.ExceptionWrapper;
import org.svenson.util.TypeConverterCache;

/**
 * Generates <a href="http://json.org">JSON</a> representations of nested java object
 * graphs. The object graphs can contain the following components:
 * <ul>
 * <li>Primitives, Booleans and Strings</li>
 * <li>Javabeans</li>
 * <li>Arrays</li>
 * <li>Collections</li>
 * <li>Maps</li>
 * </ul>
 *
 * @author Sven Helmberger ( sven dot helmberger at gmx dot de )
 * @see JSONable
 * @see JSONifier
 * @see JSONProperty
 * @see DynamicProperties
 * @see AbstractDynamicProperties
 */
public class JSON
{
    private final static JSON defaultJSON = new JSON();

    public static JSON defaultJSON()
    {
        return defaultJSON;
    }

    private Map<Class,JSONifier> jsonifiers=Collections.synchronizedMap(new HashMap<Class, JSONifier>());

    private char quoteChar;

    private boolean escapeUnicodeChars = true;

    private TypeConverterCache typeConverterCache;
    
    public JSON()
    {
        this('"');
    }

    public JSON(char quoteChar)
    {
        setQuoteChar(quoteChar);
    }

    public void setEscapeUnicodeChars(boolean escapeUnicodeChars)
    {
        this.escapeUnicodeChars = escapeUnicodeChars;
    }
    
    public boolean isEscapeUnicodeChars()
    {
        return escapeUnicodeChars;
    }

    public void setTypeConverterRepository(TypeConverterRepository typeConverterRepository)
    {
        this.typeConverterCache = new TypeConverterCache(typeConverterRepository);
    }
    
    public void registerJSONifier(Class c, JSONifier jsonifier)
    {
        jsonifiers.put(c, jsonifier);
    }

    public void deregisterJSONifiers()
    {
        jsonifiers.clear();
    }

       
    /**
     * Dumps the given object as formatted JSON representation. The method dumps
     * the object to JSON first and reformats it then, so it's not the fastest
     * method. It is mainly thought to provide debug output or similar.
     *
     * @param o
     *          Object
     * @return JSON representation
     */
    public String dumpObjectFormatted(Object o)
    {
        StringBuilderSink out = new StringBuilderSink();
        dumpObject(out, o);

        return formatJSON(out.getContent());
    }
    
    public static String formatJSON(String s)
    {
        
        StringBuilder sb = new StringBuilder(s.length() * 3 / 2);
        StringTokenizer st = new StringTokenizer(s, "{}[],\"", true);
        int icnt = 0;
        String lastToken="";
        boolean quoted=false;
        while (st.hasMoreTokens())
        {
            String token = st.nextToken();

            if (token.equals("\""))
            {
                int pos=lastToken.length()-1;
                int cnt=0;
                while (pos >= 0 && lastToken.charAt(pos) == '\\')
                {
                    pos--;
                    cnt++;
                }

                if ((cnt & 1) == 0)
                {
                    quoted=!quoted;
                }
            }

            if (quoted)
            {
                sb.append(token);
            }
            else
            {
                if (token.equals("{") || token.equals("["))
                {
                    icnt++;
                    sb.append(token);
                    newLine(sb, icnt);
                }
                else if (token.equals("}") || token.equals("]"))
                {
                    icnt--;
                    newLine(sb, icnt);
                    sb.append(token);
                }
                else if (token.equals(","))
                {
                    sb.append(token);
                    newLine(sb, icnt);
                }
                else
                {
                    sb.append(token);
                }
            }
            lastToken=token;
        }
        return sb.toString();
    }

    private final static String INDENT = "  ";

    private final static String NEWLINE = System.getProperty("line.separator");

    /**
     * Adds a newline followed by a given number of indentation spaces.
     *
     * @param sb
     *          StringStreamBuilder
     * @param cnt
     *          level of indentation
     */
    private static void newLine(StringBuilder sb, int cnt)
    {
        sb.append(NEWLINE);
        for (int i = 0; i < cnt; i++)
        {
            sb.append(INDENT);
        }
    }

    /**
     * Dumps the given object as JSON representation into the given
     * StreamBuilder
     *
     * @param out
     *          StreamBuilder
     * @param o
     *          objct
     */
    public void dumpObject(JSONCharacterSink out, Object o)
    {
        dumpObject(out, o, '\0', null);
    }

    /**
     * Dumps the given object as JSON representation into the given
     * StreamBuilder
     *
     * @param out
     *          StreamBuilder
     * @param o
     *          objct
     * @param ignoredProps
     *          List containing the properties to be ignored.
     */
    public void dumpObject(JSONCharacterSink out, Object o,
            List<String> ignoredProps)
    {
        dumpObject(out, o, '\0', ignoredProps);
    }

    /**
     * Dumps the given object as JSON representation followed by a separator
     * into the given StreamBuilder.
     *
     * @param out StreamBuilder
     * @param o object
     * @param separator separator character to append after the object or
     *            <code>'\0'</code> to append no separator.
     */
    private void dumpObject(JSONCharacterSink out, Object o, char separator, List<String> ignoredProps)
    {
        if (o == null)
        {
            out.append("null");
        }
        else
        {
            Class oClass = o.getClass();

            JSONifier jsonifier;

            if (oClass.isPrimitive())
            {
                out.append(o);
            }
            else if (Number.class.isAssignableFrom(oClass) || oClass.equals(Boolean.class) ||
                oClass.equals(Character.class))
            {
                out.append(o);
            }
            else if (o instanceof String)
            {
                quote(out, (String) o);
            }
            else if (o instanceof Collection)
            {
                out.append('[');
                for (Iterator i = ((Collection) o).iterator(); i.hasNext();)
                {
                    dumpObject(out, i.next(), i.hasNext() ? ',' : '\0', ignoredProps);
                }
                out.append(']');
            }
            else if (o.getClass().isArray())
            {
                out.append('[');
                int len = Array.getLength(o);
                for (int i = 0; i < len; i++)
                {
                    dumpObject(out, Array.get(o, i), ((i < (len - 1)) ? ',' : '\0'), ignoredProps);
                }
                out.append(']');
            }
            else if (o instanceof Map)
            {
                out.append('{');
                Map m = (Map) o;
                for (Iterator i = m.keySet().iterator(); i.hasNext();)
                {
                    Object key = i.next();

                    dumpObject(out, key.toString(), '\0', ignoredProps);
                    out.append(':');
                    dumpObject(out, m.get(key), i.hasNext() ? ',' : '\0', ignoredProps);
                }
                out.append('}');
            }
            else if ((jsonifier = jsonifiers.get(oClass)) != null)
            {
                out.append(jsonifier.toJSON(o));
            }
            else if (o instanceof JSONable)
            {
                out.append(((JSONable) o).toJSON());
            }
            else if (o instanceof Class)
            {
                quote(out, ((Class)o).getName());
            }
            else if (o instanceof Enum)
            {
                quote(out, ((Enum)o).name());
            }
            else
            {
                BeanInfo info;
                try
                {
                    info = Introspector.getBeanInfo(o.getClass());
                }
                catch (IntrospectionException e)
                {
                    throw ExceptionWrapper.wrap(e);
                }
                out.append('{');
                boolean first = true;
                PropertyDescriptor[] pds = info.getPropertyDescriptors();
                for (int cp = 0; cp < pds.length; cp++)
                {

                    try
                    {

                        PropertyDescriptor pd = pds[cp];
                        Method method = pd.getReadMethod();
                        Method writeMethod = pd.getWriteMethod();
                        if (method != null)
                        {
                            Object value = method.invoke(o, (Object[]) null);
                            
                            if (typeConverterCache != null)
                            {
                                TypeConverter typeConverter = typeConverterCache.getTypeConverter( o, pd.getName());
                                if (typeConverter != null)
                                {
                                    value = typeConverter.toJSON(value);
                                }
                            }
                            
                            String name = pd.getName();
                            boolean ignore = (ignoredProps != null && ignoredProps.contains(name));
                            if (!name.equals("class") && !ignore)
                            {
                                JSONProperty jsonProperty = method
                                    .getAnnotation(JSONProperty.class);
                                if (jsonProperty == null && writeMethod != null)
                                {
                                    jsonProperty = writeMethod.getAnnotation(JSONProperty.class);
                                }
                                if (jsonProperty != null)
                                {
                                    String nameFromAnnotation = jsonProperty.value();
                                    if (nameFromAnnotation.length() > 0)
                                    {
                                        name = jsonProperty.value();
                                    }

                                    ignore = jsonProperty.ignore() ||
                                        (value == null && jsonProperty.ignoreIfNull());
                                }

                                if (!ignore)
                                {
                                    if (!first)
                                    {
                                        out.append(',');
                                    }
                                    quote(out, name);
                                    out.append(':');
                                    dumpObject(out, value, '\0', ignoredProps);
                                    first = false;
                                }
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

                }
                if (o instanceof DynamicProperties)
                {
                    DynamicProperties dynAttrs = (DynamicProperties) o;
                    for (String name : dynAttrs.propertyNames())
                    {
                        if (!first)
                        {
                            out.append(',');
                        }
                        first = false;
                        quote(out, name);
                        out.append(':');
                        dumpObject(out, dynAttrs.getProperty(name), '\0', ignoredProps);
                    }
                }

                out.append('}');
            }
        }
        if (separator != '\0')
        {
            out.append(separator);
        }
    }

    /**
     * Returns a JSON representation of the given object as String.
     *
     * @return JSON representation
     * @param o object
     */
    public String forValue(Object o)
    {
        StringBuilderSink tmp = new StringBuilderSink();
        dumpObject(tmp, o);
        return tmp.getContent();
    }
    
    public void writeJSONToWriter(Object o, Writer w)
    {
        dumpObject(new WriterSink(w),o);
    }

    /**
     * Returns a JSON representation of the given object as String.
     *
     * @param o
     * @param ignoredProps
     * @return
     */
    public String forValue(Object o, List<String> ignoredProps)
    {
        StringBuilderSink tmp = new StringBuilderSink();
        dumpObject(tmp, o, ignoredProps);
        return tmp.getContent();
    }

    /**
     * Inserts the given String as quoted and escaped, JSON-conform String into
     * the given StreamBuilder.
     *
     * @param buf
     *          StreamBuilder
     * @param s
     *          String to quote and escape
     */
    public void quote(JSONCharacterSink buf, String s)
    {
        if (s == null)
        {
            buf.append("null");
            return;
        }

        buf.append(quoteChar);
        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            switch (c)
            {
                
                case '"':
                case '\'':
                    if (c == quoteChar)
                    {
                        buf.append("\\"+c);
                    }
                    else
                    {
                        buf.append(c);
                    }
                    break;
                case '/':
                    buf.append("\\/");
                    break;
                case '\\':
                    buf.append("\\\\");
                    break;
                case '\b':
                    buf.append("\\b");
                    break;
                case '\f':
                    buf.append("\\f");
                    break;
                case '\n':
                    buf.append("\\n");
                    break;
                case '\r':
                    buf.append("\\r");
                    break;
                case '\t':
                    buf.append("\\t");
                    break;
                default:
                    if (c < 32 || (c > 126 && escapeUnicodeChars))
                    {
                        String h = Integer.toHexString(c);
                        int len = h.length();
                        if (len < 4)
                        {
                            h = "0000".substring(len) + h;
                        }
                        buf.append("\\u" + h);
                    }
                    else
                    {
                        buf.append(c);
                    }
                    break;
            }
        }
        buf.append(quoteChar);
    }

    public String quote(String s)
    {
        StringBuilderSink sb=new StringBuilderSink();
        quote(sb,s);
        return sb.getContent();
    }

    public void setQuoteChar(char c)
    {
        if (c != '"' && c != '\'')
        {
            throw new IllegalArgumentException("quote char must be ' or \"");
        }
        
        this.quoteChar = c;
    }
}
