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
package architecture.ee.service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * 
 * @author donghyuck
 *
 */
public interface ConfigService extends EventTarget {
	
	public boolean isSetupComplete();
	
	public Locale getLocale();
	
	public void setLocale(Locale newLocale);
	
	public String getCharacterEncoding() ;
	
	public void setCharacterEncoding(String characterEncoding) throws UnsupportedEncodingException;
	
	public TimeZone getTimeZone() ;
	
	public void setTimeZone(TimeZone newTimeZone);
	
	
	/**
	 * startup-config.xml 파일에 저장된 프로퍼티 값을 리턴한다.
	 * 
	 * @param name
	 * @return
	 */
	public String getLocalProperty(String name);

	public String getLocalProperty(String name, String defaultValue) ;
	
	
	public int getLocalProperty(String name, int defaultValue);

	/**
	 * 자식에 해당하는 프로퍼티 값들을 리턴한다.
	 * 
	 * @param parent
	 * @return
	 */
	public List<String> getLocalProperties(String parent);

	/**
	 * 프로퍼티 값을 설정 한다.
	 * 
	 * @param name
	 * @param value
	 */
	public void setLocalProperty(String name, String value);

	/**
	 * 한번에 여러 프로퍼티 값들을 저장한다.
	 * 
	 * @param propertyMap
	 */
	public void setLocalProperties(Map<String, String> propertyMap);

	/**
	 * name 에 해당하는 프로퍼티를 삭제한다.
	 * 
	 * @param name
	 */
	public void deleteLocalProperty(String name);

	public String getApplicationProperty(String name);

	public String getApplicationProperty(String name, String defaultValue);

	public List<String> getApplicationPropertyNames();

	public List<String> getApplicationPropertyNames(String parent);

	public List<String> getApplicationProperties(String parent);

	public int getApplicationIntProperty(String name, int defaultValue);

	public boolean getApplicationBooleanProperty(String name);

	public boolean getApplicationBooleanProperty(String name, boolean defaultValue);

	public void setApplicationProperty(String name, String defaultValue);

	public void setApplicationProperties(Map<String, String> map);

	public void deleteApplicationProperty(String name);
}
