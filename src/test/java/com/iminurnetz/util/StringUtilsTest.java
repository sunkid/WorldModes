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

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.iminurnetz.util.StringUtils;

public class StringUtilsTest extends TestCase {
	public void testConstantCaseToEnglish() {
		assertEquals("basic", StringUtils.constantCaseToEnglish("BASIC"));
		assertEquals("two words", StringUtils.constantCaseToEnglish("TWO_WORDS"));
	}

	public void testToConstantCase() {
		assertEquals("BASIC", StringUtils.toConstantCase("bASic"));
		assertEquals("TWO_WORDS", StringUtils.toConstantCase("Two words"));
		assertEquals("THIS_IS_A_LONGER_ONE", StringUtils.toConstantCase("this is a LoNgEr One"));
	}

	public void testClosestMatch() {
		List<String> candidates = Arrays.asList("exact", "exacting", "similar", "similac", "relax", "relevant interests", "relevant ideas");
		assertEquals(Arrays.asList("similar"), StringUtils.closestMatch("similar", candidates));
		assertEquals(Arrays.asList("exact"), StringUtils.closestMatch("exact", candidates));
		assertEquals(Arrays.asList("relax", "relevant interests", "relevant ideas"), StringUtils.closestMatch("r", candidates));
		assertEquals(Arrays.asList("exact", "exacting"), StringUtils.closestMatch("E", candidates));
		assertEquals(Arrays.asList("exacting"), StringUtils.closestMatch("E*ing", candidates));
		assertEquals(Arrays.asList("relevant interests", "relevant ideas"), StringUtils.closestMatch("RI", candidates));
		assertEquals(Arrays.asList("relevant interests", "relevant ideas"), StringUtils.closestMatch("RI*s", candidates));
		assertEquals(Arrays.asList("relevant interests"), StringUtils.closestMatch("RI*ts", candidates));		
		assertEquals(Arrays.asList("relevant interests"), StringUtils.closestMatch("R*ts", candidates));		
	}
	
	public void testFirstToUpper() {
		assertEquals("Noun", StringUtils.firstToUpper("nOun"));
		assertEquals("123test", StringUtils.firstToUpper("123TEST"));
		assertEquals("A", StringUtils.firstToUpper("a"));
		assertNull(StringUtils.firstToUpper(null));
		assertEquals("", StringUtils.firstToUpper(""));
	}
	
	public void testToCamelCase() {
	    assertEquals("Test", StringUtils.toCamelCase("TeST"));
        assertEquals("BigTest", StringUtils.toCamelCase("BIG_TEST"));
        assertEquals("LittleTest", StringUtils.toCamelCase("little test"));
        assertEquals("MinorTestWithSpaces", StringUtils.toCamelCase("minor_test with spaces"));
	}
}
