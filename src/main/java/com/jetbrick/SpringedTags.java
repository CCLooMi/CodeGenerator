package com.jetbrick;

import java.io.IOException;

import jetbrick.template.JetAnnotations;
import jetbrick.template.runtime.JetTagContext;

/**© 2015-2018 Chenxj Copyright
 * 类    名：SpringedTags
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年12月26日-下午9:57:26
 */
@JetAnnotations.Tags
public class SpringedTags {

    @JetAnnotations.Name("actived_profile")
    public static void activedProfile(JetTagContext ctx, String profile) throws IOException {
        if (EnvHolder.getEnvironment().acceptsProfiles(profile)) {
            String content = ctx.getBodyContent();
            ctx.getWriter().print(content);
        }
    }

    @JetAnnotations.Name("actived_profile")
    public static void activedProfile(JetTagContext ctx, String... profiles) throws IOException {
        boolean ok = true;

        // Environment#acceptsProfiles(String...) 是或关系
        // 本方法是与关系
        for (String profile : profiles) {
            if (!EnvHolder.getEnvironment().acceptsProfiles(profile)) {
                ok = false;
                break;
            }
        }

        if (ok) {
            String content = ctx.getBodyContent();
            ctx.getWriter().print(content);
        }
    }
}
