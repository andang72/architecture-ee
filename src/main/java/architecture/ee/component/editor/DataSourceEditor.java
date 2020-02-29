package architecture.ee.component.editor;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.dom4j.Element;

import architecture.ee.util.StringUtils; 
 

public class DataSourceEditor extends AbstractXmlEditor {

	org.dom4j.Element element ; 
	
	public DataSourceEditor(File file) {
		super(file);
		initialize();
	}  
	
	protected void initialize() { 
		Element root = getDocument().getRootElement();
		this.element = root.element("database");  
	} 
	
	
	public void removeDataSource(String name) {
		Element child = getChild(element, name) ;
		if( child != null ) {
			element.remove(child);
		} 
	}

	
	/**
	<jndiDataSourceProvider>
  	<jndiName></jndiName>
  	</jndiDataSourceProvider>
	*/
	public void setJndiDataSource(JndiDataSourceConfig bean) { 
		Element oldElement = getChild(this.element, bean.name);
		if( oldElement != null ) {
			element.remove(oldElement);
		}  
		Element newElement = element.addElement(bean.name);  
		if(!StringUtils.isNullOrEmpty(bean.comment))
			newElement.addComment(bean.comment);
		
		Element jndiDataSourceProvider = newElement.addElement("jndiDataSourceProvider");
		Element jndiName = jndiDataSourceProvider.addElement("jndiName");
		jndiName.setText(bean.jndiName); 
	}

	/**
		<pooledDataSourceProvider> 
		<name></name>
	    <driverClassName></driverClassName> 
	    <url></url>
	    <username></username>
	    <password></password>
	    <connectionProperties>
	        <initialSize>1</initialSize>
	        <maxActive>8</maxActive>
	        <maxIdle>8</maxIdle>
	        <maxWait>-1</maxWait>
	        <minIdle>0</minIdle>
	        <testOnBorrow>true</testOnBorrow>
	        <testOnReturn>false</testOnReturn>
	        <testWhileIdle>false</testWhileIdle>
	        <validationQuery>select 1 from dual</validationQuery>
	    </connectionProperties>
	</pooledDataSourceProvider> 
	 */
	
	public void setPooledDataSource(PooledDataSourceConfig bean) { 
		Element oldElement = getChild(this.element, bean.name);
		if( oldElement != null ) {
			element.remove(oldElement);
		} 
		Element newElement = element.addElement(bean.name); 
		
		if(!StringUtils.isNullOrEmpty(bean.comment))
			newElement.addComment(bean.comment);
		
		Element pooledDataSourceProvider = newElement.addElement("pooledDataSourceProvider");
		pooledDataSourceProvider.addElement("driverClassName").setText(bean.driverClassName);
		pooledDataSourceProvider.addElement("url").setText(bean.url); 
		if(!StringUtils.isNullOrEmpty( bean.username ))
		{
			pooledDataSourceProvider.addElement("username").setText(bean.username);
		}
		if(!StringUtils.isNullOrEmpty( bean.password ))
		{
			pooledDataSourceProvider.addElement("password").setText(bean.password);;
		}
		Element connectionProperties = pooledDataSourceProvider.addElement("connectionProperties");
		
		Set<String> keys = bean.connectionProperties.keySet();
		for(String key : keys) {
			connectionProperties.addElement(key).setText(bean.connectionProperties.get(key));
		}
	} 
	
	
	

	/**
	<driverManagerDataSourceProvider> 
		<name></name>
	    <driverClassName></driverClassName> 
	    <url></url>
	    <username></username>
	    <password></password>
	</driverManagerDataSourceProvider> 
	 */
	public void setDriverManagerDataSource(DriverManagerDataSourceConfig bean) { 
		Element oldElement = getChild(this.element, bean.name);
		if( oldElement != null ) {
			element.remove(oldElement);
		} 
		Element newElement = element.addElement(bean.name); 
		
		if(!StringUtils.isNullOrEmpty(bean.comment))
			newElement.addComment(bean.comment);
		
		Element driverManagerDataSourceProvider = newElement.addElement("driverManagerDataSourceProvider");
		driverManagerDataSourceProvider.addElement("driverClassName").setText(bean.driverClassName);
		driverManagerDataSourceProvider.addElement("url").setText(bean.url); 
		if(!StringUtils.isNullOrEmpty( bean.username ))
		{
			driverManagerDataSourceProvider.addElement("username").setText(bean.username);
		}
		if(!StringUtils.isNullOrEmpty( bean.password ))
		{
			driverManagerDataSourceProvider.addElement("password").setText(bean.password);;
		}
	} 	
	
	protected Element getChild(Element parent, String name) {
		for (Iterator<Element> it = parent.elementIterator(); it.hasNext();) {
	        Element child = it.next();
	        // do something
	        if( StringUtils.equals( child.getName(), name ) ) {
	        	return child;
	        }
	    }
		return null;
	}

	public static PooledDataSourceConfig newPooledDataSourceBean(String name) {
		PooledDataSourceConfig bean = new PooledDataSourceConfig(name); 
		return bean;
	}
	
	public static JndiDataSourceConfig newJndiDataSourceBean(String name) {
		JndiDataSourceConfig bean = new JndiDataSourceConfig(name); 
		return bean;
	} 
	
	public static DriverManagerDataSourceConfig newsetDriverManagerDataSourceBean(String name) {
		DriverManagerDataSourceConfig bean = new DriverManagerDataSourceConfig(name); 
		return bean;
	} 
	
	@SuppressWarnings("serial")
	public static class JndiDataSourceConfig implements DataSourceConfig {
		
		String comment;
		String name;
		String jndiName ; 
		boolean active; 
		String beanName;
		String username ;

		public JndiDataSourceConfig(String name) { 
			this.name = name;
		}

		public String getBeanName() {
			return beanName;
		}

		public void setBeanName(String beanName) {
			this.beanName = beanName;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}
		
		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String exportName) {
			this.name = exportName;
		}
		
		public String getJndiName() {
			return jndiName;
		} 
		public void setJndiName(String jndiName) {
			this.jndiName = jndiName;
		} 
		
		public Types getType() {
			return Types.JNDI;
		}
 
		public String getUsername() { 
			return username;
		}
	} 
	
	@SuppressWarnings("serial")
	public static class DriverManagerDataSourceConfig implements DataSourceConfig {
		
		String comment; 
		String name; 
		String driverClassName ; 
		String url; 
		String username; 
		String password; 
		boolean active; 
		String beanName;
		
		public DriverManagerDataSourceConfig(String name) {
			super();
			this.name = name;
		}

		public String getBeanName() {
			return beanName;
		}

		public void setBeanName(String beanName) {
			this.beanName = beanName;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}

		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getName() {
			return name;
		}

		public void setName(String exportName) {
			this.name = exportName;
		}

		public String getDriverClassName() {
			return driverClassName;
		}

		public void setDriverClassName(String driverClassName) {
			this.driverClassName = driverClassName;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
		
		public Types getType() {
			return Types.DRIVER_MANAGER;
		}
	}
	
	@SuppressWarnings("serial")
	public static class PooledDataSourceConfig implements DataSourceConfig {
		
		String comment; 
		String name; 
		String driverClassName ; 
		String url; 
		String username; 
		String password;
		String beanName;
		
		Map<String, String> connectionProperties = new HashMap<String, String>(); 
		
		boolean active; 

		public PooledDataSourceConfig(String name) {
			super();
			this.name = name;
		}

		public String getBeanName() {
			return beanName;
		}

		public void setBeanName(String beanName) {
			this.beanName = beanName;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active) {
			this.active = active;
		}
		
		public String getComment() {
			return comment;
		}

		public void setComment(String comment) {
			this.comment = comment;
		}

		public String getName() {
			return name;
		}

		public void setName(String exportName) {
			this.name = exportName;
		}

		public String getDriverClassName() {
			return driverClassName;
		}

		public void setDriverClassName(String driverClassName) {
			this.driverClassName = driverClassName;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public Map<String, String> getConnectionProperties() {
			return connectionProperties;
		}

		public void setConnectionProperties(Map<String, String> connectionProperties) {
			this.connectionProperties = connectionProperties;
		}
		
		public void setConnectionProperties(String key, String value) {
			this.connectionProperties.put(key, value);
		}
 
		public Types getType() {
			return Types.POOLED;
		}
	}
}
