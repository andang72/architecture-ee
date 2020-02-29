package architecture.ee.component.editor;

import java.io.Serializable;

public interface DataSourceConfig extends Serializable {

	enum Types { 
		NONE, 
		JNDI, 
		DRIVER_MANAGER,
		POOLED;  
	}

	public abstract Types getType();
	
	public abstract String getComment();

	public abstract String getBeanName();
	
	public abstract void setBeanName(String beanName);
	
	public abstract String getName();
	
	public abstract String getUsername();

	public abstract void setComment(String comment);

	public abstract void setName(String exportName);
	
	public abstract boolean isActive();
	
	public void setActive(boolean active) ;

}
