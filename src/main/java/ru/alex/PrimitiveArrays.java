package ru.alex;

import java.util.function.Function;

public enum PrimitiveArrays implements IPrimitiveArrayBindModule {
    BYTE(strings -> {
        final byte[] bytes = new byte[strings.length];
        for (int i = 0; i < strings.length; i++) {
            bytes[i] = Byte.parseByte(strings[i]);
        }
        return bytes;
    }),
    SHORT(strings -> {
        final short[] bytes = new short[strings.length];
        for (int i = 0; i < strings.length; i++) {
            bytes[i] = Short.parseShort(strings[i]);
        }
        return bytes;
    }),
    INT(strings -> {
        final int[] bytes = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            bytes[i] = Integer.parseInt(strings[i]);
        }
        return bytes;
    }),
    LONG(strings -> {
        final long[] bytes = new long[strings.length];
        for (int i = 0; i < strings.length; i++) {
            bytes[i] = Long.parseLong(strings[i]);
        }
        return bytes;
    }),
    FLOAT(strings -> {
        final float[] bytes = new float[strings.length];
        for (int i = 0; i < strings.length; i++) {
            bytes[i] = Float.parseFloat(strings[i]);
        }
        return bytes;
    }),
    DOUBLE(strings -> {
        final double[] bytes = new double[strings.length];
        for (int i = 0; i < strings.length; i++) {
            bytes[i] = Double.parseDouble(strings[i]);
        }
        return bytes;
    }),
    BOOLEAN(strings -> {
        final boolean[] bytes = new boolean[strings.length];
        for (int i = 0; i < strings.length; i++) {
            bytes[i] = Boolean.parseBoolean(strings[i]);
        }
        return bytes;
    }),
    CHAR(strings -> {
        final char[] bytes = new char[strings.length];
        for (int i = 0; i < strings.length; i++) {
            bytes[i] = strings[i].charAt(0);
        }
        return bytes;
    });


    private final String className;
    private final Function<String[], Object> func;

    PrimitiveArrays(Function<String[], Object> func) {
        this.className = name().toLowerCase();
        this.func = func;
    }

    @Override
    public Object convert(String[] strings) {
        return func.apply(strings);
    }

    @Override
    public String componentType() {
        return className;
    }

}
