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
        assertEquals(0, config.getVal());
        assertEquals("localhost2", config.getServer().getConf2().getName());
        assertEquals(3, config.getList().size());
        assertEquals(3, config.getServer().getList().size());

    }

    @Test
    public void test2() throws Exception {
        System.setProperty("server.port", "12345");
        final Config conf = new Conf(getClass().getResourceAsStream("/app1.properties"), DefaultBindModules.get(), true).bind(Config.class);
        assertEquals(12345, conf.getServer().getPort());

    }


    @Test(expected = IllegalArgumentException.class)
    public void test3() throws Exception {

        System.setProperty("server.port", "12345");
        final ConfigTest conf = new Conf(getClass().getResourceAsStream("/app1.properties"), DefaultBindModules.get(), true)
                .bind(ConfigTest.class);


    }

    @Test()
    public void test4() throws Exception {
        final ConfigTest2 conf = new Conf(getClass().getResourceAsStream("/app1.properties"), DefaultBindModules.get(), true)
                .bind(ConfigTest2.class);
        assertNull(conf.value123);


    }

    static class ConfigTest {
        private final String value123;

        @BindProperties("valueNotExist")
        public ConfigTest(String value123) {
            this.value123 = value123;
        }
    }

    static class ConfigTest2 {
        private final String value123;

        @BindProperties("valueNotExist")
        public ConfigTest2(@OptProp String value123) {
            this.value123 = value123;
        }
    }
}