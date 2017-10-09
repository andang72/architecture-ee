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
package architecture.ee.i18n;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Localizer {

	public static final String VERSION = "version";

	public static final String MESSAGE = "message";

	public static final DecimalFormat decimalformat = new DecimalFormat("000000");

	private ResourceBundle bundle;

	public Localizer(ResourceBundle resourcebundle) {
		this.bundle = resourcebundle;
	}

	public String getVersion(){
		return getString(VERSION);
	}
	public String getString(String key, String id) throws MissingResourceException {
		return bundle.getString(new StringBuilder().append(key).append(id).toString());
	}

	public String getString(String id) {
		return getString("", id);
	}

	public String getMessage(String id) throws MissingResourceException {
		return getString(MESSAGE, id);
	}

	public String getMessage(int id) throws MissingResourceException {
		return getString(MESSAGE, decimalformat.format(id));
	}

	public String format(int id, Object... args) {
		return MessageFormat.format(getMessage(id), args);
	}
	
	public String format(String id, Object... args) {
		return MessageFormat.format(getMessage(id), args);
	}

}
