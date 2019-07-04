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

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public class LocaleUtils {

	
	/**
	 * Converts a locale string like "en", "en_US" or "en_US_win" to a Java
	 * locale object. If the conversion fails, null is returned.
	 *
	 * @param localeCode
	 *            the locale code for a Java locale. See the
	 *            {@link java.util.Locale} class for more details.
	 * @return The Java Locale that matches the locale code, or <tt>null</tt>.
	 */
	public static Locale localeCodeToLocale(String localeCode) {
		Locale locale = null;
		if (localeCode != null) {
			String language = null;
			String country = null;
			String variant = null;
			StringTokenizer tokenizer = new StringTokenizer(localeCode, "_");
			if (tokenizer.hasMoreTokens()) {
				language = tokenizer.nextToken();
				if (tokenizer.hasMoreTokens()) {
					country = tokenizer.nextToken();
					if (tokenizer.hasMoreTokens()) {
						variant = tokenizer.nextToken();
					}
				}
			}
			locale = new Locale(language, ((country != null) ? country : ""), ((variant != null) ? variant : ""));
		}
		return locale;
	}


	static Locale[] getAvailableLocales() {
		Locale locales[] = Locale.getAvailableLocales();
		Arrays.sort(locales, new Comparator<Locale>() {
		    public int compare(Locale locale1, Locale locale2) {
			return locale1.getDisplayName().compareTo(locale2.getDisplayName());
		    }
		});
		
		return locales;
	}

	public static boolean isValidCharacterEncoding(String encoding) {
		boolean valid = true;
		try {
			"".getBytes(encoding);
		} catch (Exception e) {
			valid = false;
		}
		return valid;
	}


	 private static final Map<Locale, String[][]> timeZoneLists = new ConcurrentHashMap<>();
	 
	 // The list of supported timezone ids. The list tries to include all of the relevant
    // time zones for the world without any extraneous zones.
    private static String[] timeZoneIds = new String[]{"GMT",
            "Pacific/Apia",
            "HST",
            "AST",
            "America/Los_Angeles",
            "America/Phoenix",
            "America/Mazatlan",
            "America/Denver",
            "America/Belize",
            "America/Chicago",
            "America/Mexico_City",
            "America/Regina",
            "America/Bogota",
            "America/New_York",
            "America/Indianapolis",
            "America/Halifax",
            "America/Caracas",
            "America/Santiago",
            "America/St_Johns",
            "America/Sao_Paulo",
            "America/Buenos_Aires",
            "America/Godthab",
            "Atlantic/South_Georgia",
            "Atlantic/Azores",
            "Atlantic/Cape_Verde",
            "Africa/Casablanca",
            "Europe/Dublin",
            "Europe/Berlin",
            "Europe/Belgrade",
            "Europe/Paris",
            "Europe/Warsaw",
            "ECT",
            "Europe/Athens",
            "Europe/Bucharest",
            "Africa/Cairo",
            "Africa/Harare",
            "Europe/Helsinki",
            "Asia/Jerusalem",
            "Asia/Baghdad",
            "Asia/Kuwait",
            "Europe/Moscow",
            "Africa/Nairobi",
            "Asia/Tehran",
            "Asia/Muscat",
            "Asia/Baku",
            "Asia/Kabul",
            "Asia/Yekaterinburg",
            "Asia/Karachi",
            "Asia/Calcutta",
            "Asia/Katmandu",
            "Asia/Almaty",
            "Asia/Dhaka",
            "Asia/Colombo",
            "Asia/Rangoon",
            "Asia/Bangkok",
            "Asia/Krasnoyarsk",
            "Asia/Hong_Kong",
            "Asia/Irkutsk",
            "Asia/Kuala_Lumpur",
            "Australia/Perth",
            "Asia/Taipei",
            "Asia/Tokyo",
            "Asia/Seoul",
            "Asia/Yakutsk",
            "Australia/Adelaide",
            "Australia/Darwin",
            "Australia/Brisbane",
            "Australia/Sydney",
            "Pacific/Guam",
            "Australia/Hobart",
            "Asia/Vladivostok",
            "Pacific/Noumea",
            "Pacific/Auckland",
            "Pacific/Fiji",
            "Pacific/Tongatapu"
    };

    // A mapping from the supported timezone ids to friendly english names.
    private static final Map<String, String> nameMap = new HashMap<>();

    static {
        nameMap.put(timeZoneIds[0], "International Date Line West");
        nameMap.put(timeZoneIds[1], "Midway Island, Samoa");
        nameMap.put(timeZoneIds[2], "Hawaii");
        nameMap.put(timeZoneIds[3], "Alaska");
        nameMap.put(timeZoneIds[4], "Pacific Time (US & Canada); Tijuana");
        nameMap.put(timeZoneIds[5], "Arizona");
        nameMap.put(timeZoneIds[6], "Chihuahua, La Pax, Mazatlan");
        nameMap.put(timeZoneIds[7], "Mountain Time (US & Canada)");
        nameMap.put(timeZoneIds[8], "Central America");
        nameMap.put(timeZoneIds[9], "Central Time (US & Canada)");
        nameMap.put(timeZoneIds[10], "Guadalajara, Mexico City, Monterrey");
        nameMap.put(timeZoneIds[11], "Saskatchewan");
        nameMap.put(timeZoneIds[12], "Bogota, Lima, Quito");
        nameMap.put(timeZoneIds[13], "Eastern Time (US & Canada)");
        nameMap.put(timeZoneIds[14], "Indiana (East)");
        nameMap.put(timeZoneIds[15], "Atlantic Time (Canada)");
        nameMap.put(timeZoneIds[16], "Caracas, La Paz");
        nameMap.put(timeZoneIds[17], "Santiago");
        nameMap.put(timeZoneIds[18], "Newfoundland");
        nameMap.put(timeZoneIds[19], "Brasilia");
        nameMap.put(timeZoneIds[20], "Buenos Aires, Georgetown");
        nameMap.put(timeZoneIds[21], "Greenland");
        nameMap.put(timeZoneIds[22], "Mid-Atlantic");
        nameMap.put(timeZoneIds[23], "Azores");
        nameMap.put(timeZoneIds[24], "Cape Verde Is.");
        nameMap.put(timeZoneIds[25], "Casablanca, Monrovia");
        nameMap.put(timeZoneIds[26], "Greenwich Mean Time : Dublin, Edinburgh, Lisbon, London");
        nameMap.put(timeZoneIds[27], "Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna");
        nameMap.put(timeZoneIds[28], "Belgrade, Bratislava, Budapest, Ljubljana, Prague");
        nameMap.put(timeZoneIds[29], "Brussels, Copenhagen, Madrid, Paris");
        nameMap.put(timeZoneIds[30], "Sarajevo, Skopje, Warsaw, Zagreb");
        nameMap.put(timeZoneIds[31], "West Central Africa");
        nameMap.put(timeZoneIds[32], "Athens, Istanbul, Minsk");
        nameMap.put(timeZoneIds[33], "Bucharest");
        nameMap.put(timeZoneIds[34], "Cairo");
        nameMap.put(timeZoneIds[35], "Harare, Pretoria");
        nameMap.put(timeZoneIds[36], "Helsinki, Kyiv, Riga, Sofia, Tallinn, Vilnius");
        nameMap.put(timeZoneIds[37], "Jerusalem");
        nameMap.put(timeZoneIds[38], "Baghdad");
        nameMap.put(timeZoneIds[39], "Kuwait, Riyadh");
        nameMap.put(timeZoneIds[40], "Moscow, St. Petersburg, Volgograd");
        nameMap.put(timeZoneIds[41], "Nairobi");
        nameMap.put(timeZoneIds[42], "Tehran");
        nameMap.put(timeZoneIds[43], "Abu Dhabi, Muscat");
        nameMap.put(timeZoneIds[44], "Baku, Tbilisi, Muscat");
        nameMap.put(timeZoneIds[45], "Kabul");
        nameMap.put(timeZoneIds[46], "Ekaterinburg");
        nameMap.put(timeZoneIds[47], "Islamabad, Karachi, Tashkent");
        nameMap.put(timeZoneIds[48], "Chennai, Kolkata, Mumbai, New Dehli");
        nameMap.put(timeZoneIds[49], "Kathmandu");
        nameMap.put(timeZoneIds[50], "Almaty, Novosibirsk");
        nameMap.put(timeZoneIds[51], "Astana, Dhaka");
        nameMap.put(timeZoneIds[52], "Sri Jayawardenepura");
        nameMap.put(timeZoneIds[53], "Rangoon");
        nameMap.put(timeZoneIds[54], "Bangkok, Hanoi, Jakarta");
        nameMap.put(timeZoneIds[55], "Krasnoyarsk");
        nameMap.put(timeZoneIds[56], "Beijing, Chongqing, Hong Kong, Urumqi");
        nameMap.put(timeZoneIds[57], "Irkutsk, Ulaan Bataar");
        nameMap.put(timeZoneIds[58], "Kuala Lumpur, Singapore");
        nameMap.put(timeZoneIds[59], "Perth");
        nameMap.put(timeZoneIds[60], "Taipei");
        nameMap.put(timeZoneIds[61], "Osaka, Sapporo, Tokyo");
        nameMap.put(timeZoneIds[62], "Seoul");
        nameMap.put(timeZoneIds[63], "Yakutsk");
        nameMap.put(timeZoneIds[64], "Adelaide");
        nameMap.put(timeZoneIds[65], "Darwin");
        nameMap.put(timeZoneIds[66], "Brisbane");
        nameMap.put(timeZoneIds[67], "Canberra, Melbourne, Sydney");
        nameMap.put(timeZoneIds[68], "Guam, Port Moresby");
        nameMap.put(timeZoneIds[69], "Hobart");
        nameMap.put(timeZoneIds[70], "Vladivostok");
        nameMap.put(timeZoneIds[71], "Magadan, Solomon Is., New Caledonia");
        nameMap.put(timeZoneIds[72], "Auckland, Wellington");
        nameMap.put(timeZoneIds[73], "Fiji, Kamchatka, Marshall Is.");
        nameMap.put(timeZoneIds[74], "Nuku'alofa");
    }

    /**
     * Returns a list of all available time zone's as a String [][]. The first
     * entry in each list item is the timeZoneID, and the second is the
     * display name.
     * <p>
     * The list of time zones attempts to be inclusive of all of the worlds
     * zones while being as concise as possible. For "en" language locales
     * the name is a friendly english name. For non-"en" language locales
     * the standard JDK name is used for the given Locale. The GMT+/- time
     * is also included for readability.</p>
     *
     * @return a list of time zones, as a tuple of the zime zone ID, and its
     *         display name.
     */
    public static String[][] getTimeZoneList( Locale jiveLocale ) {
        String[][] timeZoneList = timeZoneLists.get(jiveLocale);
        if (timeZoneList == null) {
            String[] timeZoneIDs = timeZoneIds;
            // Now, create String[][] using the unique zones.
            timeZoneList = new String[timeZoneIDs.length][2];
            for (int i = 0; i < timeZoneList.length; i++) {
                String zoneID = timeZoneIDs[i];
                timeZoneList[i][0] = zoneID;
                timeZoneList[i][1] = getTimeZoneName(zoneID, jiveLocale);
            }

            // Add the new list to the map of locales to lists
            timeZoneLists.put(jiveLocale, timeZoneList);
        }

        return timeZoneList;
    }	
    

    /**
     * Returns the display name for a time zone. The display name is the name
     * specified by the Java TimeZone class for non-"en" locales or a friendly english
     * name for "en", with the addition of the GMT offset
     * for human readability.
     *
     * @param zoneID the time zone to get the name for.
     * @param locale the locale to use.
     * @return the display name for the time zone.
     */
    public static String getTimeZoneName(String zoneID, Locale locale) {
        TimeZone zone = TimeZone.getTimeZone(zoneID);
        StringBuilder buf = new StringBuilder();
        // Add in the GMT part to the name. First, figure out the offset.
        int offset = zone.getRawOffset();
        if (zone.inDaylightTime(new Date()) && zone.useDaylightTime()) {
            offset += (int)ApplicationConstants.HOUR;
        }

        buf.append('(');
        if (offset < 0) {
            buf.append("GMT-");
        }
        else {
            buf.append("GMT+");
        }
        offset = Math.abs(offset);
        int hours = offset / (int)ApplicationConstants.HOUR;
        int minutes = (offset % (int)ApplicationConstants.HOUR) / (int)ApplicationConstants.MINUTE;
        buf.append(hours).append(':');
        if (minutes < 10) {
            buf.append('0').append(minutes);
        }
        else {
            buf.append(minutes);
        }
        buf.append(") ");

        // Use a friendly english timezone name if the locale is en, otherwise use the timezone id
        if ("en".equals(locale.getLanguage())) {
            String name = nameMap.get(zoneID);
            if (name == null) {
                name = zoneID;
            }

            buf.append(name);
        }
        else {
            buf.append(
                    zone.getDisplayName(true, TimeZone.LONG, locale).replace('_', ' ').replace('/',
                            ' '));
        }

        return buf.toString();
    }
}
