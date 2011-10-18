package com.iminurnetz.bukkit.plugin;

import java.io.IOException;

import junit.framework.TestCase;

public class BukkitPluginSelfUpdateTest extends TestCase {
    public void testDownloadLatestVersion() throws IOException {
        String version = BukkitPlugin.getLatestVersionFromRepository();
        double v = Double.valueOf(version);
        assertTrue(v > 1.7);
    }
}
