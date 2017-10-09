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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class CommonLogLocalizer {
	
	private static Localizer localizer = null;
    
    static {
        try {
        	ResourceBundle bundle = ResourceBundle.getBundle(CommonLogLocalizer.class.getName());
        	localizer =  new Localizer(bundle);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }	

	public static String getMessage(String id) throws MissingResourceException {
		return localizer.getMessage(id);
	}

	public static String getMessage(int id) throws MissingResourceException {
		return localizer.getMessage(id);
	}

	public static String format(String id, Object... args) {
		return localizer.format(id, args);
	}

	public static String format(int id, Object... args) {
		return localizer.format(id, args);
	}
}
