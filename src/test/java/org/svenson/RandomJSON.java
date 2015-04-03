package org.svenson;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RandomJSON
{
    private static Random random = new Random(0l);

    private String memberName(int i)
    {
        return "member"+i;
    }
    
    private double complexObjectProbality(int depth)
    {
        return 0.05/depth*depth;
    }

    private Object createRandomObject(int depth, int memberCount, double rnd)
    {
        Object value;
        if (rnd < complexObjectProbality(depth))
        {
            if (random.nextBoolean())
            {
                Map m = new HashMap();
                for (int i = 0 ; i < memberCount; i++)
                {
                    m.put(memberName(i), createRandomObject(depth + 1 , memberCount / 2, random.nextDouble()));
                }
                value = m;
            }
            else
            {
                List l = new ArrayList(memberCount);
                for (int i = 0 ; i < memberCount; i++)
                {
                    l.add( createRandomObject(depth + 1 , memberCount / 2, random.nextDouble()));
                }
                value = l;
            }
        }
        else
        {
            rnd = random.nextDouble();
            
            if (rnd < .75 )
            {
                int len = (int)Math.sqrt(memberCount);
                StringBuilder sb = new StringBuilder(len);
                for (int i=0; i < len ; i++)
                {
                    char c;
                    do
                    {
                        c = (char) random.nextInt(65536);
                    } while (Character.isISOControl(c));
                    sb.append(c);
                }
                    
                value = sb.toString();
            }
            else if (rnd < .9)
            {
                value = random.nextLong();
            }
            else
            {
                switch(random.nextInt(3))
                {
                    default:
                    case 0:
                        value = true;
                        break;
                    case 1:
                        value = false;
                        break;
                    case 2:
                        value = null;
                        break;
                }
            }
            
        }
        return value;
    }
    
    public static void main(String[] args)
    {
        try
        {
            new RandomJSON().main();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void main() throws IOException
    {
        Object o = createRandomObject(1, 150, 0.0);
        
        String json = JSON.defaultJSON().forValue(o);
        
        FileWriter fw = new FileWriter("/home/shelmberger/test.json");
        fw.write(json);
        fw.write("\n");
        fw.close();
        
    }
}
