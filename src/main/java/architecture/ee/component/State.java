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

public enum State {
	/**
     */
    NONE("NONE"),
    /**
     */
    INITIALIZING("INITIALIZING"),
    /**
     */
    INITIALIZED("INITIALIZED"),
    /**
     */
    STARTING("STARTING"),
    /**
     */
    STARTED("STARTED"),
    /**
     */
    STOPING("STOPING"),
    /**
     */
    RUNNING("RUNNING"),
    /**
     */
    STOPED("STOPED"),
    /**
     */
    DESTROYING("DESTROYING"),
    /**
     */
    DESTROYED("DESTROYED"),

    /**
     */
    POST_UPGRADE_STARTED("POST_UPGRADE_STARTED");

    private String desc;

    private State(String desc) {
	this.desc = desc;
    }

    public String toString() {
	return desc;
    }
}
