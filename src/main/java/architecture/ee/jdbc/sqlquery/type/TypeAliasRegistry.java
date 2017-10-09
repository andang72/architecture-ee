/*
 * Copyright 2016 donghyuck, son
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package architecture.ee.jdbc.sqlquery.type;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.util.ClassUtils;

/**
 * 
 * IBatis org.apache.ibatis.type.TypeAliasRegistry 의 소스에서 가져옴.
 * 
 * @author donghyuck, son
 *
 */
public class TypeAliasRegistry {
	private final HashMap<String, Class<?>> TYPE_ALIASES = new HashMap<String, Class<?>>();

	public TypeAliasRegistry() {
		registerAlias("string", String.class);
		registerAlias("byte", Byte.class);
		registerAlias("long", Long.class);
		registerAlias("short", Short.class);
		registerAlias("int", Integer.class);
		registerAlias("integer", Integer.class);
		registerAlias("double", Double.class);
		registerAlias("float", Float.class);
		registerAlias("boolean", Boolean.class);

		registerAlias("byte[]", Byte[].class);
		registerAlias("long[]", Long[].class);
		registerAlias("short[]", Short[].class);
		registerAlias("int[]", Integer[].class);
		registerAlias("integer[]", Integer[].class);
		registerAlias("double[]", Double[].class);
		registerAlias("float[]", Float[].class);
		registerAlias("boolean[]", Boolean[].class);

		registerAlias("_byte", byte.class);
		registerAlias("_long", long.class);
		registerAlias("_short", short.class);
		registerAlias("_int", int.class);
		registerAlias("_integer", int.class);
		registerAlias("_double", double.class);
		registerAlias("_float", float.class);
		registerAlias("_boolean", boolean.class);

		registerAlias("_byte[]", byte[].class);
		registerAlias("_long[]", long[].class);
		registerAlias("_short[]", short[].class);
		registerAlias("_int[]", int[].class);
		registerAlias("_integer[]", int[].class);
		registerAlias("_double[]", double[].class);
		registerAlias("_float[]", float[].class);
		registerAlias("_boolean[]", boolean[].class);

		registerAlias("date", Date.class);
		registerAlias("decimal", BigDecimal.class);
		registerAlias("bigdecimal", BigDecimal.class);
		registerAlias("object", Object.class);

		registerAlias("date[]", Date[].class);
		registerAlias("decimal[]", BigDecimal[].class);
		registerAlias("bigdecimal[]", BigDecimal[].class);
		registerAlias("object[]", Object[].class);

		registerAlias("map", Map.class);
		registerAlias("hashmap", HashMap.class);
		registerAlias("list", List.class);
		registerAlias("arraylist", ArrayList.class);
		registerAlias("collection", Collection.class);
		registerAlias("iterator", Iterator.class);

		registerAlias("locale", Locale.class);
		registerAlias("ResultSet", ResultSet.class);
	}

	public void registerAlias(String alias, Class<?> value) {
		assert alias != null;
		String key = alias.toLowerCase();
		if (TYPE_ALIASES.containsKey(key) && !TYPE_ALIASES.get(key).equals(value.getName())
				&& TYPE_ALIASES.get(alias) != null) {
			if (!value.equals(TYPE_ALIASES.get(alias))) {
				//throw new ConfigurationError(L10NUtils.format("002002", alias, TYPE_ALIASES.get(alias).getName()));
			}
		}
		TYPE_ALIASES.put(key, value);
	}

	@SuppressWarnings("unchecked")
	// throws class cast exception as well if types cannot be assigned
	public <T> Class<T> resolveAlias(String className) {
		try {
			if (className == null)
				return null;
			String key = className.toLowerCase();
			Class<T> value;
			if (TYPE_ALIASES.containsKey(key)) {
				value = (Class<T>) TYPE_ALIASES.get(key);
			} else {
				value = (Class<T>) ClassUtils.forName(className, null);
			}
			return value;
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException( className + " not found", e);
		}
	}
}
