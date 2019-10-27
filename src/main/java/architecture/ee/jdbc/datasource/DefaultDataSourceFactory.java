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
package architecture.ee.jdbc.datasource;

import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;

import architecture.ee.component.State;
import architecture.ee.component.editor.DataSourceConfig;
import architecture.ee.component.editor.DataSourceConfigReader;
import architecture.ee.component.editor.DataSourceEditor.JndiDataSourceConfig;
import architecture.ee.component.editor.DataSourceEditor.PooledDataSourceConfig;
import architecture.ee.exception.RuntimeError;
import architecture.ee.i18n.FrameworkLogLocalizer;
import architecture.ee.service.ApplicationProperties;
import architecture.ee.service.Repository;
import architecture.ee.util.ApplicationConstants;

/**
 * startup-config.xml 에 database 
 * @author donghyuck
 *
 */
public class DefaultDataSourceFactory implements DataSourceFactory {
	
	private static final String DBCP_CALSSNAME = "org.apache.commons.dbcp.BasicDataSource";
	
	private static final String DBCP2_CALSSNAME = "org.apache.commons.dbcp2.BasicDataSource";
	
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired( required = true) @Qualifier("repository")
	private Repository repository;
	
	private String profileName ;	
	
	public DataSource getDataSource() {		
		
		ApplicationProperties config = repository.getSetupApplicationProperties(); 
		if(log.isDebugEnabled())
			log.debug(FrameworkLogLocalizer.format("003040", profileName));	
		
		DataSourceConfigReader reader = new DataSourceConfigReader( config ); 
		if( !reader.hasDataSourceConfig(profileName) ) {
			if( config.getBooleanProperty(ApplicationConstants.SETUP_COMPLETE_PROP_NAME, false))
			{
				throw new RuntimeError(FrameworkLogLocalizer.format("003041", profileName));
			}
			if( log.isWarnEnabled()) {
				log.warn(FrameworkLogLocalizer.format("003041", profileName));
				log.warn(FrameworkLogLocalizer.format("003019", profileName));
				
				return new FailSafeDummyDataSource();
			}
		}
		DataSourceConfig dataSourceConfig = reader.getDataSoruceConfig(profileName);
		if( dataSourceConfig.getType() ==  DataSourceConfig.Types.JNDI )
		{
			JndiDataSourceLookup lookup = new JndiDataSourceLookup();
			return lookup.getDataSource(((JndiDataSourceConfig)dataSourceConfig).getJndiName());
		}
		else if (dataSourceConfig.getType() ==  DataSourceConfig.Types.POOLED) {
			
			DataSource dataSourceToUse = null ;
			log.debug(FrameworkLogLocalizer.format("002003", "DataSource", State.CREATING.name()));
		    if(log.isDebugEnabled()) {
		    	log.debug(FrameworkLogLocalizer.format("003043", ((PooledDataSourceConfig)dataSourceConfig).getDriverClassName(), ((PooledDataSourceConfig)dataSourceConfig).getUrl()));
		    }
		    if( dataSourceToUse == null && ClassUtils.isPresent(DBCP2_CALSSNAME, null) ) {
	    		dataSourceToUse = newDbcpDataSource( DBCP2_CALSSNAME, (PooledDataSourceConfig)dataSourceConfig);
	    	}else			    		
	    	if( dataSourceToUse == null && ClassUtils.isPresent(DBCP_CALSSNAME, null) ) {
	    		dataSourceToUse = newDbcpDataSource( DBCP_CALSSNAME, (PooledDataSourceConfig)dataSourceConfig);
	    	}		    		
	    	log.debug(FrameworkLogLocalizer.format("002003", "DataSource",  State.CREATED.name()));		    		
	    	return dataSourceToUse;	 
		}
		    
		/*
		 * 
		 * 
		String profileTag = "database." + profileName ;		
		 * Collection<String> providers = reader.getProviderNames(profileTag); //config.getChildrenNames(profileTag);
		
		if( providers.size() == 0 ) {
			
			if( config.getBooleanProperty(ApplicationConstants.SETUP_COMPLETE_PROP_NAME, false))
			{
				throw new RuntimeError(FrameworkLogLocalizer.format("003041", profileName));
			}
			if( log.isWarnEnabled()) {
				log.warn(FrameworkLogLocalizer.format("003041", profileName));
				log.warn(FrameworkLogLocalizer.format("003019", profileName));
				
				return new FailSafeDummyDataSource();
			}
		} 
		
		
		for( String dataSourceProvider : providers ){
			DataSource dataSourceToUse = null ;
			String providerTag = profileTag + "." + dataSourceProvider	;	
			if("jndiDataSourceProvider".equals(dataSourceProvider))
			{
				String jndiNameTag = providerTag + ".jndiName";				
				String jndiName = config.get( jndiNameTag + ".jndiName");		
				
				Assert.hasText(jndiName, FrameworkLogLocalizer.getMessage("003042"));		
				
				JndiDataSourceLookup lookup = new JndiDataSourceLookup();
				return lookup.getDataSource(jndiName);
				
			}else if ("pooledDataSourceProvider".equals(dataSourceProvider)){				
				log.debug(FrameworkLogLocalizer.format("002003", "DataSource", State.CREATING.name()));
				String driverClassName = config.get(providerTag + ".driverClassName");
			    String url = config.get(providerTag + ".url");
			    String username = config.get(providerTag + ".username");
			    String password = config.get(providerTag + ".password");
			    
			    if(log.isDebugEnabled()) {
			    		log.debug(FrameworkLogLocalizer.format("003043", driverClassName, url));
			    }		
			    if( dataSourceToUse == null && ClassUtils.isPresent(DBCP2_CALSSNAME, null) ) {
		    			dataSourceToUse = newDbcpDataSource(providerTag, config, DBCP2_CALSSNAME, driverClassName, url, username, password);
		    		}			    		
		    		if( dataSourceToUse == null && ClassUtils.isPresent(DBCP_CALSSNAME, null) ) {
		    			dataSourceToUse = newDbcpDataSource(providerTag, config, DBCP_CALSSNAME, driverClassName, url, username, password);
		    		}		    		
		    		log.debug(FrameworkLogLocalizer.format("002003", "DataSource",  State.CREATED.name()));		    		
		    		return dataSourceToUse;
			}
		}		
		*/
		return null;
	}
	
	private DataSource newDbcpDataSource(String className, PooledDataSourceConfig dataSourceConfig ) {
		DataSource dataSourceToUse = null;
		try { 
			if(log.isErrorEnabled())
				log.debug( FrameworkLogLocalizer.format("003010", className ));
			
			Class targetClass = ClassUtils.resolveClassName(className, null);
			Object targetObject = targetClass.getConstructor().newInstance(); 
			MethodInvoker invoker = new MethodInvoker();
			invoker.setTargetObject(targetObject);
			invoker.setTargetMethod("setDriverClassName");
			invoker.setArguments(new Object[] { dataSourceConfig.getDriverClassName() });
			invoker.prepare();
			invoker.invoke();

			invoker.setTargetMethod("setUrl");
			invoker.setArguments(new Object[] { dataSourceConfig.getUrl() });
			invoker.prepare();
			invoker.invoke();

			invoker.setTargetMethod("setUsername");
			invoker.setArguments(new Object[] { dataSourceConfig.getUsername() });
			invoker.prepare();
			invoker.invoke();

			invoker.setTargetMethod("setPassword");
			invoker.setArguments(new Object[] { dataSourceConfig.getPassword() });
			invoker.prepare();
			invoker.invoke();
			
			Map<String, String> properties = dataSourceConfig.getConnectionProperties();
		    for( String key : properties.keySet()) {
		    	invoker.setTargetMethod("addConnectionProperty");
				invoker.setArguments(new Object[] { key, properties.get(key)});
				invoker.prepare();
				invoker.invoke();
		    }
		    dataSourceToUse = (DataSource) targetObject;
		} catch (Exception e) { }
		return dataSourceToUse;
	}
	
	private DataSource newDbcpDataSource(String providerTag, ApplicationProperties config, String className, String driverClassName, String url, String username, String password) {
		DataSource dataSourceToUse = null;
		try {
			
			if(log.isErrorEnabled())
				log.debug( FrameworkLogLocalizer.format("003010", className ));
			
			Class targetClass = ClassUtils.resolveClassName(className, null);
			Object targetObject = targetClass.getConstructor().newInstance();

			MethodInvoker invoker = new MethodInvoker();
			invoker.setTargetObject(targetObject);
			invoker.setTargetMethod("setDriverClassName");
			invoker.setArguments(new Object[] { driverClassName });
			invoker.prepare();
			invoker.invoke();

			invoker.setTargetMethod("setUrl");
			invoker.setArguments(new Object[] { url });
			invoker.prepare();
			invoker.invoke();

			invoker.setTargetMethod("setUsername");
			invoker.setArguments(new Object[] { username });
			invoker.prepare();
			invoker.invoke();

			invoker.setTargetMethod("setPassword");
			invoker.setArguments(new Object[] { password });
			invoker.prepare();
			invoker.invoke();
			
		    String propertiesTag = providerTag + ".connectionProperties";			    
		    for(String name : config.getChildrenNames(propertiesTag) ){
		    		String value = config.get( propertiesTag + "." + name );		    	
		    		if(log.isDebugEnabled())
		    			log.debug(FrameworkLogLocalizer.format("003044", name, value));		    		
				invoker.setTargetMethod("addConnectionProperty");
				invoker.setArguments(new Object[] { name, value});
				invoker.prepare();
				invoker.invoke();				
		    	}
		    dataSourceToUse = (DataSource) targetObject;
		} catch (Exception e) { }
		return dataSourceToUse;
	}
	

	public String getProfileName() {
		return profileName;
	}
	
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

}
