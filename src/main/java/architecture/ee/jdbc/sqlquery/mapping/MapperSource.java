package architecture.ee.jdbc.sqlquery.mapping;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import architecture.ee.util.StringUtils;

public class MapperSource{

    private String ID;
    
    private String name;
    
    private List<ParameterMapping> mappedFields;

    private Class<?> mappedClass;
    
    public String getName(){
	return name;
    }
        
    public String getID() {
        return ID;
    }

    public void setID(String iD) {
        ID = iD;
    }

    public List<ParameterMapping> getMappedFields() {
        return mappedFields;
    }

    
    public <T> RowMapper<T> createRowMapper(Class<T> requiredType){
		ParameterMappingRowMapper<T> mapper = new ParameterMappingRowMapper<T>(requiredType);
		mapper.mapperSource = this;
		mapper.prepare();
		return mapper;
    }

    
    public static class Builder {
		
    	private MapperSource mappedRowMapper = new MapperSource();
		
    	public Builder(Class mappedClass, List<ParameterMapping> mappedFields) {
		    mappedRowMapper.mappedClass = mappedClass;
		    mappedRowMapper.mappedFields = mappedFields;
		}	
		
		public Builder name(String name) {
		    mappedRowMapper.name = name;
		    return this;
		}
		
		public MapperSource build() {
		    assert mappedRowMapper.mappedClass != null;
		    assert mappedRowMapper.mappedFields != null;
		    return mappedRowMapper;
		}
    }
        

    public static class ParameterMappingRowMapper<T> implements RowMapper<T> {
	/** The class we are mapping to */
	
	private static final Log log = LogFactory.getLog(ParameterMappingRowMapper.class);
	
	private Class<T> mappedClass;
	
	private MapperSource mapperSource;
	
	private Map<String, ParameterMapping> mappedFieldsMap;

	public ParameterMappingRowMapper() {
	    
	}
	
	public ParameterMappingRowMapper(Class<T> mappedClass) {
	    this.mappedClass = mappedClass;
	}
	
	public final Class<T> getMappedClass() {
	   return this.mappedClass;
	}
	
	protected void prepare(){
	    this.mappedFieldsMap = new HashMap<String, ParameterMapping>();
	    for (ParameterMapping mapping : mapperSource.mappedFields) {
		log.debug( mapping);
		if( !StringUtils.isEmpty(mapping.getColumn())){
		    this.mappedFieldsMap.put( mapping.getColumn(), mapping );
		}
	    }
	}
	
	protected Object getColumnValue(ResultSet rs, int index, ParameterMapping pm) throws SQLException {	
	    return JdbcUtils.getResultSetValue(rs, index, pm.getJavaType());
	}

	protected Map<String, ParameterMapping> getMappedFieldsAsMap(){
	    return mappedFieldsMap;	    
	}
	
	public T mapRow(ResultSet rs, int rowNum) throws SQLException {	    
	    T mappedObject = BeanUtils.instantiateClass(mapperSource.mappedClass, mappedClass );
	    BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
	    
	    ResultSetMetaData rsmd = rs.getMetaData();
	    int columnCount = rsmd.getColumnCount();
	    //for (ParameterMapping mapping : mapperSource.mappedFields) {
		for (int index = 1; index <= columnCount; index++) {
		    String column = JdbcUtils.lookupColumnName(rsmd, index);
		    log.debug( "[" + index + "] " + column );
		    if( getMappedFieldsAsMap().containsKey(column) ){
			ParameterMapping mapping = getMappedFieldsAsMap().get(column);
			log.debug( mappedClass.getName() + " set " + mapping.getProperty() + "="+ getColumnValue(rs, index, mapping) );
			bw.setPropertyValue(mapping.getProperty(), getColumnValue(rs, index, mapping));
		    }
		}		
	    //}	    
	    return mappedObject;
	}

    }
}