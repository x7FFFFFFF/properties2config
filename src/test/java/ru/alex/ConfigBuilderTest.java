package ru.alex;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigBuilderTest {
    @Test
    public void test() throws Exception {
        System.setProperty("server.port", "12345");
        final Conf configBuilder = new Conf(getClass().getResourceAsStream("/app1.properties"), DefaultBindModules.get(), false);
        final Config config = configBuilder.bind(Config.class);
        final Server server = config.getServer();
        assertEquals("localhost", server.getName());
        assertEquals(5423, server.getPort());
        assertTrue(server.isStatus());
        assertArrayEquals(new int[]{1, 2, 3}, server.getArgsInt());
        assertArrayEquals(new String[]{"abs", "abc", "aaa"}, server.getArgs());
        assertEquals(Config.E.B, config.getE());
        assertEquals(123, config.getVal());
        assertEquals("localhost2", config.getServer().getConf2().getName());
        assertEquals(3, config.getList().size());
        assertEquals(3, config.getServer().getList().size());

    }

    @Test
    public void test2() throws Exception {
        System.setProperty("server.port", "12345");
        final Config conf = new Conf(getClass().getResourceAsStream("/app1.properties"), DefaultBindModules.get(), true).bind(Config.class);
        assertEquals(12345, conf.getServer().getPort());
        assertEquals("server.inst1.port", new Conf.Prop("server").next("inst1").next("port").get());
    }
}