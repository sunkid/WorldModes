/**
 * LICENSING
 * 
 * This software is copyright by sunkid <sunkid@iminurnetz.com> and is
 * distributed under a dual license:
 * 
 * Non-Commercial Use:
 *    This program is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Commercial Use:
 *    Please contact sunkid@iminurnetz.com
 */
package com.iminurnetz.bukkit.util;

import com.iminurnetz.util.Version;

public class BukkitVersion implements Comparable<BukkitVersion> {

    private final Version mineCraftVersion;
    private final Version releaseVersion;

    public BukkitVersion() {
        mineCraftVersion = new Version();
        releaseVersion = new Version();
    }

    public BukkitVersion(String versionString) {
        String[] parts = versionString.split("-");
        if (parts.length > 1) {
            mineCraftVersion = new Version(parts[0]);
            parts[1] = parts[1].replaceAll("^R", "");
            releaseVersion = new Version(parts[1]);
        } else if (parts.length > 0) {
            mineCraftVersion = new Version(parts[0]);
            releaseVersion = new Version();
        } else {
            mineCraftVersion = new Version();
            releaseVersion = new Version();
        }
    }

    public boolean isEarlierVersion(BukkitVersion v) {
        return compareTo(v) == -1;
    }

    public boolean isLaterVersion(BukkitVersion v) {
        return compareTo(v) == 1;
    }

    @Override
    public int compareTo(BukkitVersion o) {
        int mvc = mineCraftVersion.compareTo(o.mineCraftVersion);
        return mvc == 0 ? releaseVersion.compareTo(o.releaseVersion) : mvc;
    }

    @Override
    public String toString() {
        String retval = mineCraftVersion.toString();
        if (releaseVersion.equals(new Version())) {
            return retval;
        }

        return retval + "-R" + releaseVersion.toString();
    }
}
