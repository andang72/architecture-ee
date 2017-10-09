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
package architecture.ee.component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import architecture.ee.service.VariableMap;

public class VariableMapImpl implements VariableMap {

	private Map<String, String> variables;

	public VariableMapImpl() {
		variables = new HashMap<String, String>();
	}

	public VariableMapImpl(Map<String, String> variables) {
		this.variables = variables;
	}

	public VariableMapImpl(Properties properties) {
		variables = new HashMap<String, String>();
		for (final String name: properties.stringPropertyNames())
			variables.put(name, properties.getProperty(name));
	}

	void append(StringBuffer stringbuffer, String string, int i, int j) {
		if (j < 0)
			j = string.length() - i;
		stringbuffer.ensureCapacity(stringbuffer.length() + j);
		j += i;
		for (int k = i; k < j; k++)
			stringbuffer.append(string.charAt(k));

	}

	public String expand(String expression) {
		return substitute(expression, variables);
	}

	String expand(String string, int start, int end, Map<String, String> map, Map<String, String> map1) throws IllegalArgumentException {
		
		StringBuffer stringbuffer = new StringBuffer();
		
		for (int i = start; i < end; i++) {
			int indexOf = string.indexOf('$', i);
			append(stringbuffer, string, i, indexOf - i);
			if (indexOf == -1)
				break;
			i = indexOf + 1;
			if (i >= end)
				continue;
			switch (string.charAt(i)) {
			case 36: // '$' 0x24 36
				stringbuffer.append('$');
				break;
			case 40: // '(' 0x28 40
			case 123: // '{' 0x7B 123
				char c = string.charAt(i);
				byte byte0 = ((byte) (c != '(' ? 125 : 41));
				int i1 = i + 1;
				int j1 = string.indexOf(byte0, i1);
				if (j1 == -1)
					throw new IllegalArgumentException("unterminated variable reference");
				i = indexOf(string, '$', i1, j1);
				if (i != -1) {
					i1 = i + 1;
					int k1 = 0;
					for (i = i1; i < string.length(); i++) {
						char c2 = string.charAt(i);
						if (c2 == c) {
							k1++;
							continue;
						}
						if (c2 == byte0 && --k1 < 0)
							break;
					}

					String s1;
					if (k1 < 0)
						s1 = expand(string, i1, i, map, map1);
					
				} else {
					i = j1;
				}
				referenceVariable(stringbuffer, string, i1, j1 - i1, map, map1);
				break;

			default:
				char c1 = string.charAt(i);
				if (!Character.isWhitespace(c1))
					referenceVariable(stringbuffer, string, i, 1, map, map1);
				break;
			}
		}

		return stringbuffer.toString();
	}

	String expand(String s, Map<String, String> map, Map<String, String> map1) throws IllegalArgumentException {
		return expand(s, 0, s.length(), map, map1);
	}

	void expandIt(String s, Map<String, String> map) {
		try {
			System.out.println(s + " ---> " + substitute(s, map));
		} catch (Throwable throwable) {
			System.out.println(s + " failed");
			throwable.printStackTrace();
		}
	}

	int indexOf(String s, char c, int i, int j) {
		for (int k = i; k < j; k++)
			if (s.charAt(k) == c)
				return k;

		return -1;
	}

	String recursivelyExpand(String s, Map<String, String> map, Map<String, String> map1) throws IllegalArgumentException {
		if (map1.get(s) != null)
			throw new IllegalArgumentException("Recursive variable: " + s);
		map1.put(s, s);
		String s1 = map.get(s);
		if (s1 == null)
			s1 = "";
		String s2 = expand(s1, map, map1);
		map1.remove(s);
		return s2;
	}

	void referenceVariable(StringBuffer stringbuffer, String str, int i, int j, Map<String, String> map, Map<String, String> map1)
			throws IllegalArgumentException {
		String key = str.substring(i, i + j);
		if (!map.containsKey(key)) {
			throw new IllegalArgumentException("undefined variable: " + key);
		} else {
			String s2 = recursivelyExpand(key, map, map1);
			stringbuffer.append(s2);
			return;
		}
	}

	public String substitute(String string, Map<String, String> map) throws IllegalArgumentException {
		if (string == null){
			return null;
		}else{
			return expand(string, 0, string.length(), map, new HashMap<String, String>());
		}
	}
}
