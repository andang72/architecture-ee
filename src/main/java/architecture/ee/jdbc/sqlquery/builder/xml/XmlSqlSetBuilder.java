/**
 *    Copyright 2015-2016 donghyuck
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
package architecture.ee.jdbc.sqlquery.builder.xml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import architecture.ee.jdbc.sqlquery.builder.AbstractBuilder;
import architecture.ee.jdbc.sqlquery.builder.BuilderException;
import architecture.ee.jdbc.sqlquery.builder.SqlQueryBuilderAssistant;
import architecture.ee.jdbc.sqlquery.factory.Configuration;
import architecture.ee.jdbc.sqlquery.parser.XNode;
import architecture.ee.jdbc.sqlquery.parser.XPathParser;
import architecture.ee.util.StringUtils;


public class XmlSqlSetBuilder extends AbstractBuilder {

	private Logger logger = LoggerFactory.getLogger(XmlSqlSetBuilder.class);
	
	private XPathParser parser;
	private String environment;
	private String resource;
	private SqlQueryBuilderAssistant builderAssistant;
	
	public XmlSqlSetBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
		
		this(new XPathParser(inputStream, false, new Properties(), null), configuration, resource, sqlFragments);
	}

	public XmlSqlSetBuilder(InputStream inputStream, Configuration configuration, String resource, Map<String, XNode> sqlFragments, String namespace) {
		this(inputStream, configuration, resource, sqlFragments);
		this.builderAssistant.setCurrentNamespace(namespace);
	}
	

	private XmlSqlSetBuilder(XPathParser parser, Configuration configuration, String resource, Map<String, XNode> sqlFragments) {
		super(configuration);
		this.builderAssistant = new SqlQueryBuilderAssistant(configuration, resource);
		this.parser = parser;
		// this.sqlFragments = sqlFragments;
		this.resource = resource;
	}

	private XmlSqlSetBuilder(XPathParser parser, String environment, Properties props) {
		super(new Configuration());
		// ErrorContext.instance().resource("SQL Mapper Configuration");
		// this.configuration.setVariables(props);
		// this.parsed = false;
		this.environment = environment;
		this.parser = parser;
	}
	
	public void parse() {
		try {
			if (!configuration.isResourceLoaded(resource)) {				
				configuration.addLoadedResource(resource);
			}			
			XNode context = parser.evalNode("/sqlset");
			configurationElement(context);				
			buildStatement(context.evalNodes("/sqlset/sql-query"));
			buildRowMapper(context.evalNodes("/sqlset/row-mapper"));				
			
		} catch (Exception e) {

		}
	}
	
	private void buildRowMapper(List<XNode> list){
		for (XNode context : list) {
			final XmlRowMapperBuilder mapperParser = new XmlRowMapperBuilder(configuration, builderAssistant, context);
			try{
				mapperParser.parseRowMapperNode();				
			}catch(Exception e){
				// 오류 발생시에 대한 처리 로직 요구됨.
			}
		}		
	}
	
	private void buildStatement(List<XNode> list){
		for (XNode context : list) {
			final XmlStatementBuilder statementParser = new XmlStatementBuilder(configuration, builderAssistant, context);
			try{
				statementParser.parseStatementNode();				
			}catch(Exception e){
				// 오류 발생시에 대한 처리 로직 요구됨.
			}
		}
	}
	
	private void configurationElement(XNode context){		
		String name = context.getStringAttribute("name", null);
		String namespace = context.getStringAttribute("namespace", null);
		String desctiption = context.getStringAttribute("description", null);
		String version = context.getStringAttribute("version", null);		
		if(StringUtils.isEmpty(name))
		{
			name = context.evalString("name");
		}		
		if(StringUtils.isEmpty(namespace))
		{
			namespace = context.evalString("namespace");
			if( StringUtils.isEmpty(namespace)  ){
				namespace = name;
			}
		}		
		if(StringUtils.isEmpty(desctiption))
		{
			desctiption = context.evalString("desctiption");
		}
		
		if(StringUtils.isEmpty(version))
		{
			version = context.evalString("version");
		}
		
		if (namespace == null || namespace.equals("")) {
	        throw new BuilderException("Mapper's namespace cannot be empty");
	    }		
		builderAssistant.setCurrentNamespace(namespace);		
		logger.debug("BUILD SQL Name={}, Namespace={}, Version={}, Description={}", name, namespace, desctiption, version);
	}
	

	
	private void rowMapperElement(List<XNode> list) throws Exception {
		String currentNamespace = builderAssistant.getCurrentNamespace();
		//configuration.addRowMapperNodes(currentNamespace, list);		
	}

	private void sqlQueryElement(List<XNode> list) throws Exception {
		String currentNamespace = builderAssistant.getCurrentNamespace();
		//configuration.addStatementNodes(currentNamespace, list);
		for (XNode context : list) {
			final XmlStatementBuilder statementParser = new XmlStatementBuilder(configuration, builderAssistant, context);
			try{
				statementParser.parseStatementNode();				
			}catch(Exception e){
				// 오류 발생시에 대한 처리 로직 요구됨.
				
				
			}
		}
	}

}
