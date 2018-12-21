package org.svenson.benchmark;

import org.openjdk.jmh.annotations.*;
import org.svenson.JSON;
import org.svenson.JSONParser;
import org.svenson.JSONProperty;
import org.svenson.converter.ComplexDateConverter;
import org.svenson.converter.DateConverter;
import org.svenson.converter.DefaultTypeConverterRepository;
import org.svenson.converter.JSONConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@State(Scope.Benchmark)
public class ExecutionPlan {

    public enum Scenario {
        PLAIN,
        TYPE_CONVERT
    }

    @Param({"1", "2"})
    public int nested;

    @Param({"1", "10"})
    public int items;

    @Param({"PLAIN", "TYPE_CONVERT"})
    public Scenario scenario;


    public JSONParser parser;

    public JSON generator;

    public String json;
    public Bean dump;


    @Setup(Level.Invocation)
    public void setUp() {
        DefaultTypeConverterRepository typeConverterRepository = new DefaultTypeConverterRepository();
        typeConverterRepository.addTypeConverter(new DateConverter());
        typeConverterRepository.addTypeConverter(new ComplexDateConverter());
        parser = new JSONParser();
        parser.setTypeConverterRepository(typeConverterRepository);
        generator = JSON.defaultJSON();
        generator.setTypeConverterRepository(typeConverterRepository);

        Bean root = new Bean();

        Bean current = root;
        for (int i = 0; i < nested; ++i) {
            current.setValue(randomString(10));
            current.setValues(new ArrayList<String>(items));
            current.setBeans(new ArrayList<Bean>(items));
            for (int item = 0; item < items; ++item) {
                current.getValues().add(randomString(10));
            }
            for (int item = 0; item < items; ++item) {
                Bean bean = new Bean();
                bean.setValue(randomString(10));
                current.getBeans().add(bean);
            }

            switch (scenario){
                case TYPE_CONVERT:
                    current.setDate(new Date());
                    break;

                case PLAIN:

            }

            Bean child = new Bean();
            current.setInner(child);
            current = child;
        }

        json = JSON.defaultJSON().forValue(root);
        dump = root;

    }

    private String randomString(int length) {
        final String letters = "abcdefg";
        final Random random = new Random();

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            result.append(letters.charAt(random.nextInt(letters.length())));
        }

        return result.toString();
    }


    public static class Bean {
        private String value;
        private List<String> values;
        private List<Bean> beans;
        private Bean inner;

        private Date date;

        @JSONProperty(ignoreIfNull = true)
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @JSONProperty(ignoreIfNull = true)
        public List<String> getValues() {
            return values;
        }

        @JSONProperty(ignoreIfNull = true)
        public List<Bean> getBeans() {
            return beans;
        }

        public void setBeans(List<Bean> beans) {
            this.beans = beans;
        }

        public void setValues(List<String> values) {
            this.values = values;
        }

        @JSONProperty(ignoreIfNull = true)
        public Bean getInner() {
            return inner;
        }

        public void setInner(Bean inner) {
            this.inner = inner;
        }

        @JSONProperty(ignoreIfNull = true)
        @JSONConverter(type = DateConverter.class)
        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }


}
