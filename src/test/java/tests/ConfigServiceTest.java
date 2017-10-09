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
package tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.eventbus.Subscribe;

import architecture.ee.component.event.PropertyChangeEvent;
import architecture.ee.service.ConfigService;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations={"classpath:application-context5.xml"})
public class ConfigServiceTest {

	private static Logger log = LoggerFactory.getLogger(ConfigServiceTest.class);
	
	@Autowired
    private ConfigService configService;
	
	
	@Test
	public void testGetProperty(){
		String name = "setup.complete";
		String value = configService.getLocalProperty(name);
		log.debug( "{}={}" , name, value);		
	}
	
	
	@Test
	public void testSetGetProperty(){
		String name = "setup.complete";
		configService.setLocalProperty(name, "true");
		String value = configService.getLocalProperty(name);
		log.debug( "after set {}={}" , name, value);		
		
		configService.setLocalProperty(name, "false");
		value = configService.getLocalProperty(name);
		log.debug( "after set {}={}" , name, value);	
	}

	@Test
	public void testJdbcSetGetProperty(){
		configService.registerEventListener(new PropertyChangeEventListener());	
		
		log.debug("setup completed :" + configService.isSetupComplete());
		
		if( configService.getApplicationProperty("welcome.message", null) != null )
			configService.deleteApplicationProperty("welcome.message");
		
		if( configService.getApplicationProperty("welcome.message", null) == null )
			configService.setApplicationProperty("welcome.message", "안녕하신가요 !!");
		
		log.debug( "LOCALE {}, TIMEZONE {}, ENCODING {}" , configService.getLocale().getDisplayName(), configService.getTimeZone().getDisplayName() , configService.getCharacterEncoding());
	}
	
	class PropertyChangeEventListener {
		
		@Subscribe 
		public void handel(PropertyChangeEvent e) {
		    log.debug("EVENT ************** {} {}", e.getSource().getClass().getName(), e.getEventType().name());
		}
		
	}
}
