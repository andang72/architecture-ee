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
package architecture.ee.spring.jdbc.datasource;

import javax.sql.DataSource;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.MethodInvoker;

import architecture.ee.jdbc.datasource.DefaultDataSourceFactory;
import architecture.ee.util.StringUtils;

public class DataSourceFactoryBean extends DefaultDataSourceFactory implements FactoryBean<DataSource>, DisposableBean {
	
	private DataSource dataSource;
	
	public void destroy() throws Exception {
		if( dataSource != null){
			String packageName = ClassUtils.getPackageName(dataSource.getClass());
			if( StringUtils.startsWithIgnoreCase( packageName, "org.apache.commons.dbcp") || StringUtils.startsWithIgnoreCase ( packageName, "org.apache.commons.dbcp2") ){
				MethodInvoker invoker = new MethodInvoker();
				invoker.setTargetObject(dataSource);
				invoker.setTargetMethod("close");
				invoker.prepare();
				invoker.invoke();
			}
		}
	}

	public DataSource getObject() throws Exception {
		if( dataSource == null )
			dataSource = getDataSource();		
		return dataSource;
	}

	public Class<DataSource> getObjectType() {
		return DataSource.class;
	}

	public boolean isSingleton() {
		return false;
	}

}