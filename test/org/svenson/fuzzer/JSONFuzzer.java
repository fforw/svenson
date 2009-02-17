package org.svenson.fuzzer;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.goui.util.MTRandom;

import org.junit.Ignore;
import org.junit.Test;
import org.svenson.JSON;

public class JSONFuzzer
{
    private static final int SIMPLE_ELEMENT_MULTIPLIER = 2;
    private static final int COMPLEX_ELEMENT_CUTOFF = 20;

    private Random random = new MTRandom();

    private final static String[] BUILTINS = new String[] { "null", "false", "true" };
    
    public ElementType getRandomType(Random random, int level)
    {
        ElementType[] types = ElementType.values();
        
        int n = random.nextInt( (types.length - 2) * SIMPLE_ELEMENT_MULTIPLIER + 2 );
        
        if (n == 0 && level < COMPLEX_ELEMENT_CUTOFF)
        {
            return ElementType.OBJECT;
        }
        else if ( n == 1 && level < COMPLEX_ELEMENT_CUTOFF)
        {
            return ElementType.ARRAY;
        }
        else
        {
            n-=2;
            if (n < 0)
            {
                n = 0;
            }
            
            return types[ n / SIMPLE_ELEMENT_MULTIPLIER];
        }
    }
    
    public void dumpRandomValue(StringBuilder sb, Random random, ElementType type, int level)
    {
        switch(type)
        {
            case BUILTIN:
                sb.append(BUILTINS[random.nextInt(3)]);
                break;
            case STRING:
                sb.append(JSON.defaultJSON().quote(getRandomString(random, 0, 256)));
                break;
            case NUMBER:
                sb.append(String.valueOf(random.nextLong()));
                break;
            case ARRAY:
            {
                sb.append('[');
                int length = 4 + random.nextInt(6);
                for (int i=0; i < length; i++)
                {
                    if (i != 0)
                    {
                        sb.append(',');
                    }
                    dumpRandomValue(sb,random, getRandomType(random, level + 1), level + 1);
                }
                sb.append(']');
                break;
            }
            case OBJECT:
            {
                sb.append('{');
                Map m = new HashMap();
                int length = 4 + random.nextInt(6);
                for (int i=0; i < length; i++)
                {
                    if (i != 0)
                    {
                        sb.append(',');
                    }
                    sb.append( JSON.defaultJSON().quote(getRandomString(random, 0, 100)));
                    sb.append(':');
                    dumpRandomValue(sb,random, getRandomType(random, level + 1), level + 1);
                }
                sb.append('}');
                break;
            }
            default:
                throw new IllegalStateException("Unknown element type " + this);
        }
    }
    
    private String getRandomString(Random random, int min, int max)
    {
        int length = min + random.nextInt(max-min);
        
        StringBuilder sb = new StringBuilder(length);
        for (int i=0; i < length; i++)
        {
            sb.append((char)random.nextInt(65536));
        }
        return sb.toString();
    }
    
    @Test
    @Ignore
    public void elemProbability()
    {
        final int numberOfDraws = 100000;

        int numberOfTypes = ElementType.values().length;
        int[] counts = new int[numberOfTypes];
        
        MTRandom random = new MTRandom(0);
        
        for (int i=0; i < numberOfDraws ; i++)
        {
            ElementType type = getRandomType(random, 0);
            counts[type.ordinal()]++;
        }
        
        for (int i=0; i < numberOfTypes; i++)
        {
            System.out.println(ElementType.values()[i]+" : "+ (counts[i] * 1.0 / numberOfDraws));
        }
        
    }
    
    @Test
    @Ignore
    public void testRandomValue()
    {
        StringBuilder sb = new StringBuilder();
        dumpRandomValue(sb, new MTRandom(0), ElementType.OBJECT, 0);
        System.out.println(JSON.formatJSON(sb.toString()));
    }

}
