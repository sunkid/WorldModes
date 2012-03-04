package com.iminurnetz.bukkit.plugin;

import java.io.IOException;

import com.iminurnetz.bukkit.util.BukkitVersion;
import com.iminurnetz.util.Version;

import junit.framework.TestCase;

public class BukkitPluginSelfUpdateTest extends TestCase {

    BukkitPlugin p;

    public void setUp() {
        p = new TestPlugin();
    }

    public void testDownloadLatestVersion() throws IOException {
        BukkitPlugin.VersionTuple version = p.getLatestVersionFromRepository();
        assertTrue(version.isLaterVersion(new Version("1.7")));
    }

    public void testCompatibility() throws IOException {
        BukkitPlugin.VersionTuple version = p.getLatestVersionFromRepository();
        assertTrue(version.isBukkitCompatible(new BukkitVersion("1.1-R5")));
    }

    class TestPlugin extends BukkitPlugin {
        @Override
        public void enablePlugin() throws Exception {
        }

        @Override
        public String getName() {
            return "BaseBukkitPlugin";
        }
    }
}
