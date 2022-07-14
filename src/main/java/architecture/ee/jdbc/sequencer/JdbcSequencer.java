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
package architecture.ee.jdbc.sequencer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;

import architecture.ee.i18n.CommonLogLocalizer;
import architecture.ee.jdbc.sqlquery.factory.Configuration;
import architecture.ee.jdbc.sqlquery.mapping.BoundSql;
import architecture.ee.jdbc.sqlquery.mapping.MappedStatement;
import architecture.ee.util.StringUtils;

public class JdbcSequencer implements Sequencer {
	
	@Autowired
	@Qualifier("sqlConfiguration")
    private Configuration sqlConfiguration;	

	private DataSource dataSource;
		
	private Logger logger = LoggerFactory.getLogger(getClass());	
	
    private long currentId;
    private long maxId;
    private int blockSize;
    private String name;
    private int type;
    
    /**
     * Creates a new JdbcSequencer.
     *
     * @param seqType the type of sequence.
     * @param size the number of id's to "checkout" at a time.
     */
    public JdbcSequencer(int seqType, int size) {
        this.type = seqType;
        this.blockSize = size;
        currentId = 0l;
        maxId = 0l;
        name = null;
    }

    public JdbcSequencer(String name, int size) {
        this.type = 0;
        this.blockSize = size;
        currentId = 0l;
        maxId = 0l;
        this.name = name;
        if( StringUtils.isNullOrEmpty( name ))
        	throw new IllegalArgumentException(CommonLogLocalizer.getMessage("003101"));        
    }
      
    
    public JdbcSequencer(int type, String name, int size) {
		this.type = type;
		this.name = name;
		this.blockSize = size;
        this.currentId = 0l;
        this.maxId = 0L;
	}

	public int getType() {
		return type;
	}

 
	public String getName() {
		return name;
	}
 

	public Configuration getSqlConfiguration() {
		return sqlConfiguration;
	}

	public void setSqlConfiguration(Configuration sqlConfiguration) {
		this.sqlConfiguration = sqlConfiguration;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
     * Returns the next available unique ID. Essentially this provides for the functionality of an
     * auto-increment database field.
     */
    public synchronized long getNextValue() {
        if (!(currentId < maxId)) {
            // Get next block -- make 5 attempts at maximum.
            getNextBlock(5);
        }
        long id = currentId;
        currentId++;
        return id;
    }

    /**
     * Performs a lookup to get the next available ID block. The algorithm is as follows:
     * <ol>
     * <li> Select currentID from appropriate db row.
     * <li> Increment id returned from db.
     * <li> Update db row with new id where id=old_id.
     * <li> If update fails another process checked out the block first; go back to step 1.
     * Otherwise, done.
     * </ol>
     */
    private void getNextBlock(int count) {
        if (count == 0) {
            logger.error(CommonLogLocalizer.getMessage("003102"));
            return;
        } 
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean success = false;
        
        try {
        	long currentID = 1; 
        	con = DataSourceUtils.getConnection(getDataSource());
            if( type == 0 )
            {
                pstmt = con.prepareStatement(getBoundSqlText("FRAMEWORK_EE.SELECT_SEQUENCER_BY_NAME"));
            	DataSourceUtils.applyTransactionTimeout(pstmt, getDataSource());
            	pstmt.setString(1, name);
            	rs = pstmt.executeQuery();
            	//logger.debug(getBoundSql("FRAMEWORK_EE.SELECT_SEQUENCER_BY_NAME").getSql());
            	if( rs.next() ){
            		this.type = rs.getInt(1);
            		currentID = rs.getLong(3);
            	}else{
            		 createNewID(con, type);
            	}
                JdbcUtils.closeResultSet(rs);
                JdbcUtils.closeStatement(pstmt);
            }else{
                // Get the current ID from the database.
                pstmt = con.prepareStatement(getBoundSqlText("FRAMEWORK_EE.SELECT_SEQUENCER_BY_ID"));
    	        DataSourceUtils.applyTransactionTimeout(pstmt, getDataSource());         
    	        pstmt.setInt(1, type);
    	        rs = pstmt.executeQuery();
                //logger.debug(getBoundSql("FRAMEWORK_EE.SELECT_SEQUENCER_BY_NAME").getSql());
    	        if (rs.next()) {
                    currentID = rs.getLong(1);
                }
                else {
                    createNewID(con, type);
                }
                JdbcUtils.closeResultSet(rs);
                JdbcUtils.closeStatement(pstmt);
            }
            
            // Increment the id to define our block.
            long newID = currentID + blockSize;
            // The WHERE clause includes the last value of the id. This ensures
            // that an update will occur only if nobody else has performed an
            // update first.

            pstmt = con.prepareStatement(getBoundSqlText("FRAMEWORK_EE.UPDATE_SEQUENCER"));
            pstmt.setLong(1, newID);
            pstmt.setInt(2, type);
            pstmt.setLong(3, currentID);
            // Check to see if the row was affected. If not, some other process
            // already changed the original id that we read. Therefore, this
            // round failed and we'll have to try again.
            success = pstmt.executeUpdate() == 1;
            if (success) {
                this.currentId = currentID;
                this.maxId = newID;
            }
        }
        catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new DataAccessResourceFailureException(CommonLogLocalizer.getMessage("003103"), e);
        }
        finally {
            JdbcUtils.closeResultSet(rs);
            JdbcUtils.closeStatement(pstmt);
            
            try {
				con.commit();
			} catch (SQLException e) { 
			}
            
            DataSourceUtils.releaseConnection(con, getDataSource());
        }

        if (!success) {
            logger.warn(CommonLogLocalizer.getMessage("003104"));
            // Call this method again, but sleep briefly to try to avoid thread contention.
            try {
                Thread.sleep(75);
            }
            catch (InterruptedException ie) {
                // Ignore.
            }
            getNextBlock(count - 1);
        }
    }

    private void createNewID(Connection con, int type) throws SQLException {
    	if( logger.isWarnEnabled() ){
            logger.warn(CommonLogLocalizer.format("003105", type, name)); 
        } 
        // create new ID row
        PreparedStatement pstmt = null;
        try {
        	if(type == 0 && !StringUtils.isNullOrEmpty(name)){
        		pstmt = con.prepareStatement(getBoundSqlText("FRAMEWORK_EE.SELECT_SEQUENCER_MAX_ID"));   
        		ResultSet rs = pstmt.executeQuery();
        		if( rs.next() ){
        			this.type = rs.getInt(1);
        		}else{
        			this.type = 1 ;
        		}
        		JdbcUtils.closeResultSet(rs);
                JdbcUtils.closeStatement(pstmt);
        	}        	
        	
            pstmt = con.prepareStatement(getBoundSqlText("FRAMEWORK_EE.CREATE_SEQUENCER"));
            pstmt.setInt(1, this.type);
			pstmt.setLong(1, 1L);
			pstmt.setString(2, this.name);
			pstmt.setInt(3, this.type );
			
            pstmt.execute();  
        }
        finally { 
        	JdbcUtils.closeStatement(pstmt);
        }
    }
    
    private String getBoundSqlText(String statement){ 
        BoundSql sql = getBoundSql(statement); 
        if( sql != null ){
           return sql.getSql();
        }
        return null ;
    }

	private BoundSql getBoundSql(String statement){ 
		if ( this.sqlConfiguration != null ) {
			MappedStatement stmt = sqlConfiguration.getMappedStatement(statement);
			return stmt.getBoundSql(null);
		} 
        return null;
	}
}
