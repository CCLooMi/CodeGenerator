package com.jetbrick;

import java.util.Locale;
import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

import jetbrick.template.JetEngine;

/**© 2015-2018 Chenxj Copyright
 * 类    名：JetTemplateViewResolver
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年12月26日-下午9:24:49
 */
public final class JetTemplateViewResolver extends AbstractTemplateViewResolver implements InitializingBean {
    private String configLocation;
    private Properties configProperties;
    private JetEngine jetEngine;

    public JetTemplateViewResolver() {
        setViewClass(requiredViewClass());
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    public void setConfigProperties(Properties configProperties) {
        this.configProperties = configProperties;
    }

    public JetEngine getJetEngine() {
        return jetEngine;
    }

    @Override
    protected Class<?> requiredViewClass() {
        return JetTemplateView.class;
    }

    @Override
    protected View loadView(String viewName, Locale locale) throws Exception {
        View view = super.loadView(viewName, locale);
        if (view instanceof JetTemplateView) {
            ((JetTemplateView) view).setAllowRequestOverride(false);
            ((JetTemplateView) view).setAllowSessionOverride(false);
        }
        return view;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        JetEngine engine = JetWebEngine.create(getServletContext(), configProperties, configLocation);
        if (getSuffix() == null || getSuffix().length() == 0) {
            setSuffix(engine.getConfig().getTemplateSuffix());
        }
        this.jetEngine = engine;
    }
}
