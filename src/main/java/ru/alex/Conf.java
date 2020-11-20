package ru.alex;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Conf {
    private final Properties properties;
    private final List<IBindModule> modules;


    public Conf(InputStream is, List<IBindModule> modules, boolean overrideWithSystem) throws IOException {
        this(load(is), modules, overrideWithSystem);
    }

    public Conf(Properties properties, List<IBindModule> modules, boolean overrideWithSystem) {
        this.properties = properties;
        if (overrideWithSystem) {
            this.properties.putAll(System.getProperties());
        }
        mergeArraysProps(properties);
        this.modules = Collections.unmodifiableList(modules);
    }

    private void mergeArraysProps(Properties properties) {
        final Map<String, StringBuilder> map = new HashMap<>();
        for (Object key : new HashSet<>(properties.keySet())) {
            final String keyStr = key.toString();
            final int i = keyStr.indexOf("[");
            if (i != -1) {
                final Object property = properties.remove(keyStr);
                map.compute(keyStr.substring(0, i), (k, old) -> {
                    final StringBuilder builder = old == null ? new StringBuilder() : old;
                    builder.append(property).append(",");
                    return builder;
                });
            }
        }
        map.forEach((k, v) -> {
            v.setLength(v.length() - 1);
            this.properties.put(k, v.toString());
        });
    }

    private static Properties load(InputStream is) throws IOException {
        final Properties properties = new Properties();
        properties.load(is);
        return properties;
    }


    @SuppressWarnings("unchecked")
    public <T> T bind(Class<T> aClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final ConstructorInfo constructorInfo = new ConstructorInfo(aClass);
        final Object[] args = new Object[constructorInfo.parameterCount];
        for (int i = 0; i < constructorInfo.parameterCount; i++) {
            args[i] = newInstance(constructorInfo.parameters[i], new Prop(constructorInfo.parameterNames[i]));
        }
        return (T) constructorInfo.constructor.newInstance(args);
    }

    private Object newInstance(Parameter parameter, Prop prop) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        final Class<?> parameterType = parameter.getType();

        final Optional<IBindModule> bindModule = modules.stream().filter(m -> m.test(parameter)).findFirst();
        if (bindModule.isPresent()) {
            return bindModule.get().apply(properties, prop.get(), parameter);
        }
        final ConstructorInfo constructorInfo = new ConstructorInfo(parameterType);
        final Object[] args = new Object[constructorInfo.parameterCount];
        for (int i = 0; i < constructorInfo.parameterCount; i++) {
            args[i] = newInstance(constructorInfo.parameters[i], prop.next(constructorInfo.parameterNames[i]));
        }
        return constructorInfo.constructor.newInstance(args);
    }

    public static class Prop {
        final String name;
        Prop next;
        final Prop head;


        Prop(String name) {
            this.name = name;
            next = null;
            head = this;
        }

        private Prop(String name, Prop head) {
            this.name = name;
            this.next = null;
            this.head = head;
        }

        Prop next(String name) {
            return (next = new Prop(name, this.head));
        }


        public String get() {
            final StringBuilder builder = new StringBuilder();
            Prop current = head;
            do {
                builder.append(current.name);
                if (current.next != null) {
                    builder.append(".");
                }
                current = current.next;
            } while (current != null);
            return builder.toString();
        }
    }

    private static class ConstructorInfo {
        final Parameter[] parameters;
        final String[] parameterNames;
        final int parameterCount;
        private final Constructor<?> constructor;

        public ConstructorInfo(Class<?> cls) {
            final List<Constructor<?>> constructors = Stream.of(cls.getConstructors()).filter(c -> c.isAnnotationPresent(BindProperties.class)).collect(Collectors.toList());
            if (constructors.size() != 1) {
                throw new IllegalStateException("Must be only one constructors with @BindProperties");
            }
            constructor = constructors.get(0);
            parameterNames = constructor.getAnnotation(BindProperties.class).value();
            parameterCount = constructor.getParameterCount();
            assert parameterNames.length == parameterCount;
            parameters = constructor.getParameters();
        }

    }
}
