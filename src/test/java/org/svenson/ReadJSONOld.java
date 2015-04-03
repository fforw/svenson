package org.svenson;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

public class ReadJSONOld
{
    public static void main(String[] args)
    {
        JSONParser parser = JSONParser.defaultJSONParser();
        
        try
        {
            String json = FileUtils.readFileToString(new File(args[0]));            
            List l = parser.parse(List.class, json);
            System.out.println("Size of array: " + l.size());
            System.out.println("free memory: " + Runtime.getRuntime().freeMemory());
            System.out.println("total memory: " + Runtime.getRuntime().totalMemory());
            System.out.println("max memory: " + Runtime.getRuntime().maxMemory());            
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
