package ru.alex;

import java.util.List;

public class Config {
    private final List<String> list;
    private final Server server;
    private final E e;
    private final int val;

    @BindProperties({"list","server", "e", "val"})
    public Config(List<String> list, Server server, E e, @OptProp int val) {
        this.list = list;
        this.server = server;
        this.e = e;
        this.val = val;
    }

    public Server getServer() {
        return server;
    }

    public E getE() {
        return e;
    }

    public int getVal() {
        return val;
    }

    public enum E {
        A,B,C;
    }

    public List<String> getList() {
        return list;
    }
}
