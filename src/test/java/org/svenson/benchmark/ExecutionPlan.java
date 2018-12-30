package org.svenson.benchmark;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.JsonAdapter;
import org.openjdk.jmh.annotations.*;
import org.svenson.JSON;
import org.svenson.JSONParser;
import org.svenson.JSONProperty;
import org.svenson.converter.DefaultTypeConverterRepository;
import org.svenson.converter.JSONConverter;
import org.svenson.info.JavaObjectSupport;
import org.svenson.info.ObjectSupport;
import org.svenson.info.methodhandle.MethodHandleAccessFactory;
import org.svenson.info.reflectasm.ReflectasmAccessFactory;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@State(Scope.Benchmark)
public class ExecutionPlan {

    public enum Framework {
        SVENSON,
        GSON
    }

    public enum MethodAccess {
        DEFAULT,
        METHOD_HANDLE,
        REFLECTASM
    }

    public enum Scenario {
        PLAIN,
        TYPE_CONVERT
    }

    @Param({
            "GSON",
            "SVENSON"})
    public Framework framework;

    @Param({"1", "2"})
    public int nested;

    @Param({"1", "10"})
    public int items;

    @Param({"PLAIN", "TYPE_CONVERT"})
    public Scenario scenario;

    @Param({"DEFAULT", "METHOD_HANDLE", "REFLECTASM"})
    public MethodAccess methodAccess;


    public Deserializer deserializer;

    public Serializer serializer;

    public String json;
    public Bean dump;


    @Setup(Level.Invocation)
    public void setUp() {
        switch (framework) {
            case SVENSON:
                DefaultTypeConverterRepository typeConverterRepository = new DefaultTypeConverterRepository();
                typeConverterRepository.addTypeConverter(new TimestampTypeConverter());
                ObjectSupport support;

                switch (methodAccess) {
                    case REFLECTASM:
                        support = new JavaObjectSupport(new ReflectasmAccessFactory());
                        break;
                    case METHOD_HANDLE:
                        support = new JavaObjectSupport(new MethodHandleAccessFactory());
                        break;
                    default:
                    case DEFAULT:
                        support = new JavaObjectSupport();
                        break;

                }


                final JSONParser parser = new JSONParser();
                parser.setObjectSupport(support);
                parser.setTypeConverterRepository(typeConverterRepository);
                final JSON generator = new JSON(support, '"');
                generator.setTypeConverterRepository(typeConverterRepository);

                this.deserializer = new Deserializer() {
                    @Override
                    public <T> T read(String json, Class<T> type) {
                        return parser.parse(type, json);
                    }
                };

                this.serializer = new Serializer() {
                    @Override
                    public String dump(Object object) {
                        return generator.forValue(object);
                    }
                };
                break;
            case GSON:
                final Gson gson = new GsonBuilder()
                        .setDateFormat(DateFormat.LONG)
                        .create();

                switch (methodAccess) {
                    case DEFAULT:
                        break;
                    default:
                        throw new IllegalStateException("unsupported method access" + methodAccess);
                }

                this.deserializer = new Deserializer() {
                    @Override
                    public <T> T read(String json, Class<T> type) {
                        return gson.fromJson(json, type);
                    }
                };

                this.serializer = new Serializer() {
                    @Override
                    public String dump(Object object) {
                        return gson.toJson(object);
                    }
                };

                break;
        }


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

            switch (scenario) {
                case TYPE_CONVERT:
                    current.setDate(new Date());
                    break;

                case PLAIN:

            }

            Bean child = new Bean();
            current.setInner(child);
            current = child;
        }

        json = serializer.dump(root);
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

        @JsonAdapter(TimestampAdapter.class)
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
        @JSONConverter(type = TimestampTypeConverter.class)
        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
    }


}
