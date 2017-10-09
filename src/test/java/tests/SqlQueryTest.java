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

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import architecture.ee.jdbc.sqlquery.SqlQuery;
import architecture.ee.jdbc.sqlquery.factory.SqlQueryFactory;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations={"classpath:application-context3.xml"})
public class SqlQueryTest {

	private static Logger log = LoggerFactory.getLogger(SqlQueryTest.class);
	
	@Autowired
	@Qualifier("sqlQueryFactory")
    private SqlQueryFactory sqlQueryFactory;
	
	@Autowired
	@Qualifier("dataSource")
    private DataSource dataSource;
	
	@Test
	public void testJdbcTemplate1(){		
		
		SqlQuery sqlQuery = sqlQueryFactory.createSqlQuery(dataSource);
		sqlQuery.setMaxResults(15);
		
		log.debug("LIST 1: {}" , sqlQuery.queryForList("COMMON.SELECT_TABLE_NAMES", String.class ) );
		
		log.debug("LIST 2: {}" , sqlQuery.queryForList("COMMON.SELECT_TABLE_NAMES", 15, 15, String.class) );

		log.debug("LIST 3: {}" , sqlQuery.queryForList("COMMON.SELECT_TABLE_NAMES" ) );
		
		log.debug("LIST 4: {}" , sqlQuery.queryForList("COMMON.SELECT_TABLE_NAMES", 15, 15 ) );
		
		
		try {
			log.debug("OBJECT 1: {}" , sqlQuery.queryForObject("COMMON.SELECT_TABLE_NAME", "ORI_TEMP" ) );
		} catch (Exception e) {
			log.error("",e);
		}
		
	}
	
	
}