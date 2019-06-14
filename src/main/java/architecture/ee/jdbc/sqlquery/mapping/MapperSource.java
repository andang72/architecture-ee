package architecture.ee.jdbc.sqlquery.mapping;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;

import architecture.ee.util.NumberUtils;
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
		//log.debug( mapping);
		if( !StringUtils.isEmpty(mapping.getColumn())){
		    this.mappedFieldsMap.put( mapping.getColumn(), mapping );
		}
	    }
	}
	
	protected Object getColumnValue(ResultSet rs, int index, ParameterMapping pm) throws SQLException {	
		if( pm.getJavaType() == String.class ) { 
			String value = StringUtils.defaultString( rs.getString(index), null ); 
			if( value != null ) {
				if(!StringUtils.isNullOrEmpty( pm.getCipher())){		
					try {
						Cipher cipher = Cipher.getInstance(pm.getCipher());
						SecretKeySpec skeySpec = new SecretKeySpec(Hex.decodeHex(pm.getCipherKey().toCharArray()), pm.getCipherKeyAlg());
						cipher.init(Cipher.DECRYPT_MODE, skeySpec);
						 byte raw[] ;
						if(!StringUtils.isEmpty( pm.getEncoding())){
							String enc = pm.getEncoding();
							if(enc.toUpperCase().equals("BASE64")){
						        raw = Base64.decodeBase64(value);
							}else if(enc.toUpperCase().equals("HEX")){
								raw = Hex.decodeHex(value.toCharArray());
							}else{
								raw = value.getBytes();
							}
						}else{
							raw= value.getBytes();
						}
				        byte stringBytes[] = cipher.doFinal(raw);
				        return new String(stringBytes);	 
					} catch (Exception e) {
						log.error(e);
					}
				}
				else if( StringUtils.isNullOrEmpty( pm.getCipher()) && !StringUtils.isNullOrEmpty(pm.getEncoding()))
				{	
					String [] encoding = StringUtils.split(pm.getEncoding() , ">");
					try {	
						if( encoding.length == 2 )
						{
							return new String(value.getBytes(encoding[0]), encoding[1]); 
						}
						else if (encoding.length == 1 ) { 
							return new String(value.getBytes(), encoding[0]); 
						}	
					} catch (UnsupportedEncodingException e) { 
						log.error(e);
					} 
				}
			}
		}else if (pm.getJavaType() == Boolean.class ) {
			if( NumberUtils.toInt(rs.getString(index), 0) == 1 )
				return Boolean.TRUE;
			else 
				return Boolean.FALSE;
		} 
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
		   // log.debug( "[" + index + "] " + column );
		    if( getMappedFieldsAsMap().containsKey(column) ){
			ParameterMapping mapping = getMappedFieldsAsMap().get(column);
			//log.debug( mappedClass.getName() + " set " + mapping.getProperty() + "="+ getColumnValue(rs, index, mapping) );
			bw.setPropertyValue(mapping.getProperty(), getColumnValue(rs, index, mapping));
		    }
		}		
	    //}	    
	    return mappedObject;
	}

    }
}