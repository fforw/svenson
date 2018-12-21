package org.svenson.info;

import java.beans.Introspector;
import java.lang.reflect.Method;

enum JavaBeanMethod {
    ADD {
        @Override
        public boolean matches(Method m) {
            return super.matches(m) && m.getParameterTypes().length == 1;
        }

        @Override
        public Class<?> type(Method m) {
            return m.getParameterTypes()[0];
        }
    },
    SET {
        @Override
        public boolean matches(Method m) {
            return super.matches(m) && m.getParameterTypes().length == 1;
        }

        @Override
        public Class<?> type(Method m) {
            return m.getParameterTypes()[0];
        }
    },
    GET {
        @Override
        public boolean matches(Method m) {
            return super.matches(m) && m.getParameterTypes().length == 0 && !m.getReturnType().equals(void.class);
        }

        @Override
        public Class<?> type(Method m) {
            return m.getReturnType();
        }
    },
    IS {
        @Override
        public boolean matches(Method m) {
            return super.matches(m) && m.getParameterTypes().length == 0 && !m.getReturnType().equals(void.class);
        }

        @Override
        public Class<?> type(Method m) {
            return m.getReturnType();
        }
    };

    private final String prefix;
    private final int length;

    JavaBeanMethod() {
        prefix = name().toLowerCase();
        length = name().length();
    }

    public abstract Class<?> type(Method m);


    public boolean matches(Method m) {
        final String name = m.getName();
        return name.length() > length && name.startsWith(prefix);
    }

    public String toPropertyName(Method m) {
        return Introspector.decapitalize(m.getName().substring(length));
    }

    public String toMethodName(String property) {
        StringBuilder methodName = new StringBuilder();
        methodName.append(prefix)
                .append(property);
        final int firstPropNameLetter = length;
        methodName.setCharAt(firstPropNameLetter, Character.toUpperCase(methodName.charAt(firstPropNameLetter)));
        return methodName.toString();
    }
}
