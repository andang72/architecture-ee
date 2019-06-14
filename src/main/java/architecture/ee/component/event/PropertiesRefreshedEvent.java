package architecture.ee.component.event;

import org.springframework.context.ApplicationEvent;

public class PropertiesRefreshedEvent extends ApplicationEvent {

	private String name;
	
	public PropertiesRefreshedEvent(Object source, String name) {
		super(source);
		this.name = name;
	}

	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	@Override
	public String toString() {
		return "PropertiesRefreshedEvent [ " + name  + "]";
	}
}
