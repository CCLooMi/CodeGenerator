package com.jetbrick;

import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jetbrick.config.ConfigLoader;
import jetbrick.template.JetConfig;
import jetbrick.template.JetEngine;

/**© 2015-2018 Chenxj Copyright
 * 类    名：JetWebEngine
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年12月26日-下午10:07:03
 */

/**
 * 负责初始化 Web 环境下的 JetEngine
 */
public final class JetWebEngine {
    private static final Logger log = LoggerFactory.getLogger(JetWebEngine.class);

    private static final String JET_ENGINE_KEY_NAME = JetEngine.class.getName();
    private static final String CONFIG_LOCATION_PARAMETER = "jetbrick-template-config-location";

    private static ServletContext servletContext;
    private static JetEngine engine;

    public static JetEngine create(ServletContext sc) {
        return create(sc, null, null);
    }

    public static JetEngine create(ServletContext sc, Properties config, String configLocation) {
        if (engine != null) {
            if (sc.getAttribute(JET_ENGINE_KEY_NAME) == engine) {
                return engine;
            }
            log.warn("webapp reloading: recreating the JetEngine ...");
        }

        servletContext = sc;
        engine = doCreateWebEngine(sc, config, configLocation);

        servletContext.setAttribute(JET_ENGINE_KEY_NAME, engine);
        return engine;
    }

    public static ServletContext getServletContext() {
        return servletContext;
    }

    public static JetEngine getEngine() {
        return engine;
    }

    private static JetEngine doCreateWebEngine(ServletContext sc, Properties config, String configLocation) {
        ConfigLoader loader = new ConfigLoader();

        // Web 环境下的默认配置
        loader.load(JetConfig.IO_SKIPERRORS, "true");
        loader.load(JetConfig.TEMPLATE_LOADERS, TemplateResourceLoader.class.getName());

        if (config != null) {
            loader.load(config);
        }

        // 用户配置文件
        if (configLocation == null) {
            configLocation = sc.getInitParameter(CONFIG_LOCATION_PARAMETER);
            if (configLocation == null || configLocation.length() == 0) {
                configLocation = JetConfig.DEFAULT_CONFIG_FILE;
            }
        }
        try {
            log.info("Loading config file: {}", configLocation);
            loader.load(configLocation, sc);
        } catch (IllegalStateException e) {
            // 默认配置文件允许不存在
            if (!JetConfig.DEFAULT_CONFIG_FILE.equals(configLocation)) {
                throw e;
            }
            log.warn("no default config file found: {}", JetConfig.DEFAULT_CONFIG_FILE);
        }
        return JetEngine.create(loader.asProperties());
    }

}
