/**
 *    Copyright 2015-2017 donghyuck
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package architecture.ee.util;

import java.util.Date;

public class NumberUtils {
	public static int toInt(final String str, final int defaultValue) {
		if (str == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(str);
		} catch (final NumberFormatException nfe) {
			return defaultValue;
		}
	}
	
	public static long toLong(final String str, final long defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(str);
        } catch (final NumberFormatException nfe) {
            return defaultValue;
        }
    }
	
	/**
	 * Returns the ISO8601 timestamp of now.
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public static String makeISO8601TimestampString(){
		return makeISO8601TimestampString(System.currentTimeMillis());
	}
	
	/**
	 * Creates an ISO8601 confirm timestamp string in form of YYYY-MM-DDTHH:MM:SS,zzz.
	 *
	 * @param millis time in millis.
	 * @return string that represents the time parameter as iso8601 timestamp.
	 */
	public static String makeISO8601TimestampString(long millis){
		String ret = makeISO8601DateString(millis);
		Date d = new Date(millis);
		ret += 'T' +itoa(d.getHours())+ ':' +itoa(d.getMinutes())+ ':' +itoa(d.getSeconds());
		ret += ',' +itoa((int)(millis-millis/1000*1000),3);
		return ret; 
	}
	
	/**
	 * Returns the ISO8601 confirmant date string for the given time.
	 *
	 * @param millis the time in millis since 01.01.1970.
	 * @return string in form of YYYY-MM-DD.
	 */
	public static String makeISO8601DateString(long millis){
		Date d = new Date(millis);
		return itoa(d.getYear(),4)+ '-' +itoa(d.getMonth())+ '-' +itoa(d.getDay());
	}
	
	/**
	 * Returns the given size in bytes as short string (Kb, Mb, B)...
	 *
	 * @param size the size in bytes
	 * @return the corresponding string
	 */
	public static String makeSizeString(long size){
		String bytes = "B";
		if (size>1024){
			size/=1024;
			bytes = "kB";
		}
		
		if (size>1024){
			size/=1024;
			bytes = "Mb";
		}
		
		if (size>1024){
			size/=1024;
			bytes = "Gb";
		}
		return size + " " + bytes;
			
	}	

	/**
	 * Converts an integer number in a String with given number of chars;
	 * fills in zeros if needed from the left side.
	 * Example: itoa(23, 4) -&gt; 0023.
	 *
	 * @param i a int.
	 * @param limit a int.
	 * @return a {@link java.lang.String} object.
	 */
	public static String itoa(int i, int limit){
		String a = String.valueOf(i);
		while (a.length()<limit)
			a = '0' +a;
		return a;			
	}
	
	/**
	 * Calls itoa(i,2);
	 *
	 * @param i a int.
	 * @return a {@link java.lang.String} object.
	 */
	public static String itoa(int i){
		return itoa(i,2);
	}	
}
