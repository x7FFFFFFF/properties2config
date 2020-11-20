package ru.alex;

import java.util.List;

public class Server {
    private final List<String> list;
    private final String name;
    private final int port;
    private final String[] args;
    private final int[] argsInt;
    private final boolean status;
    private final Conf2 conf2;

    @BindProperties({"list", "name", "port", "args", "argsInt", "status", "conf2"})
    public Server(List<String> list, String name, int port, String[] args, int[] argsInt, boolean status, Conf2 conf2) {
        this.list = list;
        this.name = name;
        this.port = port;
        this.args = args;
        this.argsInt = argsInt;
        this.status = status;
        this.conf2 = conf2;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public String[] getArgs() {
        return args;
    }

    public boolean isStatus() {
        return status;
    }

    public int[] getArgsInt() {
        return argsInt;
    }

    public Conf2 getConf2() {
        return conf2;
    }

    public List<String> getList() {
        return list;
    }
}
