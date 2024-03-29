package architecture.ee.component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import architecture.ee.component.event.StateChangeEvent;
import architecture.ee.exception.ConfigurationError;
import architecture.ee.i18n.CommonLogLocalizer;
import architecture.ee.i18n.FrameworkLogLocalizer;
import architecture.ee.jdbc.sqlquery.builder.BuilderException;
import architecture.ee.jdbc.sqlquery.builder.xml.XmlSqlSetBuilder;
import architecture.ee.jdbc.sqlquery.factory.SqlQueryFactory;
import architecture.ee.service.ApplicationProperties;
import architecture.ee.service.ConfigService;
import architecture.ee.service.Repository;
import architecture.ee.util.ApplicationConstants;
import architecture.ee.util.LocaleUtils;
import architecture.ee.util.NumberUtils;
import architecture.ee.util.StringUtils;

public class DefaultConfigService implements ConfigService {
	
	@Autowired(required = false)
	private ApplicationEventPublisher applicationEventPublisher;	
	
	@Autowired(required = true)
	@Qualifier("repository")
	private Repository repository;

	@Autowired(required = false)
	@Qualifier("dataSource")
	private DataSource dataSource;

	@Autowired(required = false)
	@Qualifier("sqlQueryFactory")
	private SqlQueryFactory sqlQueryFactory ; 
	
	private Logger logger = LoggerFactory.getLogger(ConfigServiceImpl.class);

	private State state = State.NONE;

	private ApplicationProperties setupProperties = null;

	private ApplicationProperties properties = null;

	private Locale locale = null;

    private TimeZone timeZone = null;

    private String characterEncoding = null;
    
	public DefaultConfigService() { 
	}
	 
	
	public boolean isSetDataSource() {
		boolean isSetDataSource = dataSource != null ? true : false ;
		if( isSetDataSource ) {
			if (dataSource instanceof architecture.ee.jdbc.datasource.FailSafeDummyDataSource ) {
				isSetDataSource = false;
			} 
		} 
		return isSetDataSource;
	}
	
	public boolean isDatabaseInitialized() {
		boolean result = isSetDataSource();
		if(result) {
			String tables = repository.getSetupApplicationProperties().getOrDefault("services.config.tables", "AC_UI_PROPERTY"); 
			try {
				
				JdbcTemplate jt = new JdbcTemplate (dataSource) ;
				StringBuilder sb = new StringBuilder();
				sb.append("SELECT * FROM ").append(tables);
				jt.queryForList(sb.toString());
				result = true;
				//result = ExtendedJdbcUtils.tableExists(dataSource, "tables");
				
			} catch (Exception e) {
				result = false;
				logger.error(e.getMessage(), e);
			} 
			logger.debug("VALIDATE tables '{}' exist : {} ", tables,  result );
		}
		return result;
	}
	
	
	
	public void initialize() {		
		state = State.INITIALIZING;
		logger.info(FrameworkLogLocalizer.format("002001", "ConfigService", state.name() ));
		
		if(logger.isInfoEnabled()) {
			logger.info(FrameworkLogLocalizer.format("002002",  "database", isSetDataSource()  ? "true" : "false" ));
			logger.info(FrameworkLogLocalizer.format("002002",  "persistence", isConfigPersistenceJdbcEnabled() ? "database":"xml" ));
			logger.info(FrameworkLogLocalizer.format("002002",  "external sql", isUsingExternalSql() ? "true":"false" ));
		} 
		if( isSetDataSource() )
		{
			getApplicationProperties();
		}		
		
		State oldState = state;
		state = State.INITIALIZED;
		fireStateChangeEvent(oldState, state); 
		logger.info(FrameworkLogLocalizer.format("002001", "ConfigService",state.name() ));
	}

	private ApplicationProperties getApplicationProperties() {
		if (properties == null) {
			this.properties = newApplicationProperties(false);
		}
		return properties == null ? LocalApplicationProperties.EMPTY_APPLICATION_PROPERTIES : properties;
	}

	private ApplicationProperties getSetupProperties() {
		if (setupProperties == null) {
			this.setupProperties = repository.getSetupApplicationProperties();
			logger.debug(FrameworkLogLocalizer.format("002015",  this.setupProperties.getPropertyNames()  ));
		}
		return setupProperties;
	}

	private ApplicationProperties newApplicationProperties(boolean localized) {
		if( isConfigPersistenceJdbcEnabled() ){
			DataSource dataSourceToUse = this.dataSource;
			// 데이터베이스 설정이 완료되지 않았다면 널을 리턴한다.
			logger.debug("Checking DataSource exist.");
			if (dataSourceToUse != null) {
				if ( !isSetDataSource() ) {
					logger.debug( "DataSource not configed.");
					return null ;
				}
				 
				if(logger.isDebugEnabled()) {
					logger.debug(FrameworkLogLocalizer.getMessage("003014"));					
				}
				try { 
					if(!StringUtils.isNullOrEmpty( getExternalSqlFilepathIfExist() )) {
						buildExternalSql(getExternalSqlFilepathIfExist());
					} 
					
					boolean isExists = isDatabaseInitialized();
					if( !isExists ) {
						return null ;
					}
					
					JdbcApplicationProperties impl = new JdbcApplicationProperties(localized, isUsingExternalSql());
					impl.setSqlConfiguration(sqlQueryFactory.getConfiguration());
					impl.setApplicationEventPublisher(applicationEventPublisher);
					impl.setDataSource(dataSourceToUse);
					impl.afterPropertiesSet(); 
					 
					if(logger.isDebugEnabled())
						logger.debug(CommonLogLocalizer.format("003015", StringUtils.collectionToCommaDelimitedString(impl.getPropertyNames()) ) ); 
					
					return impl; 
				} catch (Exception e) {
					throw new ConfigurationError(e.getMessage(), e);
				}
			}			
		}
		return null;
	}
	
	
	protected void buildExternalSql(String path) throws BuilderException {	
		try {	
			File file = null ;		
			if( StringUtils.startsWithIgnoreCase(path, "file:")){					
				try {
					file = (new FileSystemResourceLoader()).getResource(path).getFile();
				} catch (IOException e) { /* ignore */ }					
			}else{
				file = repository.getFile(path);
			}
			if( file != null && file.exists()) {
				XmlSqlSetBuilder builder = new XmlSqlSetBuilder(new FileInputStream(file), sqlQueryFactory.getConfiguration(), file.toURI().toString(), null);
				builder.parse();
			}
		} catch (IOException e) {
			throw new BuilderException("fail to parse external sql file", e);
		}
	} 

	public boolean isSetupComplete(){
		return getLocalProperty(ApplicationConstants.SETUP_COMPLETE_PROP_NAME, false);
		
	}

	public boolean isConfigPersistenceJdbcEnabled(){
		return getLocalProperty(ApplicationConstants.SERVICES_CONFIG_PERSISTENCE_JDBC_ENABLED, false);
	}
	
	
	public boolean isUsingExternalSql(){
		return getLocalProperty(ApplicationConstants.SERVICES_CONFIG_PERSISTENCE_JDBC_USING_EXTERNAL_SQL, false);
	}
	
	public String getExternalSqlFilepathIfExist(){
		return getLocalProperty(ApplicationConstants.SERVICES_CONFIG_PERSISTENCE_JDBC_EXTERNAL_SQL_FILEPATH, null);
	}
	
	/**
     * Returns the global Locale used by System. A locale specifies language
     * and country codes, and is used for internationalization. The default
     * locale is system dependent - Locale.getDefault().
     *
     * @return the global locale used by System.
     */
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
					localeToUse = new Locale(languageToUse, "", "");
				} else {
					localeToUse = new Locale(languageToUse, countryToUse, "");
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
		this.locale = newLocale;
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
		this.timeZone = newTimeZone;
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
	
	protected void fireStateChangeEvent(State oldState, State newState) {
		StateChangeEvent event = new StateChangeEvent(this, oldState, newState);
 
		if( applicationEventPublisher != null )
			applicationEventPublisher.publishEvent(event);
	}

}
