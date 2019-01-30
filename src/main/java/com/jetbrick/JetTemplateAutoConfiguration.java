package com.jetbrick;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import jetbrick.template.JetConfig;
import jetbrick.template.JetEngine;
import jetbrick.template.loader.ClasspathResourceLoader;

/**© 2015-2018 Chenxj Copyright
 * 类    名：JetTemplateAutoConfiguration
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年12月26日-下午9:26:00
 */
@ConditionalOnWebApplication
@ConditionalOnClass(JetEngine.class)
@ConditionalOnProperty(value = "spring.jetbrick.template.enabled", havingValue = "true")
@EnableConfigurationProperties(JetTemplateProperties.class)
public class JetTemplateAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(JetTemplateViewResolver.class)
    public JetTemplateViewResolver jetTemplateViewResolver(JetTemplateProperties properties) {
        Properties config = properties.getConfig();
        if (config == null) {
            config = new Properties();
        }
        if (!config.containsKey(JetConfig.TEMPLATE_LOADERS)) {
            config.put(JetConfig.TEMPLATE_LOADERS, ClasspathResourceLoader.class.getName());
        }

        JetTemplateViewResolver resolver = new JetTemplateViewResolver();
        resolver.setPrefix(properties.getPrefix());
        resolver.setSuffix(properties.getSuffix());
        resolver.setCache(properties.isCache());
        resolver.setViewNames(properties.getViewNames());
        resolver.setContentType(properties.getContentType().toString());
        resolver.setOrder(properties.getOrder());
        resolver.setConfigProperties(config);
        resolver.setConfigLocation(properties.getConfigLocation());
        return resolver;
    }

    @Bean
    @ConditionalOnMissingBean(EnvHolder.class)
    public EnvHolder envHolder() {
        return EnvHolder.INSTANCE;
    }

    @Bean
    public BeanPostProcessor autoRegiesterBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                return bean;
            }

            @Override
            public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
                if (bean != null && bean.getClass() == JetTemplateViewResolver.class) {
                    JetEngine engine = ((JetTemplateViewResolver) bean).getJetEngine();

                    // 注册Tags
                    engine.getGlobalResolver().registerTags(SpringedTags.class);
                }
                return bean;
            }
        };
    }
}
