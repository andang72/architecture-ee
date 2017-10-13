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
package architecture.ee.spring.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import architecture.ee.exception.ComponentNotFoundException;
import architecture.ee.i18n.CommonLogLocalizer;
import architecture.ee.service.ConfigService;
import architecture.ee.util.StringUtils;

public final class ApplicationHelper implements ApplicationContextAware {

	private static final Logger logger = LoggerFactory.getLogger(ApplicationHelper.class);

	private static ApplicationContext applicationContext = null;

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public static boolean isReady(){
		if( applicationContext == null )
			return false;
		return true;
	}
	
	public static ConfigService getConfigService(){
		return getComponent(ConfigService.class);
	}
	
	public static <T> T getComponent(Class<T> requiredType) {

		if (applicationContext == null) {
			throw new IllegalStateException(CommonLogLocalizer.getMessage("002005"));
		}
		
		if (requiredType == null) {
			throw new IllegalArgumentException(CommonLogLocalizer.getMessage("002001"));
		}

		try {
			return applicationContext.getBean(requiredType);
		} catch (NoSuchBeanDefinitionException e) {
			throw new ComponentNotFoundException(CommonLogLocalizer.format("002002", requiredType.getName()), e);
		}
	}

	public <T> T getComponent(String requiredName, Class<T> requiredType) {
		
		if (applicationContext == null) {
			throw new IllegalStateException(CommonLogLocalizer.getMessage("002005"));
		}		
		
		if (requiredType == null) {
			throw new IllegalArgumentException(CommonLogLocalizer.getMessage("002001"));
		}
		
		try {
			if( !StringUtils.isNullOrEmpty(requiredName) ){
				return applicationContext.getBean(requiredName, requiredType);
			}else {
				return applicationContext.getBean(requiredType);
			}
		} catch (NoSuchBeanDefinitionException e) {
			throw new ComponentNotFoundException(CommonLogLocalizer.format("002004", requiredType.getName(), requiredType.getName() ), e);
		}

	}

}