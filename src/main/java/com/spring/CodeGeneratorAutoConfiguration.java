package com.spring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.handler.MyRequestMappingHandlerMapping;
/**
 * @date 2019年1月30日 上午9:31:39
 * @author chenxianjun
 * @since version
 */
@ConditionalOnProperty(value="code.generator.enabled",havingValue="true")
public class CodeGeneratorAutoConfiguration {
	@Bean
	public RequestMappingHandlerMapping myRequestMappingHandlerMapping() {
		RequestMappingHandlerMapping rm= new MyRequestMappingHandlerMapping();
		rm.setOrder(1);
		return rm;
	}
}
