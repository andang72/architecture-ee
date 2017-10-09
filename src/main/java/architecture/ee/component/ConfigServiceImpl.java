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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.eventbus.EventBus;

import architecture.ee.exception.ConfigurationError;
import architecture.ee.i18n.CommonLogLocalizer;
import architecture.ee.service.ApplicationProperties;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;
import architecture.ee.util.ApplicationConstants;
import architecture.ee.util.LocaleUtils;
import architecture.ee.util.NumberUtils;
import architecture.ee.util.StringUtils;

public class ConfigServiceImpl implements ConfigService {

	@Autowired(required = true)
	@Qualifier("repository")
	private Repository repository;

	@Autowired(required = true)
	@Qualifier("eventBus")
	private EventBus eventBus;
	
	@Autowired(required = false)
	@Qualifier("dataSource")
	private DataSource dataSource;

	@Autowired(required = false)
	@Qualifier("sqlConfiguration")
	private architecture.ee.jdbc.sqlquery.factory.Configuration sqlConfiguration;
	
	
	
	private Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);

	private State state = State.NONE;

	private ApplicationProperties setupProperties = null;

	private ApplicationProperties properties = null;

	private Locale locale = null;

    private TimeZone timeZone = null;

    private String characterEncoding = null;
    
	public void initialize() {
		state = State.INITIALIZING;
		logger.debug(CommonLogLocalizer.format("003001", "ConfigService",state.name() ));
		boolean isSetDataSource = dataSource != null ? true : false ;
		if( isSetDataSource )
		{
			getApplicationProperties();
		}		
		state = State.INITIALIZED;
		logger.debug(CommonLogLocalizer.format("003001", "ConfigService",state.name() ));
	}

	private ApplicationProperties getApplicationProperties() {
		if (properties == null) {
			this.properties = newApplicationProperties(false);
		}
		return properties == null ? LocalApplicationProperties.EMPTY_APPLICATION_PROPERTIES : properties;
	}

	private ApplicationProperties getSetupProperties() {
		if (setupProperties == null)
			this.setupProperties = repository.getSetupApplicationProperties();
		
		return setupProperties;
	}

	private ApplicationProperties newApplicationProperties(boolean localized) {
		if( isConfigPersistenceJdbcEnabled() ){
			DataSource dataSourceToUse = this.dataSource;
			// 데이터베이스 설정이 완료되지 않았다면 널을 리턴한다.
			if (dataSourceToUse != null) {
				logger.debug(CommonLogLocalizer.getMessage("003014"));
				try {
					JdbcApplicationProperties impl = new JdbcApplicationProperties(localized);
					impl.setSqlConfiguration(sqlConfiguration);
					impl.setEventBus(eventBus);
					//ConfigurableJdbcApplicationProperties impl = new ConfigurableJdbcApplicationProperties(localized);
					
					
					impl.setDataSource(dataSourceToUse);
					impl.afterPropertiesSet();
					
					logger.debug(CommonLogLocalizer.format("003015", StringUtils.collectionToCommaDelimitedString(impl.getPropertyNames()) ) );
					
					return impl;
					
				} catch (Exception e) {
					throw new ConfigurationError(e);
				}
			}			
		}
		return null;
	}

	public boolean isSetupComplete(){
		return getLocalProperty(ApplicationConstants.SETUP_COMPLETE_PROP_NAME, false);
		
	}

	public boolean isConfigPersistenceJdbcEnabled(){
		return getLocalProperty(ApplicationConstants.SERVICES_CONFIG_PERSISTENCE_JDBC_ENABLED, false);
	}
	
	public Locale getLocale() {

		if (this.locale == null) {
			Locale localeToUse = Locale.getDefault();
			String languageToUse = getLocalProperty(ApplicationConstants.LOCALE_LANGUAGE_PROP_NAME, null);
			String countryToUse = getLocalProperty(ApplicationConstants.LOCALE_COUNTRY_PROP_NAME, null);
			if (!StringUtils.isEmpty(languageToUse)) {
				if (StringUtils.isEmpty(countryToUse)) {
					localeToUse = new Locale(languageToUse, "", "");
				} else {
					localeToUse = new Locale(languageToUse, countryToUse, "");
				}
			}

			languageToUse = (String) getApplicationProperties().get(ApplicationConstants.LOCALE_LANGUAGE_PROP_NAME);
			countryToUse = (String) getApplicationProperties().get(ApplicationConstants.LOCALE_COUNTRY_PROP_NAME);

			if (!StringUtils.isEmpty(languageToUse)) {
				if (StringUtils.isEmpty(countryToUse)) {
					localeToUse = new Locale(languageToUse, null, null);
				} else {
					localeToUse = new Locale(languageToUse, countryToUse, null);
				}
			}
			this.locale = localeToUse;
		}
		return locale;
	}
	
	/**
     * 로케일을 지정한다.
     * 
     * @param newLocale
     */
	public void setLocale(Locale newLocale) {
		String country = newLocale.getCountry();
		String language = newLocale.getLanguage();
		setApplicationProperty(ApplicationConstants.LOCALE_COUNTRY_PROP_NAME, country);
		setApplicationProperty(ApplicationConstants.LOCALE_LANGUAGE_PROP_NAME, language);
	}

	/**
	 * @return 문자 인코딩을 리턴한다.
	 */
	public String getCharacterEncoding() {
		if (characterEncoding == null) {
			String encoding = getLocalProperty(ApplicationConstants.LOCALE_CHARACTER_ENCODING_PROP_NAME);
			if (encoding != null)
				characterEncoding = encoding;
			String charEncoding = getApplicationProperty(ApplicationConstants.LOCALE_CHARACTER_ENCODING_PROP_NAME);
			if (charEncoding != null)
				characterEncoding = charEncoding;
			else if (characterEncoding == null)
				characterEncoding = ApplicationConstants.DEFAULT_CHAR_ENCODING;
		}
		return characterEncoding;
	}

	/**
	 * @param characterEncoding
	 * @throws UnsupportedEncodingException
	 */
	public void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException {
		if (!LocaleUtils.isValidCharacterEncoding(characterEncoding)) {
			throw new UnsupportedEncodingException((new StringBuilder()).append("Invalid character encoding: ").append(characterEncoding).toString());
		} else {
			setApplicationProperty(ApplicationConstants.LOCALE_CHARACTER_ENCODING_PROP_NAME, characterEncoding);
			return;
		}
	}

	/**
	 * @return
	 */
	public TimeZone getTimeZone() {
		if (timeZone == null)
			if (properties != null) {
				String timeZoneID = (String) properties.get(ApplicationConstants.LOCALE_TIMEZONE_PROP_NAME);
				if (timeZoneID == null)
					timeZone = TimeZone.getDefault();
				else
					timeZone = TimeZone.getTimeZone(timeZoneID);
			} else {
				return TimeZone.getDefault();
			}
		return timeZone;
	}

	/**
	 * @param newTimeZone
	 */
	public void setTimeZone(TimeZone newTimeZone) {
		String timeZoneId = newTimeZone.getID();
		setApplicationProperty(ApplicationConstants.LOCALE_TIMEZONE_PROP_NAME, timeZoneId);
	}
	
	public String getLocalProperty(String name) {
		return (String) getSetupProperties().get(name);
	}

	public String getLocalProperty(String name, String defaultValue) {
		String value = getLocalProperty(name);
		if (value != null)
			return value;
		else
			return defaultValue;
	}
	
	public int getLocalProperty(String name, int defaultValue) {
		return getSetupProperties().getIntProperty(name, defaultValue);
	}
	
	public boolean getLocalProperty(String name, boolean defaultValue) {
		return getSetupProperties().getBooleanProperty(name, defaultValue);
	}
	

	public List<String> getLocalProperties(String parent) {
		List<String> values = new ArrayList<String>();
		Collection<String> propNames = getSetupProperties().getChildrenNames(parent);
		for (String propName : propNames) {
			String value = getLocalProperty(
					(new StringBuilder()).append(parent).append(".").append(propName).toString());
			if (StringUtils.isNullOrEmpty(value))
				values.add(value);
		}
		return values;
	}

	public void setLocalProperty(String name, String value) {
		getSetupProperties().put(name, value);
	}

	public void setLocalProperties(Map<String, String> propertyMap) {
		getSetupProperties().putAll(propertyMap);
	}

	public void deleteLocalProperty(String name) {
		getSetupProperties().remove(name);
	}

	public String getApplicationProperty(String name) {
		return getApplicationProperties().get(name);
	}

	public String getApplicationProperty(String name, String defaultValue) {		
		String value = (String) getApplicationProperties().get(name);
		if (value != null)
			return value;
		else
			return defaultValue;
	}

	public List<String> getApplicationPropertyNames() {
		return new ArrayList<String>(getApplicationProperties().getPropertyNames());
	}

	public List<String> getApplicationPropertyNames(String parent) {
		return new ArrayList<String>(getApplicationProperties().getChildrenNames(parent));
	}

	public List<String> getApplicationProperties(String parent) {
		Collection<String> propertyNames = getApplicationProperties().getChildrenNames(parent);
		List<String> values = new ArrayList<String>();
		for (String propertyName : propertyNames) {
			String value = getApplicationProperty(propertyName);
			if (value != null)
				values.add(value);
		}
		return values;
	}

	public int getApplicationIntProperty(String name, int defaultValue) {
		String value = getApplicationProperty(name);
		return NumberUtils.toInt(value, defaultValue);
	}

	public boolean getApplicationBooleanProperty(String name) {
		return Boolean.valueOf(getApplicationProperty(name)).booleanValue();
	}

	public boolean getApplicationBooleanProperty(String name, boolean defaultValue) {
		String value = getApplicationProperty(name);
		if (value != null)
			return Boolean.valueOf(value).booleanValue();
		else
			return defaultValue;
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void setApplicationProperty(String name, String value) {
		getApplicationProperties().put(name, value);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void setApplicationProperties(Map<String, String> map) {
		getApplicationProperties().putAll(map);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public void deleteApplicationProperty(String name) {
		getApplicationProperties().remove(name);
	}

	public void registerEventListener(Object listener) {
		if( eventBus != null)
			eventBus.register(listener);
	}

	public void unregisterEventListener(Object listener) {
		if( eventBus != null)
			eventBus.unregister(listener);
	}

}