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
package architecture.ee.component.event;

import org.springframework.context.ApplicationEvent;

public class PropertyChangeEvent extends ApplicationEvent {
	public enum Type {
		/**
		 * @uml.property name="aDDED"
		 * @uml.associationEnd
		 */
		ADDED,
		/**
		 * @uml.property name="rEMOVED"
		 * @uml.associationEnd
		 */
		REMOVED,
		/**
		 * @uml.property name="mODIFIED"
		 * @uml.associationEnd
		 */
		MODIFIED,
		
		NONE
	    }
	
	private Type eventType;
	
	/**
	 * name of the property that changed. May be null, if not known.
	 * 
	 * @serial
	 * @uml.property name="propertyName"
	 */
	private String propertyName;

	/**
	 * New value for property. May be null if not known.
	 * 
	 * @serial
	 * @uml.property name="newValue"
	 */
	private Object newValue;

	/**
	 * Previous value for property. May be null if not known.
	 * 
	 * @serial
	 * @uml.property name="oldValue"
	 */
	private Object oldValue;

	public PropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue) {
		super(source);
		this.propertyName = propertyName;
		this.newValue = newValue;
		this.oldValue = oldValue;
		this.eventType = Type.NONE;
	}

	public PropertyChangeEvent(Object source, Type eventType, String propertyName, Object oldValue, Object newValue) {
		super(source);
		this.propertyName = propertyName;
		this.newValue = newValue;
		this.oldValue = oldValue;
		this.eventType = eventType;
	}
	
	/**
	 * Gets the new value for the property, expressed as an Object.
	 * 
	 * @return The new value for the property, expressed as an Object. May be
	 *         null if multiple properties have changed.
	 * @uml.property name="newValue"
	 */
	public Object getNewValue() {
		return newValue;
	}

	/**
	 * Gets the old value for the property, expressed as an Object.
	 * 
	 * @return The old value for the property, expressed as an Object. May be
	 *         null if multiple properties have changed.
	 * @uml.property name="oldValue"
	 */
	public Object getOldValue() {
		return oldValue;
	}

	/**
	 * Gets the programmatic name of the property that was changed.
	 * 
	 * @return The programmatic name of the property that was changed. May be
	 *         null if multiple properties have changed.
	 * @uml.property name="propertyName"
	 */
	public String getPropertyName() {
		return propertyName;
	}

	public Type getEventType() {
		return eventType;
	}

	public void setEventType(Type eventType) {
		this.eventType = eventType;
	}

}
