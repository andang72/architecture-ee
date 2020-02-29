package architecture.ee.jdbc.sqlquery.factory.impl;

import java.util.HashMap;
import java.util.Map;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

public class StaticModels {

	private static Map<String, String> staticModels = new HashMap<String, String>();

	protected static Map<String, String> getStaticModels() {
		return staticModels;
	}  
	
	public static void populateStatics(BeansWrapper wrapper , Map<String, Object> model) {  
		TemplateHashModel staticHashModels = wrapper.getStaticModels();
		try {
			for ( Map.Entry<String, String> entry : staticModels.entrySet()) {
				String key = entry.getKey();
				String value = entry.getValue();
				model.put(key,	staticHashModels.get(value));
			}		
		} catch (TemplateModelException e) {
		} 
	} 
}
