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

import java.util.Properties;

import architecture.ee.component.VariableMapImpl;
import architecture.ee.service.VariableMap;

public class StringUtils extends org.springframework.util.StringUtils {

	public static final int INDEX_NOT_FOUND = -1;
	public static final String EMPTY = "";
	
	public static String defaultString(final String str, final String defaultStr) {
		return str == null ? defaultStr : str;
	}

	public static String expend(String expression, Properties props) {
		VariableMap variables = new VariableMapImpl(props);
		return variables.expand(expression);
	}

	public static boolean isNullOrEmpty(String str) {
		return com.google.common.base.Strings.isNullOrEmpty(str);
	}
	
	public static String substringBeforeLast(final String str, final String separator) {
		if (isEmpty(str) || isEmpty(separator)) {
			return str;
		}
		final int pos = str.lastIndexOf(separator);
		if (pos == INDEX_NOT_FOUND) {
			return str;
		}
		return str.substring(0, pos);
	}
	
	 public static String substringAfterLast(final String str, final String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (isEmpty(separator)) {
			return EMPTY;
		}
		final int pos = str.lastIndexOf(separator);
		if (pos == INDEX_NOT_FOUND || pos == str.length() - separator.length()) {
			return EMPTY;
		}
		return str.substring(pos + separator.length());
	} 
		 
}
