package com.jetbrick;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;


/**© 2015-2018 Chenxj Copyright
 * 类    名：EnvHolder
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年12月26日-下午9:58:10
 */

public final class EnvHolder implements EnvironmentAware {

    public static final EnvHolder INSTANCE = new EnvHolder();
    private static Environment environment;

    private EnvHolder() {
        super();
    }

    @Override
    public void setEnvironment(Environment environment) {
        EnvHolder.environment = environment;
    }

    public static Environment getEnvironment() {
        return environment;
    }

}
