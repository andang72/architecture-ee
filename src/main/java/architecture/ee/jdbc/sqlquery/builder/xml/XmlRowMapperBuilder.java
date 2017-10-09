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
import org.springframework.util.ClassUtils;

import architecture.ee.jdbc.sqlquery.builder.AbstractBuilder;
import architecture.ee.jdbc.sqlquery.builder.SqlQueryBuilderAssistant;
import architecture.ee.jdbc.sqlquery.factory.Configuration;
import architecture.ee.jdbc.sqlquery.mapping.ParameterMapping;
import architecture.ee.jdbc.sqlquery.parser.XNode;
import architecture.ee.util.StringUtils;

public class XmlRowMapperBuilder extends AbstractBuilder {

	public static final String XML_ATTR_CLASS_TAG = "class";
	public static final String XML_ATTR_IMPL_CLASS_TAG = "implement-class";
	public static final String XML_ATTR_ID_TAG = "id";
	public static final String XML_ATTR_NAME_TAG = "name";

	private Logger log = LoggerFactory.getLogger(XmlRowMapperBuilder.class);

	private SqlQueryBuilderAssistant builderAssistant;

	private XNode context;

	public XmlRowMapperBuilder(Configuration configuration, SqlQueryBuilderAssistant builderAssistant, XNode context) {
		super(configuration);
		this.builderAssistant = builderAssistant;
		this.context = context;
	}

	public void parseRowMapperNode() {

		String idToUse = context.getStringAttribute(XML_ATTR_ID_TAG);
		String nameToUse = context.getStringAttribute(XML_ATTR_NAME_TAG);
		if (StringUtils.isEmpty(idToUse))
			idToUse = nameToUse;

		String classNameToUse = context.getStringAttribute(XML_ATTR_CLASS_TAG);

		ClassLoader contextCL = Thread.currentThread().getContextClassLoader();
		ClassLoader loader = contextCL == null ? ClassUtils.class.getClassLoader() : contextCL;
		Class<?> mappedClass = ClassUtils.resolveClassName(classNameToUse, loader);
		List<ParameterMapping> parameterMappings = parseParameterMappings(context);

	}

	private List<ParameterMapping> parseParameterMappings(XNode node) {

		List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>();
		List<XNode> children = node.evalNodes("parameterMapping");
		
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

			String columnName = child.getStringAttribute("column", null);
			if (!StringUtils.isNullOrEmpty(columnName)) {
				builder.column(columnName);
			}
			String jdbcTypeName = child.getStringAttribute("jdbcType", null);
			if (!StringUtils.isNullOrEmpty(jdbcTypeName))
				builder.jdbcTypeName(jdbcTypeName);
			
			String javaTypeName = child.getStringAttribute("javaType", null);
			if (!StringUtils.isNullOrEmpty(javaTypeName))
				builder.javaType(getTypeAliasRegistry().resolveAlias(javaTypeName));

			parameterMappings.add(builder.build());
		}
		
		return parameterMappings;
	}

}
