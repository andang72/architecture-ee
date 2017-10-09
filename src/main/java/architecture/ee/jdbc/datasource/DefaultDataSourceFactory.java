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

import java.util.Collection;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.util.Assert;

import architecture.ee.exception.RuntimeError;
import architecture.ee.i18n.CommonLogLocalizer;
import architecture.ee.service.ApplicationProperties;
import architecture.ee.service.Repository;

/**
 * startup-config.xml 에 database 
 * @author donghyuck
 *
 */
public class DefaultDataSourceFactory implements DataSourceFactory {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired( required = true) @Qualifier("repository")
	private Repository repository;
	
	
	private String profileName ;	


	
	public DataSource getDataSource() {			
		
		String profileTag = "database." + profileName ;		
		
		ApplicationProperties config = repository.getSetupApplicationProperties();		
		Collection<String> dataSourceProviders = config.getChildrenNames(profileTag);				
		
		log.debug(CommonLogLocalizer.format("003040", profileName));		
	
		if( dataSourceProviders.size() == 0 )
			throw new RuntimeError(CommonLogLocalizer.format("003041", profileName));
		
		for( String dataSourceProvider : dataSourceProviders ){
			String providerTag = profileTag + "." + dataSourceProvider	;	
			if("jndiDataSourceProvider".equals(dataSourceProvider))
			{
				String jndiNameTag = providerTag + ".jndiName";				
				String jndiName = config.get( jndiNameTag + ".jndiName");		
				
				Assert.hasText(jndiName, CommonLogLocalizer.getMessage("003042"));		
				
				JndiDataSourceLookup lookup = new JndiDataSourceLookup();
				return lookup.getDataSource(jndiName);
				
			}else if ("pooledDataSourceProvider".equals(dataSourceProvider)){
				

				String driverClassName = config.get(providerTag + ".driverClassName");
			    String url = config.get(providerTag + ".url");
			    String username = config.get(providerTag + ".username");
			    String password = config.get(providerTag + ".password");
			    
			    log.debug(CommonLogLocalizer.format("003043", driverClassName, url));
			    
			    org.apache.commons.dbcp2.BasicDataSource dbcp = new org.apache.commons.dbcp2.BasicDataSource();
			    dbcp.setDriverClassName(driverClassName);
			    dbcp.setUrl(url);
			    dbcp.setUsername(username);
			    dbcp.setPassword(password);			    
			    String propertiesTag = providerTag + ".connectionProperties";			    
			    for(String name : config.getChildrenNames(propertiesTag) ){
			    	String value = config.get( propertiesTag + "." + name );
			    	log.debug(CommonLogLocalizer.format("003044", name, value));
			    	dbcp.addConnectionProperty(name, value);
			    }			  
			    
			    return dbcp;
			}
		}		
		return null;
	}

	public String getProfileName() {
		return profileName;
	}

	
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

}
