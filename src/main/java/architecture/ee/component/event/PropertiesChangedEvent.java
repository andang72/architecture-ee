package architecture.ee.component.event;

import org.springframework.context.ApplicationEvent;

public class PropertiesChangedEvent extends ApplicationEvent {

	private String name;
	
	public PropertiesChangedEvent(Object source, String name ) {
		super(source); 
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String toString() {
		return "PropertiesChangedEvent [" + name  + "]" ;
	}

}
