package ru.alex;

import java.util.function.Function;

public enum Primitives implements IPrimitiveBindModule {
    BYTE(Byte::valueOf),
    SHORT(Short::valueOf),
    INT(Integer::valueOf),
    LONG(Long::valueOf),
    FLOAT(Float::valueOf),
    DOUBLE(Double::valueOf),
    BOOLEAN(Boolean::valueOf),
    CHAR(str -> Character.valueOf(str.charAt(0)));


    private final String className;
    private final Function<String, Object> func;

    Primitives(Function<String, Object> func) {
        this.className = name().toLowerCase();
        this.func = func;
    }

    @Override
    public String className() {
        return className;
    }

    @Override
    public Function<String, Object> convert() {
        return func;
    }
}
