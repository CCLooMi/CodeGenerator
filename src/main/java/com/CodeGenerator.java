package com;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccloomi.core.common.controller.BaseController;
import com.ccloomi.core.component.ecj.CCCompiler;
import com.ccloomi.core.util.Paths;
import com.ccloomi.core.util.StringUtil;
import com.jetbrick.Functions;
import com.sun.jna.platform.FileUtils;
import com.util.SpringUtil;

import jetbrick.template.JetConfig;
import jetbrick.template.JetEngine;
import jetbrick.template.JetTemplate;

/**© 2015-2018 Chenxj Copyright
 * 类    名：CodeGenerator
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年12月15日-下午3:05:31
 */
public class CodeGenerator {
	public static final String MODULE;
	public static final String COPYRIGHT;
	public static final String AUTHOR;
	public static final String EMAIL;
	static final Logger log=LoggerFactory.getLogger(CodeGenerator.class);
	static final Map<String, JetTemplate>tps=new HashMap<>();
	static final JetEngine engine;
	static final Charset UTF_8=Charset.forName("UTF-8");
	static final Properties settings=new Properties();
	static final Set<String>tmSet;
	static final Map<String, String>tmMap;
	static {
		Properties config=new Properties();
		config.put(JetConfig.IMPORT_FUNCTIONS,Functions.class.getName());
		for(Entry<Object, Object>e:settings.entrySet()) {
			if(((String)e.getKey()).startsWith("jetx.")) {
				if(config.containsKey(e.getKey())) {
					config.put(e.getKey(), config.get(e.getKey())+","+e.getValue());
				}else {
					config.put(e.getKey(), e.getValue());
				}
			}
		}
		engine=JetEngine.create(config);
		
		Path p=Paths.getUserDirFile("ST").toPath();
		try {
			Files.walkFileTree(p, new FileVisitor<Path>() {
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					if(!attrs.isDirectory()) {
						String fname=file.getFileName().toString();
						if(fname.endsWith(".properties")) {
							settings.load(new FileInputStream(file.toFile()));
						}else if(fname.charAt(0)!='.'){
							tps.put(fname,engine.createTemplate(new String(Files.readAllBytes(file),UTF_8)));
						}
					}
					return FileVisitResult.CONTINUE;
				}
				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		tmSet=toSet(settings.getProperty("table.mid",""),",");
		tmMap=toMap(settings.getProperty("model.alias", ""), ",");
		MODULE=settings.getProperty("module", "unknown");
		COPYRIGHT=settings.getProperty("copyright", "chenxj");
		AUTHOR=settings.getProperty("author","chenxj");
		EMAIL=settings.getProperty("email", "chenios@foxmail.com");
	}
	/**
	 * 描述：渲染文件名
	 * 作者：chenxj
	 * 日期：2019年1月29日 - 下午7:48:38
	 * @param ctx
	 * @param fn
	 * @return
	 */
	protected static String renderFileName(Map<String, Object>ctx,String fn) {
		StringBuilder fname=new StringBuilder(fn);
		for(int i=0;i<fname.length();i++) {
			if(fname.charAt(i)=='_') {
				fname.setCharAt(i, '/');
			}
		}
		return renderString(engine.createTemplate(fname.toString()), ctx);
	}
	/**
	 * 描述：渲染模版为String
	 * 作者：chenxj
	 * 日期：2019年1月29日 - 下午7:51:51
	 * @param jt
	 * @param ctx
	 * @return
	 */
	protected static String renderString(JetTemplate jt,Map<String, Object>ctx) {
		ByteArrayOutputStream bout=new ByteArrayOutputStream();
		jt.render(ctx, bout);
		try {bout.flush();} 
		catch (IOException e) {}
		return bout.toString();
	}
	/**
	 * 描述：获取文件的包名
	 * 作者：chenxj
	 * 日期：2019年1月29日 - 下午7:52:32
	 * @param file
	 * @return
	 */
	protected static String getPkg(Path file) {
		String s=file.toString();
		//15为[/src/main/java/]的长度
		int start=System.getProperty("user.dir").length()+15;
		int fnl=file.getFileName().toString().length();
		return s.substring(start, s.length()-fnl-1).replaceAll("/|\\\\+", ".");
	}
	/**
	 * 描述：根据文件获取className
	 * 作者：chenxj
	 * 日期：2019年1月29日 - 下午8:21:14
	 * @param file
	 * @return
	 */
	protected static String getClassName(Path file) {
		String fn=file.toFile().getName();
		return new String(fn.substring(0, fn.length()-5));
	}
	/**
	 * 描述：将模版渲染到文件
	 * 作者：chenxj
	 * 日期：2019年1月29日 - 下午8:21:55
	 * @param jt
	 * @param ctx
	 * @param file
	 */
	protected static void renderToFile(JetTemplate jt,Map<String, Object>ctx,Path file) {
		try {
			//路径不存在则创建
			if(!file.getParent().toFile().exists()) {
				Files.createDirectories(file.getParent());
			}
			FileOutputStream fout=new FileOutputStream(file.toFile());
			jt.render(ctx, fout);
			fout.flush();
			fout.close();
			log.info("Render to file success:\t{}",file);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static Set<String>toSet(String s,String split){
		Set<String>set=new HashSet<>();
		if(s!=null&&s.length()>0) {
			String[] sa=s.split(split);
			for(int i=0;i<sa.length;i++) {
				set.add(sa[i]);
			}
		}
		return set;
	}
	private static Map<String, String>toMap(String s,String split){
		Map<String, String>map=new HashMap<>();
		if(s!=null&&s.length()>0) {
			String[] sa=s.split(split);
			for(int i=0;i<sa.length;i++) {
				String[]ss=sa[i].split(":",2);
				if(ss.length>1) {
					map.put(ss[0], ss[1]);
				}
			}
		}
		return map;
	}
	/**
	 * 描述：编译包含java源码的map and 注册controller中方法
	 * 作者：chenxj
	 * 日期：2019年1月29日 - 下午9:05:53
	 * @param sourceMap
	 * @return
	 */
	protected List<Class<?>> compileSourceMap(Map<String, String>sourceMap){
		List<Class<?>>cls=new ArrayList<>(1);
		CCCompiler.compile(sourceMap, (name,c)->{
			SpringUtil.registBean((Class<?>)c);
			if(BaseController.class.isAssignableFrom((Class<?>)c)) {
				cls.add((Class<?>)c);
			}
		});
		if(!cls.isEmpty()) {
			SpringUtil.detectHandlerMethods(cls.get(0));
		}
		return cls;
	}
	protected static String removeCode(String tableName) {
		Map<String, Object>ctx=new HashMap<>();
		ctx.put("module", MODULE);
		ctx.put("copyright", COPYRIGHT);
		ctx.put("author", AUTHOR);
		ctx.put("email", EMAIL);
		String[]s=toModelName(tableName);
		ctx.put("tableName", tableName);
		ctx.put("prefix", s[0]);
		ctx.put("name", s[1]);
		if(tmMap.containsKey(s[1])) {
			ctx.put("alias", tmMap.get(s[1]));
		}else {
			ctx.put("alias", s[1]);
		}
		for(Entry<String, JetTemplate>e:tps.entrySet()) {
			String fname=renderFileName(ctx, e.getKey());
			File file=Paths.getUserDirFile(fname);
			if(file.exists()) {
				moveToTrash(file);
//				file.delete();
				log.info("删除文件:\t{}",fname);
				if(file.getParentFile().list().length>0) {
					continue;
				}
//				file.getParentFile().delete();
				moveToTrash(file.getParentFile());
				log.info("删除目录:\t{}",file.getParentFile());
			}
		}
		return (String) ctx.get("alias");
	}
	protected static String[] toModelName(String tableName) {
		String[]s=tableName.split("_");
		if(s.length>1) {
			StringBuilder sb=new StringBuilder();
			sb.append(s[1]);
			for(int i=2;i<s.length;i++) {
				sb.append(StringUtil.upperCaseFirstLatter(s[i]));
			}
			if(s[0].length()>1) {
				return new String[] {new String(s[0]),sb.toString()};
			}else {
				return new String[] {"",sb.toString()};
			}
		}else {
			return new String[] {"",new String(s[0])};
		}
	}
	private static void moveToTrash(File...files) {
		try {
			FileUtils.getInstance().moveToTrash(files);
		} catch (IOException e) {
			log.error("删除文件异常",e);
		}
	}
}
