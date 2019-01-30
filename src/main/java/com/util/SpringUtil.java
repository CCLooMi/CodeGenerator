package com.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;

import com.handler.MyRequestMappingHandlerMapping;

/**© 2015-2018 Chenxj Copyright
 * 类    名：SpringUtil
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年12月22日-下午10:24:01
 */
public class SpringUtil {
	private static Logger log=LoggerFactory.getLogger(SpringUtil.class);
	private static BeanDefinitionRegistry registry;
	private static MyRequestMappingHandlerMapping handlerMapping;
	
	public static boolean isInit() {
		return handlerMapping!=null;
	}
	
	public static void setRequestMappingHandlerMapping(MyRequestMappingHandlerMapping rmq) {
		ApplicationContext ctx=rmq.getApplicationContext();
		AutowireCapableBeanFactory autoF=ctx.getAutowireCapableBeanFactory();
		registry=(BeanDefinitionRegistry)autoF;
		handlerMapping=rmq;
	}
	
	public static void registBean(Class<?>...classes) {
		for(Class<?>c:classes) {
			if(!registry.containsBeanDefinition(c.getName())&&!c.isInterface()) {
				log.info("regist bean:\t{}",c);
				registry.registerBeanDefinition(
						c.getName(),
						BeanDefinitionBuilder
						.genericBeanDefinition(c)
						.getBeanDefinition());
			}
		}
	}
	public static void detectHandlerMethods(Class<?> c) {
		registBean(c);
		log.info("detect handler methods:\t",c);
		Object o=handlerMapping.getApplicationContext().getBean(c);
		handlerMapping.detectMyHandlerMethods(o);
	}
	
}
