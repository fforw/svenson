package org.svenson.benchmark;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Table {
    private final Set<String> headers = new TreeSet<>();

    private final Map<String, List<Object>> table = new HashMap<>();

    private int rows = 0;

    public void add(String column, Object value) {
        if (table.get(column) == null) {
            table.put(column, new ArrayList<Object>());
        }

        final List<Object> objects = table.get(column);
        objects.add(value);

        rows = Math.max(rows, objects.size());
    }


    public void writeTo(Writer writer) throws IOException {
        writer.write('|');
        for (String header : headers) {
            writer.write(header);
            writer.write('|');
        }
        writer.write('|');
        for (String header : headers) {
            for (char c : header.toCharArray()) {
                writer.write("-");
            }
            writer.write('|');
        }
        writer.write('|');
        for (int i = 0; i < rows; ++i) {

            for (String header : headers) {
                final List<Object> objects = table.get(header);
                if (objects.size() < i) {
                    writer.write(' ');
                }
                final Object o = objects.get(i);
                if (o != null) {
                    writer.write(o.toString());
                } else {
                    writer.write(' ');
                }

                writer.write('|');
            }
        }
    }


}
