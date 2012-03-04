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
package com.iminurnetz.util;


public class Version implements Comparable<Version> {
    private final String[] parts;

    public Version() {
        parts = new String[0];
    }

    public Version(String versionString) {
        String clean = versionString.replaceAll("[^.\\d].*", "");
        parts = clean.split("\\.");
    }

    public boolean isEarlierVersion(Version v) {
        return compareTo(v) == -1;
    }

    public boolean isLaterVersion(Version v) {
        return compareTo(v) == 1;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Version) {
            return compareTo((Version) o) == 0;
        }

        return false;
    }

    @Override
    public String toString() {
        return new String(StringUtils.join(".", parts));
    }

    @Override
    public int compareTo(Version other) {
        int i = 0;
        while (i < this.parts.length && i < other.parts.length && this.parts[i].equals(other.parts[i])) {
            i++;
        }

        if (i < this.parts.length && i < other.parts.length) {
            int diff = new Integer(this.parts[i]).compareTo(new Integer(other.parts[i]));
            return diff < 0 ? -1 : diff == 0 ? 0 : 1;
        }

        return this.parts.length < other.parts.length ? -1 : this.parts.length == other.parts.length ? 0 : 1;
    }
}
