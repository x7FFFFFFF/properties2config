package ru.alex;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.*;
import java.util.stream.Stream;

public enum DefaultBindModules implements IBindModule {
    STRING {
        @Override
        public boolean test(Parameter parameter) {
            return CharSequence.class.isAssignableFrom(parameter.getType());
        }

        @Override
        public Object apply(Properties properties, String name, Parameter parameter) {
            return properties.getProperty(name);
        }

        @Override
        public int order() {
            return 0;
        }
    },

    STR_ARRAY {
        @Override
        public boolean test(Parameter parameter) {
            Class<?> aClass = parameter.getType();
            return aClass.isArray() && CharSequence.class.isAssignableFrom(aClass.getComponentType());
        }

        @Override
        public Object apply(Properties properties, String name, Parameter parameter) {
            return properties.getProperty(name).split(",");
        }

        @Override
        public int order() {
            return 0;
        }
    },
    ENUM {
        @Override
        public boolean test(Parameter parameter) {
            return parameter.getType().isEnum();
        }

        @Override
        public Object apply(Properties properties, String name, Parameter parameter) {
            Class<?> clz = parameter.getType();
            final Object[] enumConstants = clz.getEnumConstants();
            final String property = properties.getProperty(name).toLowerCase();
            return Stream.of(clz.getEnumConstants()).filter(e -> e.toString().toLowerCase()
                    .equals(property)).findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown enum constant " + property));
        }

        @Override
        public int order() {
            return 0;
        }
    },
    STRING_COLLECTION {
        @Override
        public boolean test(Parameter parameter) {
            Class<?> aClass = parameter.getType();
            final Type parameterizedType = parameter.getParameterizedType();
            return Collection.class.isAssignableFrom(aClass) && parameterizedType instanceof ParameterizedType
                    && ((ParameterizedType) parameterizedType).getActualTypeArguments().length == 1
                    && ((ParameterizedType) parameterizedType).getActualTypeArguments()[0] == String.class;
        }

        @Override
        public int order() {
            return 0;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object apply(Properties properties, String name, Parameter parameter) {
            try {
                final Collection<String> collection = getCollection(parameter);
                collection.addAll(Arrays.asList(properties.getProperty(name).split(",")));
                return collection;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }


        @SuppressWarnings("unchecked")
        private Collection<String> getCollection(Parameter parameter) throws InstantiationException, IllegalAccessException {
            final Class<?> type = parameter.getType();
            if (List.class.isAssignableFrom(type)) {
                return new ArrayList<>();
            } else if (Set.class.isAssignableFrom(type)) {
                return new HashSet<>();
            } else if (Queue.class.isAssignableFrom(type)) {
                return new ArrayDeque<>();
            } else if (!type.isInterface()) {
                return (Collection<String>) type.newInstance();
            } else {
                throw new UnsupportedOperationException("Unsupported collection type " + parameter);
            }
        }
    };

    public static List<IBindModule> get() {
        final List<IBindModule> list = new ArrayList<>(Arrays.asList(values()));
        list.addAll(new ArrayList<>(Arrays.asList(Primitives.values())));
        list.addAll(new ArrayList<>(Arrays.asList(PrimitiveArrays.values())));
        list.sort(Comparator.comparingInt(IBindModule::order));
        return Collections.unmodifiableList(list);
    }
}
