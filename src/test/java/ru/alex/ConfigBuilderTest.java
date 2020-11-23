package ru.alex;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class ConfigBuilderTest {
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

    @Test
    public void test2() throws Exception {
        System.setProperty("server.port", "12345");
        final Config conf = new ConfBuilder(getClass().getResourceAsStream("/app1.properties"))
                .setOverrideWithSystem(true).create(Config.class);
        assertEquals(12345, conf.getServer().getPort());

    }


    @Test(expected = IllegalArgumentException.class)
    public void test3() throws Exception {

        System.setProperty("server.port", "12345");
        final ConfigTest conf = new ConfBuilder(getClass().getResourceAsStream("/app1.properties"))
                .setOverrideWithSystem(true).create(ConfigTest.class);


    }

    @Test()
    public void test4() throws Exception {
        final ConfigTest2 conf = new ConfBuilder(getClass().getResourceAsStream("/app1.properties"))
                .setOverrideWithSystem(true).create(ConfigTest2.class);
        assertNull(conf.value123);


    }


    @Test()
    public void testPrefix() throws Exception {
        final ConfigTest3 conf = new ConfBuilder(getClass().getResourceAsStream("/app1.properties"))
                .setOverrideWithSystem(true).create(ConfigTest3.class);
        assertNotNull(conf.val);
        assertNotNull(conf.configTest4);
        assertEquals("prfixVal2", conf.configTest4.val);

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

    static class ConfigTest3 {
        private final String val;
        private final ConfigTest4 configTest4;

        @BindProperties(value = {"val", "configTest4"}, prefix = "prefix1.prefix2")
        public ConfigTest3(String val, ConfigTest4 configTest4) {
            this.val = val;
            this.configTest4 = configTest4;
        }
    }

    static class ConfigTest4 {
        private final String val;

        @BindProperties(value = "val", prefix = "prefix3.prefix4")
        public ConfigTest4(String val) {
            this.val = val;
        }
    }
}