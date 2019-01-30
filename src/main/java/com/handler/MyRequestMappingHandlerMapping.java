package com.handler;

import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.util.SpringUtil;

/**© 2015-2018 Chenxj Copyright
 * 类    名：MyRequestMappingHandlerMapping
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年12月22日-下午5:12:27
 */
public class MyRequestMappingHandlerMapping extends RequestMappingHandlerMapping {
	@Override
	public void afterPropertiesSet() {
		SpringUtil.setRequestMappingHandlerMapping(this);
	}
	public void detectMyHandlerMethods(Object handler) {
		this.detectHandlerMethods(handler);
	}
}
