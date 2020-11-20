package ru.alex;

import java.lang.reflect.Parameter;
import java.util.Properties;

public interface IPrimitiveArrayBindModule extends IBindModule {
    @Override
    default int order() {
        return 0;
    }

    @Override
    default Object apply(Properties properties, String name, Parameter parameter) {
        return convert(properties.getProperty(name).split(","));
    }

    Object convert(String[] strings);

    @Override
    default boolean test(Parameter parameter) {
        Class<?> aClass = parameter.getType();
        return aClass.isArray() && aClass.getComponentType().getName().equals(componentType());
    }

    String componentType();
}
