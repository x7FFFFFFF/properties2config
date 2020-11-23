# properties2config
```
A small library for turning a properties file into an instance of the specified class.
Requirements:
Java 1.8+
Zero dependencies

Usage:
   final Config config = new ConfBuilder(getClass().getResourceAsStream("/app1.properties"))
                .setOverrideWithSystem(false).create(Config.class);
   // where Config.class is the class into which the properties will be injected
Example:
----------- app.properties --------------------
list=aa,bbb, ccc b
server.name=localhost
server.port=5423
server.args=abs,abc,aaa
server.argsInt=1,2,3
server.status=true
server.conf2.name=localhost2
server.conf2.port=1111
server.list[0]=aaa
server.list[1]=bbb
server.list[2]=ccc

------------ Config.java ---------------------
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

------------- Server.java -------------
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

---------------- main code -----------
   @Test
    public void test() throws Exception {
        System.setProperty("server.port", "12345");

        final Config config = new ConfBuilder(getClass().getResourceAsStream("/app1.properties"))
                .setOverrideWithSystem(false).create(Config.class);

        final Server server = config.getServer();
        assertEquals("localhost", server.getName());
        assertEquals(5423, server.getPort());
        assertTrue(server.isStatus());
        assertArrayEquals(new int[]{1, 2, 3}, server.getArgsInt());
        assertArrayEquals(new String[]{"abs", "abc", "aaa"}, server.getArgs());
        assertEquals(Config.E.B, config.getE());
        assertEquals(0, config.getVal());
        assertEquals("localhost2", config.getServer().getConf2().getName());
        assertEquals(3, config.getList().size());
        assertEquals(3, config.getServer().getList().size());
    }

```