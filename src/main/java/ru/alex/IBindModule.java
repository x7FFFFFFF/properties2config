package ru.alex;

import java.lang.reflect.Parameter;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public interface IBindModule extends Predicate<Parameter> {
    int order();

    Object apply(Properties properties, String name, Parameter parameter);


}
