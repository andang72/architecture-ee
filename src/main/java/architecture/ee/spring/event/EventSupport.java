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
package architecture.ee.spring.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.google.common.eventbus.EventBus;

public abstract class EventSupport implements ApplicationEventPublisherAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(EventSupport.class);
	
	@Autowired(required = false)
	@Qualifier("eventBus")
	private EventBus eventBus;

	private ApplicationEventPublisher applicationEventPublisher;	

	public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
		this.applicationEventPublisher = applicationEventPublisher;
	}
	
	protected void registerEventListener(Object listener) {
		if( eventBus != null)
			eventBus.register(listener);
	}

	protected void unregisterEventListener(Object listener) {
		if( eventBus != null)
			eventBus.unregister(listener);
	}
	
	protected void fireEvent(Object event){		 
		
		LOG.debug("EVENT:" + event.getClass().getName());
		
		if( eventBus != null ){ 
			LOG.debug("GOOGLE EVENT ENABLED");
			eventBus.post(event);
		} 
		if( applicationEventPublisher != null ) {
			LOG.debug("SPRING EVENT ENABLED");
			applicationEventPublisher.publishEvent(event);
		}
	}

}
