package com.iminurnetz.bukkit.util;

import junit.framework.TestCase;

public class BukkitVersionTest extends TestCase {
    public void testVersion() {
        BukkitVersion v = new BukkitVersion("1.1-R5");
        assertEquals("1.1-R5", v.toString());
        assertTrue(v.isEarlierVersion(new BukkitVersion("1.1-R5.1")));
        assertTrue(v.isEarlierVersion(new BukkitVersion("1.2-R0.1")));
        assertTrue(v.isLaterVersion(new BukkitVersion("1.1-R4")));
        assertTrue(v.isLaterVersion(new BukkitVersion("1.0-R10")));
    }

    public void testLatest() {
        BukkitVersion v = new BukkitVersion("1.2.3-R0.1");
        assertEquals("1.2.3-R0.1", v.toString());
    }
}
