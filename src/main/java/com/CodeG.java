package com;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.ccloomi.core.common.controller.BaseController;
import com.ccloomi.core.component.ecj.CCCompiler;
import com.ccloomi.core.util.Paths;
import com.exceptions.NotAutoConfigureException;
import com.util.DbUtil;
import com.util.SpringUtil;

import jetbrick.template.JetTemplate;
/**
 * code generator util
 * @date 2019年1月30日 上午10:02:36
 * @author chenxianjun
 * @since version
 */
public class CodeG extends CodeGenerator{
	@SuppressWarnings("static-access")
	public static void generateAllModelCode(boolean toFile,Filter filter,Map<String, String>sourceMap) {
		Map<String, Object>ctx=new HashMap<>();
		ctx.put("module", MODULE);
		ctx.put("copyright", COPYRIGHT);
		ctx.put("author", AUTHOR);
		ctx.put("email", EMAIL);
		//COLUMN_NAME,DATA_TYPE,CHARACTER_MAXIMUM_LENGTH,CHARACTER_SET_NAME,
		//COLUMN_COMMENT,COLUMN_TYPE,COLUMN_DEFAULT
		try {
			DbUtil
			.driver(settings.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"))
			.url(settings.getProperty("db.url", "jdbc:mysql://localhost:3306/?serverTimezone=GMT"))
			.user(settings.getProperty("db.username", "root"))
			.password(settings.getProperty("db.password", "123456"))
			.schemaTraverse(settings.getProperty("db.name","test"), (tableName,sa)->{
				boolean hasId=false;
				int i=0;
				for(String[]s:sa) {
					if("id".equalsIgnoreCase(s[0])) {
						hasId=true;
						break;
					}
					i++;
				}
				if(hasId) {
					sa.remove(i);
				}
				String[]s=toModelName(tableName);
				ctx.put("tableName", tableName);
				ctx.put("prefix", s[0]);
				ctx.put("name", s[1]);
				if(tmMap.containsKey(s[1])) {
					ctx.put("alias", tmMap.get(s[1]));
				}else {
					ctx.put("alias", s[1]);
				}
				ctx.put("comment", s[1]);
				ctx.put("columns", sa);
				ctx.put("hasId", hasId);
				generateModelCode(ctx,toFile,filter,sourceMap);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void generateAllModelCodeAndCompile(Filter filter) {
		if(!SpringUtil.isInit()) {
			throw new NotAutoConfigureException("To compile,you must import CodeGeneratorAutoConfiguration.class in your config class!\n"
					+ "Then add [code.generator.enabled=true] to your application.properties file to enable it");
		}
		Map<String, String>sourceMap=new HashMap<>();
		generateAllModelCode(true, filter, sourceMap);
		List<Class<?>>cls=new ArrayList<>();
		CCCompiler.compile(sourceMap, (name,c)->{
			SpringUtil.registBean((Class<?>)c);
			if(BaseController.class.isAssignableFrom((Class<?>)c)) {
				cls.add((Class<?>)c);
			}
		});
		if(!cls.isEmpty()) {
			SpringUtil.detectHandlerMethods(cls.get(0));
		}
	}
	@SuppressWarnings("static-access")
	public static void generateModelCode(String tableName,boolean toFile,Filter filter,Map<String, String>sourceMap) {
		try {
			List<String[]> ca = DbUtil.driver(settings.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"))
					.url(settings.getProperty("db.url", "jdbc:mysql://localhost:3306/?serverTimezone=GMT"))
					.user(settings.getProperty("db.username", "root"))
					.password(settings.getProperty("db.password", "123456"))
					.tableColumnsData(settings.getProperty("db.name", "test"), tableName);
			gernerateModelCodeWithColumas(tableName, ca, toFile, filter, sourceMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("static-access")
	public static void generateModelCodeAndCompile(String tableName,Filter filter) {
		if(!SpringUtil.isInit()) {
			throw new NotAutoConfigureException("To compile,you must import CodeGeneratorAutoConfiguration.class in your config class!\n"
					+ "Then add [code.generator.enabled=true] to your application.properties file to enable it");
		}
		Map<String, String>sourceMap=new HashMap<>();
		try {
			List<String[]> ca = DbUtil.driver(settings.getProperty("db.driver", "com.mysql.cj.jdbc.Driver"))
					.url(settings.getProperty("db.url", "jdbc:mysql://localhost:3306/?serverTimezone=GMT"))
					.user(settings.getProperty("db.username", "root"))
					.password(settings.getProperty("db.password", "123456"))
					.tableColumnsData(settings.getProperty("db.name", "test"), tableName);
			gernerateModelCodeWithColumas(tableName, ca, true, filter, sourceMap);
			//编译sourceMap
			List<Class<?>>cls=new ArrayList<>(1);
			CCCompiler.compile(sourceMap, (name,c)->{
				SpringUtil.registBean((Class<?>)c);
				if(BaseController.class.isAssignableFrom((Class<?>)c)) {
					cls.add((Class<?>)c);
				}
			});
			//必须等所有编译好的class注册之后再解析controller中handlerMethod
			if(!cls.isEmpty()) {
				SpringUtil.detectHandlerMethods(cls.get(0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void moveCode2Trash(String tableName) {
		removeCode(tableName);
	}
	/**
	 * 描述：生成一个模块代码
	 * 作者：chenxj
	 * 日期：2019年1月29日 - 下午8:55:45
	 * @param ctx
	 * @param toFile
	 * @param filter
	 * @param sourceMap 存储java源码用于编译
	 */
	private static void generateModelCode(Map<String, Object>ctx,boolean toFile,Filter filter,Map<String, String>sourceMap) {
		loop:for(Entry<String, JetTemplate>e:tps.entrySet()) {
			String fname=renderFileName(ctx, e.getKey());
			Path p=Paths.getUserDirFile(fname).toPath();
			if(!p.toFile().exists()||!toFile) {
				switch (filter.doFilter(fname)) {
				case CONTINUE:
					if(fname.endsWith(".java")) {
						if(tmSet.contains(ctx.get("tableName"))&&
								!(fname.endsWith("Entity.java")||
								fname.endsWith("Dao.java")||
								fname.endsWith("DaoImpl.java"))) {
							continue loop;
						}
						ctx.put("pkg", getPkg(p));
						if(sourceMap!=null) {
							sourceMap.put(getClassName(p), renderString(e.getValue(), ctx));
						}
						if(toFile) {
							renderToFile(e.getValue(), ctx, p);
						}else {
							e.getValue().render(ctx, System.out);
						}
					}else if(!tmSet.contains(ctx.get("tableName"))){
						if(toFile) {
							renderToFile(e.getValue(), ctx, p);
						}else {
							e.getValue().render(ctx, System.out);
						}
					}
					continue loop;
				case BREAK:
					break loop;
				default:
					break;
				}
			}
		}	
	}
	/**
	 * 描述：通过column信息列表生成代码
	 * 作者：chenxj
	 * 日期：2019年1月29日 - 下午9:10:59
	 * @param tableName
	 * @param sa
	 * @param toFile
	 * @return
	 */
	public static String gernerateModelCodeWithColumas(String tableName,List<String[]>sa,boolean toFile,Filter filter,Map<String, String>sourceMap) {
		Map<String, Object>ctx=new HashMap<>();
		ctx.put("module", MODULE);
		ctx.put("copyright", COPYRIGHT);
		ctx.put("author", AUTHOR);
		ctx.put("email", EMAIL);
		boolean hasId=false;
		int i=0;
		for(String[]s:sa) {
			if("id".equalsIgnoreCase(s[0])) {
				hasId=true;
				break;
			}
			i++;
		}
		if(hasId) {
			sa.remove(i);
		}
		String[]s=toModelName(tableName);
		ctx.put("tableName", tableName);
		ctx.put("prefix", s[0]);
		ctx.put("name", s[1]);
		if(tmMap.containsKey(s[1])) {
			ctx.put("alias", tmMap.get(s[1]));
		}else {
			ctx.put("alias", s[1]);
		}
		ctx.put("comment", s[1]);
		ctx.put("columns", sa);
		ctx.put("hasId", hasId);
		generateModelCode(ctx, toFile, filter,sourceMap);
		return (String) ctx.get("alias");
	}
	/**
	 * 描述：通过column信息列表生成代码并编译
	 * 作者：chenxj
	 * 日期：2019年1月30日 - 下午11:43:31
	 * @param tableName
	 * @param sa
	 * @return
	 */
	public String gernerateModelCodeWithColumasAndCompile(String tableName,List<String[]>sa) {
		Map<String, String>sourceMap=new HashMap<>();
		String n=gernerateModelCodeWithColumas(tableName, sa, true, fn->{return FilterResult.CONTINUE;}, sourceMap);
		//编译sourceMap
		List<Class<?>>cls=new ArrayList<>(1);
		CCCompiler.compile(sourceMap, (name,c)->{
			SpringUtil.registBean((Class<?>)c);
			if(BaseController.class.isAssignableFrom((Class<?>)c)) {
				cls.add((Class<?>)c);
			}
		});
		//必须等所有编译好的class注册之后再解析controller中handlerMethod
		if(!cls.isEmpty()) {
			SpringUtil.detectHandlerMethods(cls.get(0));
		}
		return n;
	}
	public static interface Filter{
		public FilterResult doFilter(String name);
	}
	public static enum FilterResult {
		CONTINUE,BREAK,STEPOVER
	}
	public static void main(String[] args) {
		CodeG.generateModelCode("sys_data_dictionary", false, (fn)->{
			if(fn.endsWith(".java")) {
				return FilterResult.CONTINUE;
			}else {
				return FilterResult.STEPOVER;
			}
		}, null);
//		CodeG.generateAllModelCode(false, fn->{
//			return FilterResult.CONTINUE;
//		}, null);
	}
}
