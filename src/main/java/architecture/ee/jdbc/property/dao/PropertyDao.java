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
package architecture.ee.jdbc.property.dao;

import java.util.Map;

/**
 * 
 * @author donghyuck
 *
 */
public interface PropertyDao {

	/**
	 * 
	 * @param table
	 * @param typeField
	 * @param objectID
	 * @return
	 */
	public abstract Map<String, String> getProperties(String table, String typeField, long objectID);

	public abstract void deleteProperties(String table, String typeField, long objectID);
	
	public abstract void updateProperties(String table, String keyField, long objectID, Map<String, String> properties);
	
}
