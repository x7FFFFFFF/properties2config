package ru.alex;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;

import static ru.alex.Conf.load;

public class ConfBuilder {
    private List<IBindModule> modules;
    private boolean overrideWithSystem;
    private final Properties properties;

    public ConfBuilder(Properties properties) {
        this.properties = properties;
    }

    public ConfBuilder(InputStream is) throws IOException {
        this.properties = load(is);
    }

    public ConfBuilder(Reader reader) throws IOException {
        this.properties = load(reader);
    }


    public ConfBuilder setModules(List<IBindModule> modules) {
        this.modules = modules;
        return this;
    }

    public ConfBuilder setOverrideWithSystem(boolean overrideWithSystem) {
        this.overrideWithSystem = overrideWithSystem;
        return this;
    }

    public <T> T create(Class<T> aClass) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        return new Conf(properties, modules != null ? modules : DefaultBindModules.get(), overrideWithSystem).bind(aClass);
    }
}