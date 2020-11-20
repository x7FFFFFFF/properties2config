package ru.alex;

import java.lang.reflect.Parameter;
import java.util.Properties;
import java.util.function.Function;

public interface IPrimitiveBindModule extends IBindModule {
    @Override
    default int order() {
        return 0;
    }

    @Override
    default Object apply(Properties properties, String name, Parameter parameter) {
        return convert().apply(properties.getProperty(name));
    }

    @Override
    default boolean test(Parameter parameter) {
        Class<?> aClass = parameter.getType();
        return aClass.isPrimitive() && aClass.getName().equals(className());
    }

    String className();

    Function<String, Object> convert();
}
