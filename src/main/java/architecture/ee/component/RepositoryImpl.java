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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletContext;

import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.support.ServletContextResource;

import architecture.ee.i18n.CommonLogLocalizer;
import architecture.ee.i18n.FrameworkLogLocalizer;
import architecture.ee.service.ApplicationProperties;
import architecture.ee.service.ConfigRoot;
import architecture.ee.service.Repository;
import architecture.ee.util.ApplicationConstants;

/**
 * 
 * @author donghyuck
 *
 */
public class RepositoryImpl implements Repository, ServletContextAware {

	private AtomicBoolean initailized = new AtomicBoolean(false);

	private Logger log = LoggerFactory.getLogger(getClass());

	private Resource rootResource = getRootResource();

	private Lock lock = new ReentrantLock();
	
	private ApplicationProperties setupProperties = null;
	
	private State state = State.NONE;
	
	public ConfigRoot getConfigRoot() {
		try {
			File file = new File( getRootResource().getFile() , "config" );
			log.debug(CommonLogLocalizer.format( "003010",  file.getPath() ));			
			Resource child = new FileSystemResource(file);			
			log.debug(CommonLogLocalizer.format( "003005",  child.exists() ));
			return new ConfigRootImpl(child);
		} catch (Exception e) {
			return null;
		}
	}
	
	public File getFile(String name) {
		try {			
			File file = new File( getRootResource().getFile() , name );
			return file;
		} catch (IOException e) {
		}
		return null;
	}

	private Resource getRootResource() {
		if (initailized.get())
			return rootResource;
		return null;
	}

	public ApplicationProperties getSetupApplicationProperties() {
		if (setupProperties == null) {
			if(initailized.get()){
				
				File file = getFile(ApplicationConstants.DEFAULT_STARTUP_FILENAME);						
						
				if (!file.exists()){					
					boolean error = false;
				    // create default file...
				    log.debug(CommonLogLocalizer.format("003012", file.getAbsolutePath()));
				    
				    Writer writer = null;
				    
				    if(!file.getParentFile().exists() )
				    {
				    	file.getParentFile().mkdirs();
				    }
				    
				    try {
				    	
				    	lock.lock();
				    	
				    	writer = new OutputStreamWriter(new FileOutputStream(file),	StandardCharsets.UTF_8);
				    	XMLWriter xmlWriter = new XMLWriter(writer, OutputFormat.createPrettyPrint());
				    	StringBuilder sb = new StringBuilder();
				    	
				    	
						org.dom4j.Document document = org.dom4j.DocumentHelper.createDocument();
						org.dom4j.Element root = document.addElement("startup-config");
						// setup start
						// ------------------------------------------------------------
						org.dom4j.Element setupNode = root.addElement("setup");
						setupNode.addElement("complete").setText("false");
						// setup end
						
						// license start
						root.addComment("LICENSE SETTING HERE!");
						org.dom4j.Element licenseNode = root.addElement("license");
						// license end
						
						// view start
						/*
						org.dom4j.Element viewNode = root.addElement("view");
						org.dom4j.Element renderNode = viewNode.addElement("render");
						org.dom4j.Element freemarkerNode = renderNode.addElement("freemarker");
						freemarkerNode.addElement("enabled").setText("true");
						freemarkerNode.addElement("source").addElement("location");
						org.dom4j.Element velocityNode = renderNode.addElement("velocity");
						velocityNode.addElement("enabled").setText("false");
						*/
						// view end
						
						// security start			
						root.addComment("SECURITY SETTING HERE!");
						org.dom4j.Element securityNode = root.addElement("security");
						org.dom4j.Element encrpptNode = securityNode.addElement("encrypt");
						encrpptNode.addElement("algorithm").setText("Blowfish");
						encrpptNode.addElement("key").addElement("current");
						org.dom4j.Element encrpptPropertyNode = encrpptNode.addElement("property");
						encrpptPropertyNode.addElement("name").setText("username");
						encrpptPropertyNode.addElement("name").setText("password");						
						securityNode.addElement("authentication").addElement("encoding").addElement("algorithm").setText("SHA-256");
						// security end
						// services start
						root.addComment("SERVICES SETTING HERE !");
						org.dom4j.Element servicesNode = root.addElement("services");
						servicesNode.addComment("config service");
						servicesNode.addElement("config").addElement("persistence").addElement("jdbc").addElement("enabled").setText("false");
						servicesNode.addComment("sqlquery service");
						servicesNode.addElement("sqlquery").addElement("resource").addElement("location").setText("sql");
						
						// services end
						// component setting start
						root.addComment("COMPONENTS SETTING HERE!");
						
						// component setting end
						
						// database start
						root.addComment("DATABASE SETTING");
						org.dom4j.Element databaseNode = root.addElement("database");
						org.dom4j.Element databaseDefaultNode = databaseNode.addElement("default");
						databaseDefaultNode.addComment(" 1. jndi datasource ");
						databaseDefaultNode.addComment((new StringBuilder()).append("\n")
						.append("      ").append("<jndiDataSourceProvider>").append("\n")
						.append("      ").append("	<jndiName></jndiName>").append("\n")
						.append("      ").append("</jndiDataSourceProvider>").append("\n")
						.toString()); 
						databaseDefaultNode.addComment(" 2. connection pool datasource using dbcp ");
						databaseDefaultNode.addComment((
						new StringBuilder()).append("\n")
						.append("      ").append("<pooledDataSourceProvider> ").append("\n")
						.append("      ").append("    <driverClassName></driverClassName> ").append("\n")
						.append("      ").append("    <url></url>").append("\n")
						.append("      ").append("    <username></username>").append("\n")
						.append("      ").append("    <password></password>").append("\n")
						.append("      ").append("    <connectionProperties>").append("\n") 
						.append("      ").append("        <initialSize>1</initialSize>").append("\n")
						.append("      ").append("        <maxActive>8</maxActive>").append("\n")
						.append("      ").append("        <maxIdle>8</maxIdle>").append("\n")
						.append("      ").append("        <maxWait>-1</maxWait>").append("\n") 
						.append("      ").append("        <minIdle>0</minIdle>").append("\n")
						.append("      ").append("        <testOnBorrow>true</testOnBorrow>").append("\n")
						.append("      ").append("        <testOnReturn>false</testOnReturn>").append("\n")
						.append("      ").append("        <testWhileIdle>false</testWhileIdle>").append("\n")
						.append("      ").append("        <validationQuery>select 1 from dual</validationQuery>").append("\n")  
						.append("      ").append("    </connectionProperties>").append("\n") 
						.append("      ").append("</pooledDataSourceProvider>") 
						.toString());  
						// database end
						xmlWriter.write(document);
					    } catch (Exception e) {
					    	log.error("fail to making {} - {}", file.getName(), e.getMessage());				    	
					    	error = true;
					    } finally {
					    	
						try {
						    writer.flush();
						    writer.close();
						} catch (Exception e) {
						    log.error("error" , e);
						    error = true;
						}						
						lock.unlock();						
				    }	
				}else{
					try {
						log.debug(CommonLogLocalizer.format("003011", file.getPath()));		
						this.setupProperties = new LocalApplicationProperties(file);
					} catch (IOException e) {
						 log.error(CommonLogLocalizer.getMessage("003013") , e);
					}
				}
			}else{
				return LocalApplicationProperties.EMPTY_APPLICATION_PROPERTIES;
			}
		}
		return setupProperties ;
	}

	public void initialize(){
		state = State.INITIALIZING;		
		log.debug(CommonLogLocalizer.format("003001", "Repository",state.name() ));
		if(initailized.get()) {			
			log.debug(CommonLogLocalizer.getMessage("003003"));
		}		
		state = State.INITIALIZED;
		log.debug(CommonLogLocalizer.format("003001", "Repository",state.name() ));
	}
	
	public void setServletContext(ServletContext servletContext) {
		if (!initailized.get()) {
			
			log.info(FrameworkLogLocalizer.format("002001", "Repository", State.INITIALIZING.name()));			
			ServletContextResource resource = new ServletContextResource(servletContext, "/WEB-INF");
			log.debug( CommonLogLocalizer.format( "003006", resource.getPath() ));
			try {
				File file = resource.getFile();
				log.debug(CommonLogLocalizer.format( "003005",  file.exists() ));
				if( !file.exists() )		
				{
					
				}
				rootResource = new FileSystemResource(file);
				log.debug(CommonLogLocalizer.format( "003007",  rootResource.toString() ));				
				initailized.set(true);
				
			} catch (IOException e) {
				log.error(CommonLogLocalizer.getMessage("003008"), e);
			}	
			log.info(FrameworkLogLocalizer.format("002001", "Repository", State.INITIALIZED.name() ));			
		}
	}

}
