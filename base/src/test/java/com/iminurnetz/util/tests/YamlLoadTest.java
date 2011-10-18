package com.iminurnetz.util.tests;

import java.io.InputStream;

import junit.framework.TestCase;

import org.bukkit.configuration.file.YamlConfiguration;

public class YamlLoadTest extends TestCase {
    public void testConfig() throws Exception {
        YamlConfiguration config = new YamlConfiguration();
        InputStream in = this.getClass().getResource("/config.yml").openStream();
        config.load(in);
        assertTrue(config.getList("Aliases.Item.pickaxe").contains("WOOD_PICKAXE"));
    }
}
