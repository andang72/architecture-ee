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

import java.util.List;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import architecture.ee.spring.jdbc.ExtendedJdbcTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations={"classpath:application-context4.xml"})
public class DataSourceTest implements ApplicationContextAware{

	private static Logger log = LoggerFactory.getLogger(DataSourceTest.class);
	
	@Autowired
	ApplicationContext context;
	
	@Test
	public void testCreateDataSource(){
		log.debug("DataSoruce:" + context.getBean(DataSource.class) );
	}
	
	@Test
	public void testCreateDataSourceAndExecuteQuery(){

		DataSource dataSource = context.getBean(DataSource.class);
		ExtendedJdbcTemplate template = new ExtendedJdbcTemplate(dataSource);
		try {
			List list = template.query("select table_name from tabs", 0, 15);
			log.debug( list.size() + " >>>>>>>>>> " + list ) ;
		} catch (DataAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		context = applicationContext;
	}
	
}
