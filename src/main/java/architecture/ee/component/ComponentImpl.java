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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;

import com.google.common.eventbus.EventBus;

import architecture.ee.component.event.StateChangeEvent;
import architecture.ee.service.Component;

/**
 * 
 * @author donghyuck
 *
 */
public class ComponentImpl implements Component {
	
	@Autowired(required = false)
	@Qualifier("eventBus")
	private EventBus eventBus;

	@Autowired(required = false)
	private ApplicationEventPublisher applicationEventPublisher;	
	
	protected EventBus getEventBus(){
		return eventBus;
	}
	
	protected ApplicationEventPublisher getApplicationEventPublisher() {
		return applicationEventPublisher;
	}

	public void publish(Object event) {		
		if( eventBus != null)
			eventBus.post(event);
	}

	public void register () {
		if( eventBus != null)
			eventBus.register(this);
	}
	
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}
	
	public void unregister (){
		if( eventBus != null)
			eventBus.unregister(this);		
	}	
	
	protected void fireStateChangeEvent(State oldState, State newState) {
		
		StateChangeEvent event = new StateChangeEvent(this, oldState, newState);
		if( eventBus != null)
			eventBus.post(event);
		
		if( applicationEventPublisher != null )
			applicationEventPublisher.publishEvent(event);
		
	}
}
