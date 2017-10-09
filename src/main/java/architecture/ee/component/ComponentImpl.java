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

import com.google.common.eventbus.EventBus;

import architecture.ee.service.Component;

public class ComponentImpl implements Component {

	private EventBus eventBus;

	protected EventBus getEventBus(){
		return eventBus;
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
	
}
