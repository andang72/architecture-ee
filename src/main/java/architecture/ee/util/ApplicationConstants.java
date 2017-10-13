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

public class ApplicationConstants {

	public static final String DEFAULT_STARTUP_FILENAME = "startup-config.xml";
	
	public static final String SETUP_COMPLETE_PROP_NAME = "setup.complete";
	
	
	public static final String DEFAULT_CHAR_ENCODING = "UTF-8";
	
	/** LOCALE PROPERTY KEY */
    public static final String LOCALE_LANGUAGE_PROP_NAME = "locale.language";
    public static final String LOCALE_COUNTRY_PROP_NAME = "locale.country";
    public static final String LOCALE_CHARACTER_ENCODING_PROP_NAME = "locale.characterEncoding";
    public static final String LOCALE_TIMEZONE_PROP_NAME = "locale.timeZone";

    /** LICENSE PROPERTY KEY */
    public static final String RESOURCE_LICENSE_LOCATION_PROP_NAME = "license.location";

    public static final String SERVICES_CONFIG_PERSISTENCE_JDBC_ENABLED = "services.config.persistence.jdbc.enabled";
    public static final String SERVICES_CONFIG_PERSISTENCE_JDBC_USING_EXTERNAL_SQL = "services.config.persistence.jdbc.usingExternalSql";
    public static final String SERVICES_CONFIG_PERSISTENCE_JDBC_EXTERNAL_SQL_FILEPATH = "services.config.persistence.jdbc.filepath";
}
