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

public enum TimeOfDay {
    NOON        ( 4000,  4000),
    DAY         (    0, 11999),
    DUSK        (13800, 13800),
    SUN_SET     (12000, 13799),
    MIDNIGHT    (16000, 16000),
    NIGHT       (13800, 21999),
    DAWN        (22000, 22000),
    SUN_RISE    (22000, 23999);
    
    private final long start;
    private final long end;

    private TimeOfDay(long start, long end) {
        this.start = start;
        this.end = end;
    }

    public long getEnd() {
        return end;
    }
    
    public long getStart() {
        return start;
    }

    public static TimeOfDay getTimeOfDay(long time) {
        return getTimeOfDay(time, false);
    }
    
    public static TimeOfDay getTimeOfDay(long time, boolean majorPeriodsOnly) {
        long theTime = time%24000;
        for (TimeOfDay tod : TimeOfDay.values()) {
            if ((!majorPeriodsOnly && theTime == tod.getStart() && theTime == tod.getEnd()) ||
                    (theTime >= tod.getStart() && theTime < tod.getEnd())) {
               return tod;
            }
        }
        
        return null;
    }

    public static TimeOfDay getTimeOfDay(String time) {
        return getTimeOfDay(time, false);
    }
    
    public static boolean isDay(long time) {
        return getTimeOfDay(time, true) == DAY;
    }
    
    public static boolean isNight(long time) {
        return getTimeOfDay(time, true) == NIGHT;
    }
    
    public static TimeOfDay getTimeOfDay(String time, boolean majorPeriodsOnly) {
        if (time.equalsIgnoreCase("dawn")) {
            return getTimeOfDay((6 - 8 + 24) * 1000, majorPeriodsOnly);
        } else if (time.equalsIgnoreCase("sunrise")) {
            return getTimeOfDay((7 - 8 + 24) * 1000, majorPeriodsOnly);
        } else if (time.equalsIgnoreCase("morning")) {
            return getTimeOfDay((8 - 8 + 24) * 1000, majorPeriodsOnly);
        } else if (time.equalsIgnoreCase("day")) {
            return getTimeOfDay((8 - 8 + 24) * 1000, majorPeriodsOnly);
        } else if (time.equalsIgnoreCase("midday") || time.equalsIgnoreCase("noon")) {
            return getTimeOfDay((12 - 8 + 24) * 1000, majorPeriodsOnly);
        } else if (time.equalsIgnoreCase("afternoon")) {
            return getTimeOfDay((14 - 8 + 24) * 1000, majorPeriodsOnly);
        } else if (time.equalsIgnoreCase("evening")) {
            return getTimeOfDay((16 - 8 + 24) * 1000, majorPeriodsOnly);
        } else if (time.equalsIgnoreCase("sunset")) {
            return getTimeOfDay((21 - 8 + 24) * 1000, majorPeriodsOnly);
        } else if (time.equalsIgnoreCase("dusk")) {
            return getTimeOfDay((21 - 8 + 24) * 1000 + (int) (30 / 60.0 * 1000), majorPeriodsOnly);
        } else if (time.equalsIgnoreCase("night")) {
            return getTimeOfDay((22 - 8 + 24) * 1000, majorPeriodsOnly);
        } else if (time.equalsIgnoreCase("midnight")) {
            return getTimeOfDay((0 - 8 + 24) * 1000, majorPeriodsOnly);
        } else
            return null;
    }
}
