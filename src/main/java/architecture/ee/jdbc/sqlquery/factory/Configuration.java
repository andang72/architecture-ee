/*
 * Copyright 2016 donghyuck
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

package architecture.ee.jdbc.sqlquery.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import architecture.ee.i18n.FrameworkLogLocalizer;
import architecture.ee.jdbc.sqlquery.MapperNotFountException;
import architecture.ee.jdbc.sqlquery.SqlNotFoundException;
import architecture.ee.jdbc.sqlquery.factory.impl.StaticModels;
import architecture.ee.jdbc.sqlquery.mapping.MappedStatement;
import architecture.ee.jdbc.sqlquery.mapping.MapperSource;
import architecture.ee.jdbc.sqlquery.parser.XNode;
import architecture.ee.jdbc.sqlquery.type.TypeAliasRegistry;
import architecture.ee.util.StringUtils;


public class Configuration {

	public static final String DEFAULT_FILE_SUFFIX = "sqlset.xml";
	
	private String prefix = "";

    private String suffix = DEFAULT_FILE_SUFFIX; 
    
	protected Integer defaultStatementTimeout;

	protected final TypeAliasRegistry typeAliasRegistry = new TypeAliasRegistry();

	protected Properties variables = new Properties();

	protected final Set<String> loadedResources = new HashSet<String>();
	 
	protected final BiMap<String, MapperSource> mappers = HashBiMap.create();
	
	 
	/**
     * 파싱되어 매핑된 스테이트 객체들이 저장되는 위치. 다중키는 아파치 commons-collections 패키지에서 제공하는
     * MultiKey (namespace + id) 을 사용하여 구현함. 다중키를 스트링 조합으로 변경함.
     * 저장소는 양방향 키 사용이 가능한 BiMap 를 사용.
     * 
     */
	protected final BiMap<String, MappedStatement> mappedStatements = HashBiMap.create();

	// protected final BiMap<String, ParameterMap> parameterMaps =
	// HashBiMap.create();

	/** A map holds statement nodes for a namespace. */
	protected final ConcurrentMap<String, List<XNode>> statementNodesToParse = new ConcurrentHashMap<String, List<XNode>>();

	protected final ConcurrentMap<String, List<XNode>> mapperNodesToParse = new ConcurrentHashMap<String, List<XNode>>();
    
	
	public void addLoadedResource(String resource) {
		loadedResources.add(resource);
	}

	public void addMapperNodes(String namespace, List<XNode> nodes) {
		mapperNodesToParse.put(namespace, nodes);
	}

    public void addMapper(MapperSource source) {
    	mappers.put(source.getID(), source);
    }
    
	public void addMappedStatement(MappedStatement statement) {
		mappedStatements.put(statement.getId(), statement);
	}

	public void addStatementNodes(String namespace, List<XNode> nodes) {
		statementNodesToParse.put(namespace, nodes);
	}
	
	protected void buildAllStatements() {			
			
	}
	
	/**
	 * Extracts namespace from fully qualified statement id.
	 * 
	 * @param statementId
	 * @return namespace or null when id does not contain period.
	 */
	protected String extractNamespace(String statementId) {
		int lastPeriod = statementId.lastIndexOf('.');
		return lastPeriod > 0 ? statementId.substring(0, lastPeriod) : null;
	}

	public Integer getDefaultStatementTimeout() {
		return defaultStatementTimeout;
	}

	public MappedStatement getMappedStatement(String id) {
	
		if( !StringUtils.isNullOrEmpty(id) && mappedStatements.containsKey(id) ){
			return mappedStatements.get(id);
		}
		throw new SqlNotFoundException( FrameworkLogLocalizer.format("003059", id));
	}

	public List<String> getMapperNames(){
		List<String> list = new ArrayList<String>();
		mappers.keySet().addAll(list);
		return list;
	}
    public MapperSource getMapper(String id) {
		if (!this.mappers.containsKey(id)) {
			return mappers.get(id);
		}
		if (!mappers.containsKey(id))
			throw new MapperNotFountException(FrameworkLogLocalizer.format("003060", id)); //L10NUtils.format("003281", id));
		return mappers.get(id);
    }
 
	public Set<MappedStatement> getMappedStatements() {
		buildAllStatements();
		return mappedStatements.values();
    }

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public TypeAliasRegistry getTypeAliasRegistry() {
		return typeAliasRegistry;
	}
				
	public boolean hasStatement(String statementName) {
		return hasStatement(statementName, true);
	}

	public boolean hasStatement(String statementName, boolean validateIncompleteStatements) {
		if (validateIncompleteStatements) {
			buildAllStatements();
		}
		return mappedStatements.containsKey(statementName);
	}

	public boolean isResourceLoaded(String resource) {
		return loadedResources.contains(resource);
	}

	public void removeLoadedResource(String resource) {
		loadedResources.remove(resource);
	}
	  
	
	public void setDefaultStatementTimeout(Integer defaultStatementTimeout) {
		this.defaultStatementTimeout = defaultStatementTimeout;
	}

	public void setPrefix(String prefix) {
		this.prefix = (prefix != null ? prefix : "");
	}

	public void setSuffix(String suffix) {
		this.suffix = (suffix != null ? suffix : "");
	}
}
