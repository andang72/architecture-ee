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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;

public class ExtendedJdbcUtils extends JdbcUtils {
	
	private static final Logger log = LoggerFactory.getLogger(ExtendedJdbcUtils.class);
	
	public enum DB {
		
		ORACLE, 
		POSTGRESQL, 
		MYSQL, 
		DB2, 
		SQLSERVER, 
		UNKNOWN; 
		
		boolean scrollResultsSupported = false;
		boolean fetchSizeSupported = false;
		
		public boolean isScrollResultsSupported() {
			return scrollResultsSupported;
		}
		public boolean isFetchSizeSupported() {
			return fetchSizeSupported;
		} 
		
	}
	

	private static final Logger logger = LoggerFactory.getLogger(ExtendedJdbcUtils.class); 
	
	public static Boolean tableExists ( DataSource dataSource , String tableName ) throws SQLException  {
		
		Connection con =  dataSource.getConnection();
		try {
			return tableExists(con, tableName);
		}finally {
			closeConnection(con);
		}
	}
	
	
	protected static boolean tableExists(final Connection connection, final String tableName) throws SQLException {
		Boolean result = false;
		if (connection != null) {
			ResultSet tables = connection.getMetaData().getTables(null, null, tableName, null); 
			while (tables.next()) { 
			
				String currentTableName = tables.getString("TABLE_NAME");
				if(logger.isDebugEnabled()){
					logger.debug("extracting tablename : {}", currentTableName.replaceAll("[\r\n]", "") );
				} 
				if (currentTableName.equals(tableName)) {
					result = true;
				}
				
			}
			tables.close(); 
			ResultSet rs = connection.getMetaData().getTables(null, null, tableName, null); 
			if (rs.next()) {
			      System.out.println("Table exists"); 
			    } else {
			      System.out.println("Table does not exist"); 
			    }
			
		}
		return result;
	}

	public static DB extractDB(Connection con) {
		DB db = DB.UNKNOWN; 
		try {
			db = extractDB(con.getMetaData());

		} catch (SQLException ex) {
			logger.debug("JDBC driver 'extractDB' method threw exception", ex);
		}
		return db;
	}

	public static DB extractDB (DatabaseMetaData dbmd) {
		DB db = DB.UNKNOWN;
		try {
		String dbName = commonDatabaseName(dbmd.getDatabaseProductName()).toLowerCase();
		String driverName = dbmd.getDriverName().toLowerCase();
		
		boolean scrollResultsSupported = scrollResultsSupported(dbmd);
		boolean fetchSizeSupported = true;
		
		if (dbName.indexOf("oracle") != -1) {
			db = DB.ORACLE;
		} else if (dbName.indexOf("db2") != -1) {
			db = DB.DB2;
		} else if (dbName.indexOf("mysql") != -1) {
			db = DB.MYSQL;
		} else if (dbName.indexOf("sql server") != -1) {
			db = DB.SQLSERVER;
			if (driverName.indexOf("una") != -1) {
				fetchSizeSupported = true;
			} else if (driverName.indexOf("jtds") != -1) {
				fetchSizeSupported = true;
			} else {
				fetchSizeSupported = false;
				scrollResultsSupported = false;
			}

		} else if (dbName.indexOf("postgres") != -1) {
			db = DB.POSTGRESQL;
			fetchSizeSupported = false;
		}
		db.scrollResultsSupported = scrollResultsSupported;
		db.fetchSizeSupported = fetchSizeSupported;
		
		} catch (SQLException ex) {
			logger.debug("JDBC driver 'extractDB' method threw exception", ex);
		} 
		return db;
	}
	
	public static boolean scrollResultsSupported(Connection con) {
		try {
			DatabaseMetaData dbmd = con.getMetaData();
			scrollResultsSupported(dbmd);
		} catch (SQLException ex) {
			logger.debug("JDBC driver 'scrollResultsSupported' method threw exception", ex);
		}
		return false;
	}

	public static boolean scrollResultsSupported(DatabaseMetaData dbmd) {
		try {
			if (dbmd != null) {
				if (dbmd.supportsResultSetType((ResultSet.TYPE_SCROLL_INSENSITIVE))) {
					logger.debug("JDBC driver supports scroll results");
					return true;
				} else {
					logger.debug("JDBC driver does not support scroll results");
				}
			}
		} catch (SQLException ex) {
			logger.debug("JDBC driver 'scrollResultsSupported' method threw exception", ex);
		}
		return false;
	}
}
