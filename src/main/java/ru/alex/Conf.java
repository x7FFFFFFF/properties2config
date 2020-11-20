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
    private static final Map<Class<?>, Object> defaultValues;

    static {
        Map<Class<?>, Object> map = new IdentityHashMap<>();
        map.put(byte.class, (byte) 0);
        map.put(short.class, (short) 0);
        map.put(int.class, 0);
        map.put(long.class, 0L);
        map.put(float.class, 0.0f);
        map.put(double.class, 0.0);
        map.put(char.class, (char) 0);
        defaultValues = Collections.unmodifiableMap(map);
    }

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

    protected void mergeArraysProps(Properties properties) {
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

    protected static Properties load(InputStream is) throws IOException {
        final Properties properties = new Properties();
        properties.load(is);
        return properties;
    }


    @SuppressWarnings("unchecked")
    public <T> T bind(Class<T> aClass) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        final ConstructorInfo constructorInfo = new ConstructorInfo(aClass);
        final Object[] args = new Object[constructorInfo.parameterCount];
        for (int i = 0; i < constructorInfo.parameterCount; i++) {
            args[i] = newInstance(constructorInfo.parameters[i], constructorInfo.parameterNames[i]);
        }
        return (T) constructorInfo.constructor.newInstance(args);
    }

    protected Object newInstance(Parameter parameter, String prop) throws IllegalAccessException, InvocationTargetException, InstantiationException {

        final Class<?> parameterType = parameter.getType();

        final Optional<IBindModule> bindModule = modules.stream().filter(m -> m.test(parameter)).findFirst();
        if (bindModule.isPresent()) {
            final String value = properties.getProperty(prop);
            if (value == null) {
                return getDefault(parameter, prop);
            }
            return bindModule.get().apply(properties, prop, parameter);
        }
        final ConstructorInfo constructorInfo = new ConstructorInfo(parameterType);
        final Object[] args = new Object[constructorInfo.parameterCount];
        for (int i = 0; i < constructorInfo.parameterCount; i++) {
            args[i] = newInstance(constructorInfo.parameters[i], prop + "." + constructorInfo.parameterNames[i]);
        }
        return constructorInfo.constructor.newInstance(args);
    }

    protected Object getDefault(Parameter parameter, String prop) {
        if (!parameter.isAnnotationPresent(OptProp.class)) {
            throw new IllegalArgumentException("Missing required property " + prop);
        } else {
            return getDefaultValue(parameter);
        }
    }


    protected Object getDefaultValue(Parameter parameter) {
        final Class<?> type = parameter.getType();
        if (!type.isPrimitive()) {
            return null;
        }

        return defaultValues.get(type);
    }

    protected static class ConstructorInfo {
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
