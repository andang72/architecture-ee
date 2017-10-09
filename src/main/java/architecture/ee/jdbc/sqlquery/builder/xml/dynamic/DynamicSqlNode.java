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
package architecture.ee.jdbc.sqlquery.builder.xml.dynamic;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;

public class DynamicSqlNode implements SqlNode {

	public enum Language {
		VELOCITY, FREEMARKER
	}
	private static BeansWrapper wrapper = new BeansWrapper();
	protected static void populateStatics(Map<String, Object> model) {
		try {
			TemplateHashModel enumModels = wrapper.getEnumModels();
			model.put("enums", enumModels);
		} catch (UnsupportedOperationException e) {
		}
		//TemplateHashModel staticModels = wrapper.getStaticModels();
		model.put("statics", BeansWrapper.getDefaultInstance().getStaticModels());
	}
	protected Logger log = LoggerFactory.getLogger(getClass());

	private String text;

	private Language language = Language.FREEMARKER;

	public DynamicSqlNode(String text) {
		this.text = text;
	}

	/**
	 * 다이나믹 구현은 Freemarker 을 사용하여 처리한다. 따라서 나이나믹 처리를 위해서는 반듯이 freemarker 의 규칙을
	 * 사용하여야 한다.
	 */
	public boolean apply(DynamicContext context) {

		Map<String, Object> map = new HashMap<String, Object>();

		Object parameterObject = context.getBindings().get(DynamicContext.PARAMETER_OBJECT_KEY);
		Object additionalParameterObject = context.getBindings().get(DynamicContext.ADDITIONAL_PARAMETER_OBJECT_KEY);

		if (additionalParameterObject != null) {
			if (additionalParameterObject instanceof Map)
				map.putAll((Map)additionalParameterObject);
			else
				map.put("additional_parameters", additionalParameterObject);
		}

		/**
		 * 파라메터 객체가 널이 아니면 ..
		 */
		if (parameterObject != null) {
			if (parameterObject instanceof Map) {
				map.putAll((Map)parameterObject);
				//map.put("parameters", parameterObject);
			} else if (parameterObject instanceof MapSqlParameterSource) {								
				map.putAll(((MapSqlParameterSource) parameterObject).getValues());
				//map.put("parameters", ((MapSqlParameterSource) parameterObject).getValues());				
			} else {
				map.put("parameters", parameterObject);
			}
		}
		context.appendSql(processTemplate(map));
		return true;
	}

	protected String processTemplate(Map<String, Object> model) {
		StringReader reader = new StringReader(text);
		StringWriter writer = new StringWriter();
		if( language == Language.FREEMARKER ){
			try {
				populateStatics(model);
				freemarker.template.SimpleHash root = new freemarker.template.SimpleHash();
				root.putAll(model);
				freemarker.template.Template template = new freemarker.template.Template("dynamic", reader, null);
				template.process(root, writer);
			} catch (IOException e) {
				log.error("", e);
			} catch (TemplateException e) {
				log.error("", e);
			}
		}
		return writer.toString();
	}

	@Override
	public String toString() {

		return "dynamic[" + text + "]";
	}
}
