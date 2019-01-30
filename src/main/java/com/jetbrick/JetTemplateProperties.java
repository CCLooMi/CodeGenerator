package com.jetbrick;

import java.util.Properties;

import org.springframework.boot.autoconfigure.template.AbstractViewResolverProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.Ordered;

/**© 2015-2018 Chenxj Copyright
 * 类    名：JetTemplateProperties
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年12月26日-下午9:58:46
 */

@ConfigurationProperties(prefix = "spring.jetbrick.template")
public class JetTemplateProperties extends AbstractViewResolverProperties {

    private Properties config;
    private String configLocation = null;
    private String prefix = "templates/";
    private String suffix = ".html";
    private int order = Ordered.LOWEST_PRECEDENCE - 20;

    public Properties getConfig() {
        return config;
    }

    public void setConfig(Properties config) {
        this.config = config;
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}

