package org.svenson.perftest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.svenson.JSON;
import org.svenson.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class PerformanceTest
{
    private static Logger log = LoggerFactory.getLogger(PerformanceTest.class);

    private final static int COUNT = 500000;

    private final static char[] ALPHABET = "1234567890abcdefghijklmnopqrstuvwxyzäöüßABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ"
        .toCharArray();

    private Random random = new Random(23870912735098l);

    @Test
    public void testPerformance()
    {
        JSONParser jsonParser = new JSONParser();
        jsonParser.addTypeHint("[]", Foo.class);

        // warm up
        {
            List<Foo> foos = createFoos(COUNT/8);
            String json = JSON.defaultJSON().forValue(foos);
            List<Foo> newFoos = jsonParser.parse(List.class, json);
        }

        List<Foo> foos = createFoos(COUNT);

        long start = System.currentTimeMillis();
        String json = JSON.defaultJSON().forValue(foos);
        long end = System.currentTimeMillis();

        long start2 = System.currentTimeMillis();
        List<Foo> newFoos = jsonParser.parse(List.class, json);
        long end2 = System.currentTimeMillis();

        report("Generation", end - start);
        report("Parsing", end2 - start2);
    }

    private void report(String op, long duration)
    {
        log.info("{}: {} ops/s", op, (1000.0 * COUNT) / duration);
    }

    private List<Foo> createFoos(int count)
    {
        List<Foo> list = new ArrayList<Foo>();

        for (int i = 0; i < count; i++)
        {
            list.add(randomFoo());
        }

        return list;
    }

    private Foo randomFoo()
    {
        return new Foo(randomString(), randomString(), random.nextInt(), random.nextBoolean());
    }

    private String randomString()
    {
        int length = random.nextInt(10) + 10;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++)
        {
            sb.append(ALPHABET[random.nextInt(ALPHABET.length)]);
        }
        return sb.toString();
    }

}
