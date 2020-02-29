package architecture.ee.component.editor;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import architecture.ee.component.editor.DataSourceEditor.JndiDataSourceConfig;
import architecture.ee.component.editor.DataSourceEditor.PooledDataSourceConfig;
import architecture.ee.exception.ConfigurationError;
import architecture.ee.i18n.FrameworkLogLocalizer;
import architecture.ee.service.ApplicationProperties;
import architecture.ee.util.ApplicationConstants;
import architecture.ee.util.StringUtils;

public class DataSourceConfigReader {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private ApplicationProperties config ;
	
	public DataSourceConfigReader(ApplicationProperties config) {
		this.config = config;
	}

	/**
	 * 
	 * @param name
	 * @return
	 * @throws ConfigurationError
	 */
	public Collection<String> getProviderNames(String name){  
		StringBuilder builder = new StringBuilder();
		builder.append(ApplicationConstants.DATABASE_PROP_NAME).append(".").append(name);
		return config.getChildrenNames(builder.toString());
	}
	 
	public boolean hasDataSourceConfig(String name) {
		return getProviderNames(name).size() > 0 ? true : false;
	}
	
	public DataSourceConfig getDataSoruceConfig(String name) {    
		log.debug("lookup datasource config for {}", name );
		for( String provider : getProviderNames(name) ){
			String prefix1 = getPrefix(name);
			if( StringUtils.equals(provider, "jndiDataSourceProvider"))
			{	
				String prefix = getProviderPrefix(name, provider);
				String jndiName = getProperty(prefix, "jiniName"); 
				Assert.hasText(jndiName, FrameworkLogLocalizer.getMessage("003042"));	 
				JndiDataSourceConfig dataSourceConfig = new JndiDataSourceConfig(name); 
				dataSourceConfig.beanName = getProperty( prefix1 , "beanName"); 
				dataSourceConfig.jndiName = jndiName; 
				log.debug("bean name is {}", dataSourceConfig.beanName );
				return dataSourceConfig;
			}else if (StringUtils.equals(provider, "pooledDataSourceProvider")) { 
				PooledDataSourceConfig dataSourceConfig = new PooledDataSourceConfig(name);
				String prefix = getProviderPrefix(name, provider);  
				dataSourceConfig.beanName = getProperty( prefix1, "beanName");  
				dataSourceConfig.driverClassName = getProperty(prefix, "driverClassName");
				dataSourceConfig.url = getProperty(prefix, "url"); 
				dataSourceConfig.username = getProperty(prefix, "username"); 
				dataSourceConfig.password = getProperty(prefix, "password");  
				setProperties(prefix, "connectionProperties", dataSourceConfig.connectionProperties);  
				log.debug("bean name is {}", dataSourceConfig.beanName );
				return dataSourceConfig;
			}
		}
		return null;
	}

	private String getPrefix(String name) {
		return (new StringBuilder()).append(ApplicationConstants.DATABASE_PROP_NAME).append(".").append(name).toString();
	} 
	
	private String getProviderPrefix(String name, String provider) {
		return (new StringBuilder()).append(ApplicationConstants.DATABASE_PROP_NAME).append(".").append(name).append(".").append(provider).toString();
	} 
	
	private String setProperties( String prefix , String propertiesKey , Map<String, String> properties) {
		StringBuilder sb = new StringBuilder(prefix);
		String property = sb.append(".").append(propertiesKey).toString();
		for(String key : config.getChildrenNames( property) ) {
			String value = config.get( property + "." + key );
			properties.put(key, value);
			if(log.isDebugEnabled())
				log.debug(FrameworkLogLocalizer.format("003044", key, value));		
		} 
		return config.get(property);
	}
	
	private String getProperty( String prefix , String name ) {
		StringBuilder sb = new StringBuilder(prefix);
		String property = sb.append(".").append(name).toString();
		return config.get(property);
	}
	
}
