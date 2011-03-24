package perf;


import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;
import org.svenson.JSONParser;

@Ignore
public class PerformanceTest
{
    private static final int NUM_FOOS = 500000;

    private final static char[] ALPHABET = "1234567890abcdefghijklmnopqrstuvwxyzäöüßABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ".toCharArray();
    
    private Random random = new Random(34987539487l);
    
    @Test
    public void test()
    {
        String json = createTestJSON();
        //System.out.println(json);
        
        Foo foo = JSONParser.defaultJSONParser().parse(Foo.class, json);
        
        long start = System.currentTimeMillis();
        foo = JSONParser.defaultJSONParser().parse(Foo.class, json);
        long end = System.currentTimeMillis() - start;
        System.out.println("Parsing: " + end + " millis");

//        JSON.defaultJSON().forValue(foo);
//        start = System.currentTimeMillis();
//        JSON.defaultJSON().forValue(foo);
//        end = System.currentTimeMillis() - start;
//        System.out.println("Generating: " + end + " millis");
        
    }

    private String createTestJSON()
    {
        return "{\"bars\":" + fooList() + "}";
    }

    private String fooList()
    {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        
        for (int i = 0; i < NUM_FOOS; i++)
        {
            if (i > 0)
            {
                sb.append(",\n");
            }
            sb.append(foo());
        }
        
        sb.append(']');
        return sb.toString();
    }

    private String foo()
    {
        return "{\"sProp\":" + randomString() + ", \"iProp\":" + randomInt() + ", \"bProp\":" + randomBoolean() + "}";
    }

    private int randomInt()
    {
        return random.nextInt();
    }

    private boolean randomBoolean()
    {
        return random.nextBoolean();
    }

    private String randomString()
    {
        int length = random.nextInt(10) + 10;
        
        StringBuilder sb = new StringBuilder();
        sb.append('"');
        for (int i=0; i < length; i++)
        {
            sb.append(ALPHABET[ random.nextInt(ALPHABET.length)]);
        }
        sb.append('"');
        
        return sb.toString();
    }

}
