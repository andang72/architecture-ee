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
package architecture.ee.jdbc.sqlquery;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

public interface SqlQuery {

	public static final int DEFAULT_START_INDEX = 0;
	
	public static final int DEFAULT_MAX_RESULTS = -1;
	
	/**
     * 사용할 데이터소스를 설정한다.
     * 
     * @param dataSource
     * @return
     */
    public void setDataSource(DataSource dataSource);
    
    
    
    /**
     * list() 함수 호출시에 데이터 페이징 처리를 위하여 사용된다. 쿼리 결과의 startIndex 부터
     * 데이터를 읽어 데이터를 리턴한다. 쿼리 실행후에 startIndex 값은 0 으로 초기화 된다.
     * @param startIndex
     * @return
     */
    public SqlQuery setStartIndex(int startIndex);
    
    /**
     * list() 함수 호출시에 데이터 페이징 처리를 위하여 사용된다. 쿼리 결과의 startIndex 부터
     * maxResults 까지의 데이터를 읽어 데이터를 리턴한다. 쿼리 실행후에 maxResults 값은 -1 으로 초기화되며, 
     * -1 값은 페이지 처리를 하지 않는 것과 동일한 결과를 가져온다.
     * 
     * @param maxResults
     * @return
     */
    public SqlQuery setMaxResults(int maxResults);
        
    public <T> List<T> queryForList(String statemenKey, Class<T> elementType);
    
    public <T> List<T> queryForList(String statemenKey, Class<T> elementType, Object... params );
    
    public <T> List<T> queryForList(String statemenKey, int startIndex, int maxResults, Class<T> elementType);
    
    public <T> List<T> queryForList(String statemenKey, int startIndex, int maxResults, Class<T> elementType, Object... params );
    
    public List<Map<String, Object>> queryForList(String statemenKey);
    
    public List<Map<String, Object>> queryForList(String statemenKey, Object... params );
    
    public List<Map<String, Object>> queryForList(String statemenKey, int startIndex, int maxResults);
    
    public List<Map<String, Object>> queryForList(String statemenKey, int startIndex, int maxResults, Object... params );    
    
    public <T> T queryForObject(String statemenKey, Class<T> elementType, Object... params );
    
    public Map<String, Object> queryForObject(String statemenKey, Object... params );
    
	public int executeUpdate(String statement);
	
	public int executeUpdate(String statement, Object... params);
	
	/**
	 * 스크립트 모드로 쿼리를 실행한다.
	 * @param statement
	 * @return
	 */
	public Object executeScript(String statemenKey, boolean stopOnError) ;
	
	public Object call(String statement, Object... params);
	
    
}
