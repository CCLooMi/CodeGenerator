package com.jetbrick;

import java.io.File;

import com.ccloomi.core.util.Paths;

import jetbrick.io.resource.FileSystemResource;
import jetbrick.io.resource.Resource;
import jetbrick.template.loader.AbstractResourceLoader;
/**
 * © 2015-2018 Chenxj Copyright
 * 类    名：TemplateResourceLoader
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年12月26日-下午10:29:51
 */
public class TemplateResourceLoader extends AbstractResourceLoader {

	public TemplateResourceLoader() {
		root=System.getProperty("user.dir");
		reloadable=false;
	}
	
	@Override
	public Resource load(String name) {
		File file=Paths.getFile(root, name);
		if(!file.exists()) {
			return null;
		}
        FileSystemResource resource = new FileSystemResource(file);
        resource.setRelativePathName(name);
        return resource;
	}

}
