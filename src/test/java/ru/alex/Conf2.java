package ru.alex;

public class Conf2 {
    private final String name;
    private final int port;
    @BindProperties({"name", "port"})
    public Conf2(String name, int port) {
        this.name = name;
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }
}
