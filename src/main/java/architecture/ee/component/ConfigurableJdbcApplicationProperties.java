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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import architecture.ee.service.ApplicationProperties;
import architecture.ee.spring.jdbc.ExtendedJdbcUtils;
import architecture.ee.spring.jdbc.ExtendedJdbcUtils.DB;
import architecture.ee.util.LocaleUtils;
import architecture.ee.util.NumberUtils;
import architecture.ee.util.StringUtils;

public class ConfigurableJdbcApplicationProperties extends JdbcDaoSupport implements ApplicationProperties  {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String SPACE = " ";
	
	private static final String COMMA = ",";
	
	private String localizedProperytableName = "V2_LOCALIZED_PROPERTY";
	
	private String properytableName = "V2_PROPERTY";
	
	private String propertyNameColumnName = "PROPERTY_NAME";
	
	private String propertyValueColumnName = "PROPERTY_VALUE";
		
	private String propertyLocaleColumnName ="LOCALE" ;

	protected final AtomicBoolean initFlag = new AtomicBoolean();
	
	private boolean localized;

	private ConcurrentMap<String, String> properties;

	protected ConfigurableJdbcApplicationProperties(boolean localized) {
		this.localized = localized;
	}

	protected void initDao() throws Exception {
		if (!initFlag.get()) {
			try {
				super.initDao();
				ConcurrentMap<String, String> map = new ConcurrentHashMap<String, String>();
				loadProperties(map);
				properties = map;
				initFlag.set(true);
			} catch (Exception e) {
				logger.error("faile to initialize", e);
			}
		}
	}

	public void setLocalizedProperytableName(String localizedProperytableName) {
		this.localizedProperytableName = localizedProperytableName;
	}

	public void setProperytableName(String properytableName) {
		this.properytableName = properytableName;
	}

	public void setPropertyNameColumnName(String propertyNameColumnName) {
		this.propertyNameColumnName = propertyNameColumnName;
	}

	public void setPropertyValueColumnName(String propertyValueColumnName) {
		this.propertyValueColumnName = propertyValueColumnName;
	}

	public void setPropertyLocaleColumnName(String propertyLocaleColumnName) {
		this.propertyLocaleColumnName = propertyLocaleColumnName;
	}

	@Override
	public boolean getBooleanProperty(String name) {
		return getBooleanProperty(name, false);
	}
	
	public boolean getBooleanProperty(String propertyKey, boolean defaultValue) {
		String value = get(propertyKey);
		if (value != null)
			return Boolean.valueOf(value).booleanValue();
		else
			return defaultValue;
	}

	public int getIntProperty(String property, int defaultValue) {
		return NumberUtils.toInt(get(property), defaultValue);
	}

	public long getLongProperty(String property, long defaultValue) {
		return NumberUtils.toLong(get(property), defaultValue);
	}
	    
	public void clear() {
		throw new UnsupportedOperationException();
	}

	public boolean containsKey(String key) {
		return properties.containsKey(key);
	}

	public boolean containsValue(String value) {
		return properties.containsValue(value);
	}

	public Collection<String> values() {
		return Collections.unmodifiableCollection(properties.values());
	}

	public void putAll(Map m) {
		for (Map.Entry entry : (Set<Map.Entry>) m.entrySet()) {
			put((String) entry.getKey(), (String) entry.getValue());
		}
	}

	public Set<Map.Entry<String, String>> entrySet() {
		return Collections.unmodifiableSet(properties.entrySet());
	}

	public Set<String> keySet() {
		return Collections.unmodifiableSet(properties.keySet());
	}

	public Collection<String> getChildrenNames(String parentKey) {
		Object keys[] = properties.keySet().toArray();
		Collection<String> results = new HashSet<String>();
		String parentKeyDot = (new StringBuilder()).append(parentKey).append(".").toString();
		int i = 0;
		for (int n = keys.length; i < n; i++) {
			String key = (String) keys[i];
			if (!key.startsWith(parentKeyDot) || key.equals(parentKey))
				continue;
			int dotIndex = key.indexOf(".", parentKey.length() + 1);
			if (dotIndex < 1) {
				if (!results.contains(key))
					results.add(key);
			} else {
				String name = (new StringBuilder()).append(parentKey)
						.append(key.substring(parentKey.length(), dotIndex)).toString();
				results.add(name);
			}
		}
		return results;
	}

	public Collection<String> getPropertyNames() {
		return (Collection<String>) new ArrayList<String>(properties.keySet());
	}

	public String getStringProperty(String property, String defaultValue) {

		return StringUtils.defaultString(get(property), defaultValue);
	}

	public boolean containsKey(Object key) {
		return properties.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return properties.containsValue(value);
	}

	public String get(String key) {
		return properties.get(key);
	}

	public boolean isEmpty() {
		return properties.isEmpty();
	}

	public String put(String key, String value) {
		String s;
		if (key == null || value == null)
			throw new NullPointerException((new StringBuilder()).append("Key or value cannot be null. Key=").append(key).append(", value=").append(value).toString());

		if ("".equals(key)) {
			logger.warn("Can not save a blank key: {}",key);
			s = null;
		} else {
			try {
				if (value.equals("") && ExtendedJdbcUtils.extractDB(getDataSource().getConnection()) == DB.ORACLE )
					value = " ";
			} catch (Exception e) {
			}
			if (key.endsWith("."))
				key = key.substring(0, key.length() - 1);
			key = key.trim();
			String oldValue = (String) properties.put(key, value);
			if (oldValue != null) {
				if (!oldValue.equals(value)) {
					updateProperty(key, value);
					HashMap<String, Object> params = new HashMap<String, Object>();
					params.put("PARAM_MODIFIED_OLD_VALUE", oldValue);
					// firePropertyChangeEvent(this,
					// ApplicationPropertyChangeEvent.Type.MODIFIED, (String)
					// key, value, params);
				}
			} else {
				insertProperty(key, value);
				// firePropertyChangeEvent(this,
				// ApplicationPropertyChangeEvent.Type.ADDED, key, value, null);
			}
			// CacheFactory.doClusterTask(new PropertyClusterPutTask(key, value,
			// localized));
			s = oldValue;
		}
		return s;
	}

	public String remove(Object key) {
		String s;

		String value = (String) properties.remove(key);

		if (value != null) {
			ArrayList<String> removedValues = new ArrayList<String>();
			Collection<String> propNames = getPropertyNames();
			String keyDot = (new StringBuilder()).append(key).append(".").toString();

			for (String name : propNames) {
				if (name.startsWith(keyDot))
					removedValues.add(properties.remove(name));
			}
			deleteProperty((String) key);

			// HashMap params = new HashMap();
			// params.put("PARAM_REMOVED_VALUES", removedValues);
			// firePropertyChangeEvent(this, PropertyChangeEvent.Type.REMOVED,
			// (String) key, value, params);
		}

		s = value;
		return s;
	}

	public String get(Object key) {
		return properties.get(key);
	}

	public int size() {
		return properties.size();
	}

	// ------------------------------------------------------------------------------------
	// JDBC Internal Methods :
	// ------------------------------------------------------------------------------------
	
	private void insertProperty(String name, String value) {
		if (getJdbcTemplate() != null) {
			StringBuilder sql = new StringBuilder();
			
			
			sql.append("INSERT INTO ");
			if (localized) {
				String baseKey = StringUtils.substringBeforeLast(name, ".");
				String localeCode = StringUtils.substringAfterLast(name, ".");				
				sql.append(localizedProperytableName).append(SPACE).append("(");
				sql.append(propertyNameColumnName).append(COMMA).append(propertyValueColumnName).append(COMMA).append(propertyLocaleColumnName);
				sql.append(") VALUES ( ?, ?, ? )");				
				getJdbcTemplate().update(
						sql.toString(),
						new SqlParameterValue(Types.VARCHAR, baseKey), 
						new SqlParameterValue(Types.VARCHAR, value), 
						new SqlParameterValue(Types.VARCHAR, localeCode));
			} else {
				sql.append(properytableName).append(SPACE).append("(");
				sql.append(propertyNameColumnName).append(COMMA).append(propertyValueColumnName);
				sql.append(") VALUES ( ?, ? )");		
				getJdbcTemplate().update(
						sql.toString(),
						new SqlParameterValue(Types.VARCHAR, name), 
						new SqlParameterValue(Types.VARCHAR, value));
			}
		}
	}
    		
	private void deleteProperty(String name) {
		if (getJdbcTemplate() != null) {
			StringBuilder sql = new StringBuilder();
			sql.append("DELETE FROM ");
			if (localized) {
				sql.append(propertyNameColumnName).append(" LIKE ?").append(" AND ").append(propertyLocaleColumnName).append("LIKE ?");				
				String baseKey = StringUtils.substringBeforeLast(name, ".");
				String localeCode = StringUtils.substringAfterLast(name, ".");
				
				getJdbcTemplate().update(
						sql.toString(),
						new SqlParameterValue(Types.VARCHAR, (new StringBuilder()).append(baseKey).append("%").toString() ),
						new SqlParameterValue(Types.VARCHAR, (new StringBuilder()).append(localeCode).append("%").toString()));
			} else {
				sql.append(this.properytableName).append(" WHERE ");
				sql.append(propertyNameColumnName).append(" LIKE ?");
				getJdbcTemplate().update(
						sql.toString(),
						new SqlParameterValue(Types.VARCHAR, (new StringBuilder()).append(name).append(".%").toString())
				);
			}
		}
	}
 
	private void updateProperty(String name, String value) {
		if (getJdbcTemplate() != null) {
			StringBuilder sql = new StringBuilder();
			if (localized) {
				String baseKey = StringUtils.substringBeforeLast(name, ".");
				String localeCode = StringUtils.substringAfterLast(name, ".");
				sql.append("UPDATE ").append(localizedProperytableName);
				sql.append(" SET ").append(propertyValueColumnName).append("=?");
				sql.append(" WHERE ").append(propertyNameColumnName).append("=?");	
				sql.append(" AND ").append(propertyLocaleColumnName).append("=?");	
				getJdbcTemplate().update(
						sql.toString(),
						new SqlParameterValue(Types.VARCHAR, value),
						new SqlParameterValue(Types.VARCHAR, baseKey),
						new SqlParameterValue(Types.VARCHAR, localeCode));
			} else {
				sql.append("UPDATE ").append(localizedProperytableName);
				sql.append(" SET ").append(propertyValueColumnName).append("=?");
				sql.append(" WHERE ").append(propertyNameColumnName).append("=?");	
				getJdbcTemplate().update(
						sql.toString(),
						new SqlParameterValue(Types.VARCHAR, value),
						new SqlParameterValue(Types.VARCHAR, name));
			}
		}
	}


	private void loadProperties(Map map) {

		if (getJdbcTemplate() != null) {
			Map rs;
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ");
			if (localized) {
				sql.append(propertyNameColumnName).append(COMMA).append(propertyValueColumnName).append(COMMA).append(propertyLocaleColumnName);
				sql.append(" FROM ").append(localizedProperytableName);
				rs = getJdbcTemplate().query(
						sql.toString(),
						new ResultSetExtractor<Map<String, String>>() {
							public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
								Map<String, String> map = new HashMap<String, String>();
								while (rs.next()) {
									String name = (new StringBuilder()).append(rs.getString(1)).append(".").append(rs.getString(3)).toString();
									String value = rs.getString(2);
									// 오라클인경우에 문제가 발생되기 때문이다.
									if (value.equals(SPACE))
										value = "";
									map.put(name, value);
								}
								return map;
							}
						});
			} else {
				sql.append(propertyNameColumnName).append(COMMA).append(propertyValueColumnName);
				sql.append(" FROM ").append(properytableName);
				rs = getJdbcTemplate().query(
						sql.toString(),
						new ResultSetExtractor<Map<String, String>>() {
							public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {
								Map<String, String> map = new HashMap<String, String>();
								while (rs.next()) {
									String name = rs.getString(1);
									String value = rs.getString(2);
									// 오라클인경우에 문제가 발생되기 때문이다.
									if (value.equals(SPACE))
										value = "";
									map.put(name, value);
								}
								return map;
							}
						});
			}
			map.putAll(rs);
		}
	}

	////////////////////////////////////////////////
	// Localized 함수 추가.
	////////////////////////////////////////////////

	public List<Locale> getLocalesForProperty(String name) {
		ArrayList<Locale> list = new ArrayList<Locale>();
		if (getJdbcTemplate() != null) {
			StringBuilder sql = new StringBuilder();
			List<String> locales = getJdbcTemplate().queryForList(
					sql.toString(),
					String.class,
					new SqlParameterValue(Types.VARCHAR, name));
			for (String localeCode : locales) {
				list.add(LocaleUtils.localeCodeToLocale(localeCode));
			}
		}
		return list;
	}

}
