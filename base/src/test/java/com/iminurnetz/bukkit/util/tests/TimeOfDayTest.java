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
package com.iminurnetz.bukkit.util.tests;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.iminurnetz.bukkit.util.TimeOfDay;

public class TimeOfDayTest extends TestCase {
    public void testTimeOfDayByString() {
        List<String> times = Arrays.asList("dawn", "sunrise", "morning", "day", "noon", "afternoon", "evening", "sunset",
                "dusk", "night", "midnight");
        
        for (String t : times) {
            TimeOfDay tod = TimeOfDay.getTimeOfDay(t, false);
            switch (tod) {
            case DAWN:
                assertEquals("dawn", t);
                break;
            case SUN_RISE:
                assertEquals("sunrise", t);
                break;
            case DAY:
                assertTrue(t.equals("morning") || t.equals("day") || t.equals("afternoon") || t.equals("evening"));
                break;
            case SUN_SET:
                assertTrue(t.equals("sunset") || t.equals("dusk"));
                break;
            case NIGHT:
                assertEquals("night", t);
                break;
            case MIDNIGHT:
                assertEquals("midnight", t);
                break;
            }
        }
    }
}
