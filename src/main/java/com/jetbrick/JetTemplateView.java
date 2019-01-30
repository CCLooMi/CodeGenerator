package com.jetbrick;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.AbstractTemplateView;

import jetbrick.template.JetTemplate;

/**© 2015-2018 Chenxj Copyright
 * 类    名：JetTemplateView
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年12月26日-下午9:27:06
 */
public final class JetTemplateView extends AbstractTemplateView {

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JetTemplate template = JetWebEngine.getEngine().getTemplate(getUrl());
        if(getUrl().endsWith("js"+JetWebEngine.getEngine().getConfig().getTemplateSuffix())) {
        	response.setContentType("text/javascript");
        }
        template.render(model, response.getOutputStream());
    }

    @Override
    public boolean checkResource(Locale locale) throws Exception {
        return JetWebEngine.getEngine().checkTemplate(getUrl());
    }
}
