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
package architecture.ee.jdbc.sqlquery.builder.xml;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import architecture.ee.jdbc.sqlquery.builder.AbstractBuilder;
import architecture.ee.jdbc.sqlquery.builder.SqlQueryBuilderAssistant;
import architecture.ee.jdbc.sqlquery.builder.xml.dynamic.DynamicSqlNode;
import architecture.ee.jdbc.sqlquery.builder.xml.dynamic.DynamicSqlSource;
import architecture.ee.jdbc.sqlquery.builder.xml.dynamic.MixedSqlNode;
import architecture.ee.jdbc.sqlquery.builder.xml.dynamic.SqlNode;
import architecture.ee.jdbc.sqlquery.builder.xml.dynamic.TextSqlNode;
import architecture.ee.jdbc.sqlquery.factory.Configuration;
import architecture.ee.jdbc.sqlquery.mapping.ParameterMapping;
import architecture.ee.jdbc.sqlquery.mapping.ResultMapping;
import architecture.ee.jdbc.sqlquery.mapping.SqlSource;
import architecture.ee.jdbc.sqlquery.mapping.StatementType;
import architecture.ee.jdbc.sqlquery.parser.XNode;
import architecture.ee.util.StringUtils;


public class XmlStatementBuilder extends AbstractBuilder {

	public static final String XML_NODE_DESCRIPTION_TAG = "description";
	public static final String XML_NODE_PARAMETER_MAPPING_TAG = "parameterMapping";
	public static final String XML_NODE_PARAMETER_MAPPINGS_TAG = "parameterMappings";

	public static final String XML_ATTR_STATEMENT_TYPE_TAG = "statementType";
	public static final String XML_ATTR_FETCH_SIZE_TAG = "fetchSize";
	public static final String XML_ATTR_TIMEOUT_TAG = "timeout";
	public static final String XML_ATTR_DYNAMIC_TAG = "dynamic";
	public static final String XML_ATTR_ID_TAG = "id";
	public static final String XML_ATTR_NAME_TAG = "name";
	public static final String XML_ATTR_CALLABLE_TAG = "callable";
	public static final String XML_ATTR_FUNCTION_TAG = "function";
	public static final String XML_ATTR_SCRIPT_TAG = "script";
	public static final String XML_ATTR_COMMENT_TAG = "comment";
	public static final String XML_ATTR_DESCRIPTION_TAG = "description";

	private Logger log = LoggerFactory.getLogger(XmlStatementBuilder.class);

	private SqlQueryBuilderAssistant builderAssistant;

	private XNode context;

	public XmlStatementBuilder(Configuration configuration, SqlQueryBuilderAssistant builderAssistant, XNode context) {

		super(configuration);
		this.builderAssistant = builderAssistant;
		this.context = context;

	}

	private List<SqlNode> parseDynamicTags(XNode node) {
		List<SqlNode> contents = new ArrayList<SqlNode>();		
		NodeList children = node.getNode().getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			XNode child = node.newXNode(children.item(i));
			String nodeName = child.getNode().getNodeName();
			if (child.getNode().getNodeType() == Node.CDATA_SECTION_NODE || child.getNode().getNodeType() == Node.TEXT_NODE) {
				String data = child.getStringBody("");
				contents.add(new TextSqlNode(data));
			} else {
				if (XML_ATTR_DYNAMIC_TAG.equals(nodeName)) {
					String data = child.getStringBody("");
					contents.add(new DynamicSqlNode(data));
				}
			}
		}
		return contents;
	}

	private List<ParameterMapping> parseParameterMappings(XNode node) {
		List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
		List<XNode> children = node.evalNodes("parameter-mappings/parameter");
		for (XNode child : children) {
			ParameterMapping.Builder builder = new ParameterMapping.Builder(child.getStringAttribute(XML_ATTR_NAME_TAG));
			builder.index(child.getIntAttribute("index", 0));
			builder.mode(child.getStringAttribute("mode", "NONE"));
			builder.primary(child.getBooleanAttribute("primary", false));
			builder.encoding(child.getStringAttribute("encoding", null));
			builder.pattern(child.getStringAttribute("pattern", null));
			builder.cipher(child.getStringAttribute("cipher", null));
			builder.cipherKey(child.getStringAttribute("cipherKey", null));
			builder.cipherKeyAlg(child.getStringAttribute("cipherKeyAlg", null));
			builder.digest(child.getStringAttribute("digest", null));
			builder.size(child.getStringAttribute("size", "0"));
			String javaTypeName = child.getStringAttribute("javaType", null);
			String jdbcTypeName = child.getStringAttribute("jdbcType", null);
			
			if (!StringUtils.isEmpty(jdbcTypeName))
				builder.jdbcTypeName(jdbcTypeName);
			
			if (!StringUtils.isEmpty(javaTypeName ))
				builder.javaType(getTypeAliasRegistry().resolveAlias(javaTypeName));
			parameterMappings.add(builder.build());
		}

		// node.getNode().getChildNodes();
		/*
		 * for (int i = 0; i < children.getLength(); i++) { XNode child =
		 * node.newXNode(children.item(i)); if( Node.ELEMENT_NODE ==
		 * child.getNode().getNodeType() &&
		 * XML_NODE_PARAMETER_MAPPING_TAG.equals(child.getNode().getNodeName()))
		 * {
		 * 
		 * ParameterMapping.Builder builder = new
		 * ParameterMapping.Builder(child.getStringAttribute(XML_ATTR_NAME_TAG))
		 * ; builder.jdbcTypeName(child.getStringAttribute("jdbcType", null));
		 * String javaTypeString = child.getStringAttribute("javaType", null);
		 * builder.index(child.getIntAttribute("index", 0));
		 * builder.primary(child.getBooleanAttribute("primary", false));
		 * if(StringUtils.isNotEmpty(javaTypeString)){
		 * builder.jdbcTypeName(javaTypeString);
		 * builder.javaType(getTypeAliasRegistry().resolveAlias(javaTypeString))
		 * ; } builder.encoding(child.getStringAttribute("encoding", null));
		 * builder.pattern( child.getStringAttribute("pattern", null) );
		 * builder.cipher(child.getStringAttribute("cipher", null));
		 * builder.cipherKey(child.getStringAttribute("cipherKey", null));
		 * builder.cipherKeyAlg(child.getStringAttribute("cipherKeyAlg", null));
		 * parameterMappings.add(builder.build());
		 * 
		 * } }
		 */
		return parameterMappings;
	}

	private List<ResultMapping> parseResultMappings(XNode node) {
		List<ResultMapping> parameterMappings = new ArrayList<ResultMapping>();
		List<XNode> children = node.evalNodes("result-mappings/result");
		for (XNode child : children) {
			ResultMapping.Builder builder = new ResultMapping.Builder(child.getStringAttribute(XML_ATTR_NAME_TAG));
			builder.index(child.getIntAttribute("index", 0));
			builder.primary(child.getBooleanAttribute("primary", false));
			builder.encoding(child.getStringAttribute("encoding", null));
			builder.pattern(child.getStringAttribute("pattern", null));
			builder.cipher(child.getStringAttribute("cipher", null));
			builder.cipherKey(child.getStringAttribute("cipherKey", null));
			builder.cipherKeyAlg(child.getStringAttribute("cipherKeyAlg", null));
			builder.size(child.getStringAttribute("size", "0"));
			
			String jdbcTypeName = child.getStringAttribute("jdbcType", null);
			if (jdbcTypeName != null)
				builder.jdbcTypeName(jdbcTypeName);
			
			String javaTypeName = child.getStringAttribute("javaType", null);
			log.debug( javaTypeName );
			if (javaTypeName != null)
				builder.javaType(getTypeAliasRegistry().resolveAlias(javaTypeName));
			parameterMappings.add(builder.build());
		}
		return parameterMappings;
	}
	
	public void parseStatementNode() {
		String idToUse = context.getStringAttribute(XML_ATTR_ID_TAG);
		String nameToUse = context.getStringAttribute(XML_ATTR_NAME_TAG);
		if (StringUtils.isEmpty(idToUse))
			idToUse = nameToUse;
		String descriptionToUse = context.getStringAttribute(XML_ATTR_DESCRIPTION_TAG);
		Integer fetchSize = context.getIntAttribute(XML_ATTR_FETCH_SIZE_TAG, 0);
		Integer timeout = context.getIntAttribute(XML_ATTR_TIMEOUT_TAG, 0);

		// bug!! 디폴트로 PREPARED !!!
		StatementType statementType = StatementType.valueOf(context.getStringAttribute(XML_ATTR_STATEMENT_TYPE_TAG, StatementType.PREPARED.toString()));
		List<ParameterMapping> parameterMappings = parseParameterMappings(context);
		List<ResultMapping> resultrMappings = parseResultMappings(context);
		
		// dynamic 해당하는 부분을 검색한다.
		List<SqlNode> contents = parseDynamicTags(context);
		MixedSqlNode rootSqlNode = new MixedSqlNode(contents);
		SqlSource sqlSource = new DynamicSqlSource(configuration, rootSqlNode, parameterMappings, resultrMappings);
		builderAssistant.addMappedStatement(idToUse, descriptionToUse, sqlSource, statementType, fetchSize, timeout);
	}
}
