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
package architecture.ee.jdbc.property.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import architecture.ee.jdbc.property.dao.PropertyDao;
import architecture.ee.util.StringUtils;

public class JdbcPropertyDao extends JdbcDaoSupport implements PropertyDao {


	private String PROPERTY_NAME_COLUMN_NAME = "PROPERTY_NAME";
	
	private String PROPERTY_VALUE_COLUMN_NAME = "PROPERTY_VALUE";
		
	public JdbcPropertyDao() {
		
	}
	
	public void setPropertyNameColumnName(String columnName) {
		PROPERTY_NAME_COLUMN_NAME = columnName;
	}

	public void setPropertyValueColumnName(String columnName) {
		PROPERTY_VALUE_COLUMN_NAME = columnName;
	}

	public Map<String, String> getProperties(String table, String typeField, long objectId) {		
		StringBuilder builder = new StringBuilder("SELECT ").append(PROPERTY_NAME_COLUMN_NAME).append(", ").append(PROPERTY_VALUE_COLUMN_NAME).append(" FROM ").append(table);
		builder.append(" WHERE ");
		builder.append(typeField).append(" =?");

		return getJdbcTemplate().query(builder.toString(), new ResultSetExtractor<Map<String, String>>(){
			public Map<String, String> extractData(ResultSet rs) throws SQLException, DataAccessException {				
				Map<String, String> rows = new HashMap<String, String>();				
				while(rs.next()){
					String key = rs.getString(1);
					String value = rs.getString(2);
					rows.put(key, value);
				}
				return rows;
			}}, new SqlParameterValue(Types.NUMERIC, objectId));
	}

	public void deleteProperties(String table, String typeField, long objectId) {
		StringBuilder builder = new StringBuilder("DELETE FROM ").append(table).append(" WHERE ").append(typeField).append(" = ?");
		getJdbcTemplate().update(builder.toString(),  new SqlParameterValue(Types.NUMERIC, objectId));
	}

	public void updateProperties(String table, String keyField, long objectId, Map<String, String> properties) {
		if(StringUtils.isEmpty(table))
            throw new IllegalArgumentException("Table to update properties for cannot be null or empty.");
        if(StringUtils.isEmpty(keyField))
            throw new IllegalArgumentException("Column specifying key cannot be null or empty.");
        if(properties == null)
        {
            deleteProperties(table, keyField, objectId);
        } else
        {
        	String sql = (new StringBuilder("INSERT INTO ")).append(table).append(" ( ").append(keyField).append(", " + PROPERTY_NAME_COLUMN_NAME + ", "+  PROPERTY_VALUE_COLUMN_NAME +") VALUES (?, ?, ?)").toString();
        	
        	final List<Object[]> copy = new ArrayList<Object[]>(properties.size());
        	
        	Set<Map.Entry<String, String>> set = properties.entrySet();
        	//SqlParameterSource[] batchArgs = new SqlParameterSource[set.size()];
        	
        	for( Map.Entry<String, String> entry : set ){
                String key = entry.getKey();
                String value = entry.getValue();
                if(!StringUtils.isEmpty(key) && !StringUtils.isEmpty(value)){
                	copy.add(new Object[]{objectId, key, value});
                }
        	}
        	        	
        	deleteProperties(table, keyField, objectId);
        	
        	if(!copy.isEmpty())        	
        		getJdbcTemplate().batchUpdate(sql, copy, new int[]{ Types.INTEGER, Types.VARCHAR, Types.VARCHAR} );
        }
        
	}
}
