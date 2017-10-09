package tests;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.google.common.eventbus.Subscribe;

import architecture.ee.component.event.StateChangeEvent;
import architecture.ee.service.Repository;

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


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations={"classpath:application-context2.xml"})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RepositoryTest {
	
	private static Logger log = LoggerFactory.getLogger(RepositoryTest.class);
	
	@Autowired
    private Repository repository;

	@Test
	public void testRepository(){
		log.debug("repository config path : {}", repository.getFile("config"));
		log.debug("startup properties : {}", repository.getSetupApplicationProperties());		
	}
	class StateChangeEventListener {
		
		@Subscribe 
		public void handel(StateChangeEvent e) {
		    log.debug("************** {} {}", e.getSource(), e.toString());
		}
		
	}
}
