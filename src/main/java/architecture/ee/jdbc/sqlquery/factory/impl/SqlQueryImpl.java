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
package architecture.ee.jdbc.sqlquery.factory.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.CallableStatementCreatorFactory;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

import com.google.common.base.Preconditions;

import architecture.ee.jdbc.sqlquery.SqlQuery;
import architecture.ee.jdbc.sqlquery.factory.Configuration;
import architecture.ee.jdbc.sqlquery.mapping.BoundSql;
import architecture.ee.jdbc.sqlquery.mapping.ParameterMapping;
import architecture.ee.spring.jdbc.ExtendedJdbcDaoSupport;


public class SqlQueryImpl extends ExtendedJdbcDaoSupport implements SqlQuery {
	
	private int startIndex = DEFAULT_START_INDEX ;
	
	private int maxResults = DEFAULT_MAX_RESULTS ;

	public SqlQueryImpl() {
		super();
	}

	public SqlQueryImpl(Configuration sqlConfiguration) {
		super(sqlConfiguration);
	}
 
	public SqlQuery setStartIndex(int startIndex) {
		this.startIndex = startIndex;
		return this;
	}
 
	public SqlQuery setMaxResults(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}
		
	public Map<String, Object> queryForObject(String statemenKey, Object... params){
		BoundSql boundSql = getBoundSql(statemenKey);	
		return getExtendedJdbcTemplate().queryForMap(boundSql.getSql(), params );
	}
	
	public <T> T queryForObject(String statemenKey, Class<T> elementType, Object... params){		
		BoundSql boundSql = getBoundSql(statemenKey);	
		return getExtendedJdbcTemplate().queryForObject(boundSql.getSql(), elementType, params);
	}
 
	
	@Override
	public List<Map<String, Object>> queryForList(String statemenKey) {
		BoundSql boundSql = getBoundSql(statemenKey);
		return getExtendedJdbcTemplate().queryForList(boundSql.getSql());
	}

	@Override
	public List<Map<String, Object>> queryForList(String statemenKey, int startIndex, int maxResults) {
		Preconditions.checkArgument(startIndex >= 0, "startIndex is %s but it must be greater then or equal to zero.", startIndex);
		Preconditions.checkArgument(maxResults > 0, "maxResults is %s but it must be greater then zero.", maxResults);		
		BoundSql boundSql = getBoundSql(statemenKey);
		return getExtendedJdbcTemplate().query(boundSql.getSql(), startIndex, maxResults);
	}
		
	public List<Map<String, Object>> queryForList(String statemenKey, Object... params) {
		BoundSql boundSql = getBoundSql(statemenKey);			
		return getExtendedJdbcTemplate().queryForList(boundSql.getSql(), params);	
	}
	
	public List<Map<String, Object>> queryForList(String statemenKey, int startIndex, int maxResults, Object... params) {
		Preconditions.checkArgument(startIndex >= 0, "startIndex is %s but it must be greater then or equal to zero.", startIndex);
		Preconditions.checkArgument(maxResults > 0, "maxResults is %s but it must be greater then zero.", maxResults);
		BoundSql boundSql = getBoundSql(statemenKey);
		return getExtendedJdbcTemplate().query(boundSql.getSql(), startIndex, maxResults ,params);
	}
	
	
	public <T> List<T> queryForList(String statemenKey, Class<T> elementType) {
		BoundSql boundSql = getBoundSql(statemenKey);	
		return getExtendedJdbcTemplate().queryForList(boundSql.getSql(), elementType);
	}
	
	public <T> List<T> queryForList(String statemenKey, Class<T> elementType, Object... params) {
		BoundSql boundSql = getBoundSql(statemenKey);			
		return getExtendedJdbcTemplate().queryForList(boundSql.getSql(), elementType, params);	
	}
	
	@Override
	public <T> List<T> queryForList(String statemenKey, int startIndex, int maxResults, Class<T> elementType) {
		Preconditions.checkArgument(startIndex >= 0, "startIndex is %s but it must be greater then or equal to zero.", startIndex);
		Preconditions.checkArgument(maxResults > 0, "maxResults is %s but it must be greater then zero.", maxResults);
		BoundSql boundSql = getBoundSql(statemenKey);	
		return getExtendedJdbcTemplate().query(boundSql.getSql(), startIndex, maxResults, elementType);
	}
	
	public <T> List<T> queryForList(String statemenKey, int startIndex, int maxResults, Class<T> elementType, Object... params) {
		Preconditions.checkArgument(startIndex >= 0, "startIndex is %s but it must be greater then or equal to zero.", startIndex);
		Preconditions.checkArgument(maxResults > 0, "maxResults is %s but it must be greater then zero.", maxResults);
		BoundSql boundSql = getBoundSql(statemenKey);
		return getExtendedJdbcTemplate().query(boundSql.getSql(), startIndex, maxResults ,elementType, params);
	}

	@Override
	public int executeUpdate(String statemenKey) {
		BoundSql boundSql = getBoundSql(statemenKey);
		return getExtendedJdbcTemplate().update(boundSql.getSql());
	}

	@Override
	public int executeUpdate(String statemenKey, Object... parameters) {
		BoundSql boundSql = getBoundSql(statemenKey);
		return getExtendedJdbcTemplate().update(boundSql.getSql(), parameters);
	}

	@Override
	public Object executeScript(String statemenKey, boolean stopOnError) {
		BoundSql boundSql = getBoundSql(statemenKey);
		return getExtendedJdbcTemplate().executeScript(stopOnError, new StringReader(boundSql.getSql()));
	}

	@Override
	public Object call(String statemenKey, Object... parameters) {
		BoundSql boundSql = getBoundSql(statemenKey);		
		List<SqlParameter> declaredParameters = new ArrayList<SqlParameter>();
		Map<String, Object> paramsToUse = new HashMap<String, Object>();
		
		// 메핑 파라메터에 따라 INPUT 과 OUTPU 을 설정한다.
		for( ParameterMapping mapping : boundSql.getParameterMappings()){			
			mapping.getProperty(); 
			mapping.getJdbcType();
			mapping.getMode();			
			if( mapping.getMode() == ParameterMapping.Mode.IN ){				
				SqlParameter input = new SqlParameter(mapping.getProperty(), mapping.getJdbcType().ordinal());
				declaredParameters.add(input);
				paramsToUse.put(mapping.getProperty(), parameters[mapping.getIndex() -1 ]);
				
			}else if (mapping.getMode() == ParameterMapping.Mode.OUT){
				SqlOutParameter output = new SqlOutParameter(mapping.getProperty(), mapping.getJdbcType().ordinal());
				declaredParameters.add(output);
			}			
		}		
		CallableStatementCreatorFactory callableStatementFactory = new CallableStatementCreatorFactory(boundSql.getSql(), declaredParameters);		
		return getExtendedJdbcTemplate().call(callableStatementFactory.newCallableStatementCreator(paramsToUse), declaredParameters );
	
	}

}