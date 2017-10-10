/*
 * Copyright 2012 Donghyuck, Son
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package architecture.ee.spring.jdbc.support;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.lob.LobHandler;

import architecture.ee.spring.jdbc.ExtendedJdbcTemplate;
import architecture.ee.util.StringUtils;
import architecture.ee.i18n.CommonLogLocalizer;
import architecture.ee.jdbc.sqlquery.SqlQuery;
import architecture.ee.jdbc.sqlquery.factory.SqlQueryFactory;


/**
 * @author   andang, son
 */
public class SqlQueryDaoSupport extends JdbcDaoSupport {
	
	protected Logger log = LoggerFactory.getLogger(getClass());

	private SqlQueryFactory sqlQueryFactory = null ;

	public SqlQueryFactory getSqlQueryFactory() {
		
		return sqlQueryFactory;
	}
	
	public void setSqlQueryFactory(SqlQueryFactory sqlQueryFactory) {
		this.sqlQueryFactory = sqlQueryFactory;
	}

	protected ExtendedJdbcTemplate createJdbcTemplate(DataSource dataSource) {
		return new ExtendedJdbcTemplate(dataSource);
	}
	
	public ExtendedJdbcTemplate getExtendedJdbcTemplate(){
		return (ExtendedJdbcTemplate) getJdbcTemplate();
	} 
	
	/*protected MaxValueIncrementer getMaxValueIncrementer(){
		return sqlQueryFactory.getMaxValueIncrementer();
	}*/
	
	protected void initTemplateConfig() {
	    log.debug("initTemplateConfig");
	    getExtendedJdbcTemplate().initialize();
	}
	
	public LobHandler getLobHandler(){
		return getExtendedJdbcTemplate().getLobHandler();
	} 
	
	public void setLobHandler(LobHandler lobHandler){
		getExtendedJdbcTemplate().setLobHandler(lobHandler);
	} 

	private boolean isSetDataSource(){
		if( getDataSource() != null)
			return true;
		return false;		
	}
	
	private boolean isSetSqlQueryFactory(){
		if( sqlQueryFactory != null )
			return true;
		return false;
	}
		
	protected SqlQuery getSqlQuery(){
		if(isSetSqlQueryFactory()){
			if(isSetDataSource())
				return sqlQueryFactory.createSqlQuery(getDataSource());	
			else 
				return sqlQueryFactory.createSqlQuery();			
		}
		return null;
	}	
	
	protected SqlQuery getSqlQuery(DataSource dataSource){
		if(isSetSqlQueryFactory())
			return sqlQueryFactory.createSqlQuery(dataSource);
		return null;
	}
		
		
	/**
	 * 
	 * @param scriptName 실행할 스크립트 
	 * @param methodName 스크립트의 함수
	 * @param parameters 파라메터
	 * @return
	 */
	protected Object execute( String scriptName, String methodName, Object... parameters ){
		if( StringUtils.isEmpty( scriptName ) )
			throw new NullPointerException( CommonLogLocalizer.getMessage("003081"));	
		return null;		
	}
	
}