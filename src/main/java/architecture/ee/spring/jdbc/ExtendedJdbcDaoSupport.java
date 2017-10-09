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
package architecture.ee.spring.jdbc;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;

import architecture.ee.jdbc.sqlquery.factory.Configuration;
import architecture.ee.jdbc.sqlquery.mapping.BoundSql;
import architecture.ee.jdbc.sqlquery.mapping.MappedStatement;

public class ExtendedJdbcDaoSupport extends JdbcDaoSupport {

	@Autowired
	@Qualifier("sqlConfiguration")
	private Configuration sqlConfiguration;

	final protected Logger logger = LoggerFactory.getLogger(getClass());

	private LobHandler lobHandler = new DefaultLobHandler();

	public ExtendedJdbcDaoSupport() {
		super();
	}

	public ExtendedJdbcDaoSupport(Configuration sqlConfiguration) {
		super();
		this.sqlConfiguration = sqlConfiguration;
	}

	public void setSqlConfiguration(Configuration sqlConfiguration) {
		this.sqlConfiguration = sqlConfiguration;
	}

	/**
	 * Create a ExtendedJdbcTemplate for the given DataSource. Only invoked if
	 * populating the DAO with a DataSource reference!
	 * 
	 * @param dataSource
	 *            the JDBC DataSource to create a ExtendedJdbcTemplate for
	 * @return the new ExtendedJdbcTemplate instance
	 * @see #setDataSource
	 */
	protected JdbcTemplate createJdbcTemplate(DataSource dataSource) {
		return new ExtendedJdbcTemplate(dataSource);
	}

	public LobHandler getLobHandler() {
		return lobHandler;
	}

	public void setLobHandler(LobHandler lobHandler) {
		this.lobHandler = lobHandler;
	}

	public BoundSql getBoundSql(String statement) {
		if (isSetConfiguration()) {
			MappedStatement stmt = sqlConfiguration.getMappedStatement(statement);
			return stmt.getBoundSql(null);
		}
		return null;
	}

	public BoundSql getBoundSql(String statement, Object... params) {
		if (isSetConfiguration()) {
			MappedStatement stmt = sqlConfiguration.getMappedStatement(statement);
			return stmt.getBoundSql(params);
		}
		return null;
	}

	public BoundSql getBoundSqlWithAdditionalParameter(String statement, Object additionalParameter) {
		if (isSetConfiguration()) {
			MappedStatement stmt = sqlConfiguration.getMappedStatement(statement);
			return stmt.getBoundSql(null, additionalParameter);
		}
		return null;
	}

	public BoundSql getBoundSqlWithAdditionalParameter(String statement, Object parameters,
			Object additionalParameter) {
		if (isSetConfiguration()) {
			MappedStatement stmt = sqlConfiguration.getMappedStatement(statement);
			return stmt.getBoundSql(parameters, additionalParameter);
		}
		return null;
	}

	public ExtendedJdbcTemplate getExtendedJdbcTemplate() {
		return (ExtendedJdbcTemplate) getJdbcTemplate();
	}
		
	public boolean isSetConfiguration() {
		if (sqlConfiguration == null)
			return false;
		else
			return true;
	}

}
