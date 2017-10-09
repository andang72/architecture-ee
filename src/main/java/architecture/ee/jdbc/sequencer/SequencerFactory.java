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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import architecture.ee.i18n.CommonLogLocalizer;
import architecture.ee.jdbc.sequencer.annotation.MaxValue;
import architecture.ee.jdbc.sqlquery.factory.Configuration;
import architecture.ee.service.ConfigService;
import architecture.ee.util.StringUtils;

/**
 * 데이터베이스를 사용하여 고유한 ID 시퀀스를 관리한다.
 * 
 * @author donghyuck
 *
 */
public class SequencerFactory {

	private Logger logger = LoggerFactory.getLogger(getClass().getName());

	private final ConcurrentMap<Integer, Sequencer> sequencersById = new ConcurrentHashMap<Integer, Sequencer>();
	
	private final ConcurrentMap<String, Sequencer> sequencersByName = new ConcurrentHashMap<String, Sequencer>();

	@Autowired
	@Qualifier("sqlConfiguration")
    private Configuration sqlConfiguration;	
	
	@Autowired(required = false)
	@Qualifier("dataSource")
	private DataSource dataSource;	
	
	@Autowired(required = false)
	@Qualifier("configService")
	private ConfigService configService;	
	
		
	public void setSqlConfiguration(Configuration sqlConfiguration) {
		this.sqlConfiguration = sqlConfiguration;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public int getType(String name){
		if( StringUtils.isNullOrEmpty( name ))
			throw new IllegalArgumentException(CommonLogLocalizer.getMessage("003101"));		
		if (sequencersByName.containsKey(name)) {
			return sequencersByName.get(name).getType();
		}
		return -1;
	}
	
	public long getNextValue(String name) {
		if( StringUtils.isNullOrEmpty( name ))
			throw new IllegalArgumentException(CommonLogLocalizer.getMessage("003101"));		
		if (sequencersByName.containsKey(name)) {
			return sequencersByName.get(name).getNextValue();
		} else {
			
			logger.debug(CommonLogLocalizer.format("003105", null, name));
			
			int blockSize = 1 ;
			if(configService!=null)
				blockSize = configService.getLocalProperty("services.sequencer.blockSize", blockSize);
			JdbcSequencer sequencer = new JdbcSequencer(name, blockSize);
			sequencer.setDataSource(dataSource);
			sequencer.setSqlConfiguration(sqlConfiguration);	
			
			sequencersByName.put(name, sequencer);
			
			return sequencer.getNextValue();
		}
	}

	public long getNextValue(int type) {
		if (sequencersById.containsKey(type)) {
			return sequencersById.get(type).getNextValue();
		} else {
			// Verify type is valid from the db, if so create an instance for
			// the type
			// And return the next unique id
			logger.debug(CommonLogLocalizer.format("003105", type, null));
			
			int blockSize = 1 ;
			if(configService!=null)
				blockSize = configService.getLocalProperty("services.sequencer.blockSize", blockSize);
			
			JdbcSequencer sequencer = new JdbcSequencer(type, blockSize);
			sequencer.setDataSource(dataSource);
			sequencer.setSqlConfiguration(sqlConfiguration);		
			sequencersById.put(type, sequencer);			
			return sequencer.getNextValue();
		}
	}
	
	public long getNextValue(int type, String name){		
		if (sequencersByName.containsKey(name)) {
			return sequencersByName.get(name).getNextValue();		
		}
		
		int blockSize = 1 ;		
		if(configService!=null)
			blockSize = configService.getLocalProperty("services.sequencer.blockSize", blockSize);			
		JdbcSequencer sequencer = new JdbcSequencer(type, name, blockSize);;
		sequencer.setDataSource(dataSource);
		sequencer.setSqlConfiguration(sqlConfiguration);		
		sequencersByName.put(name, sequencer);		
		return sequencer.getNextValue();
	}

	public long getNextValue(Object object) {
		MaxValue max = object.getClass().getAnnotation(MaxValue.class);
		if (max == null) {
			String msg = CommonLogLocalizer.format("003106", object.getClass());
			logger.error(msg);
			throw new IllegalArgumentException(msg);
		}		
		return getNextValue(max.id(), max.name());
		
	}
	
	public static class Builder{		
		public static SequencerFactory build() {
		    return new SequencerFactory();
		}
		
	}
}
