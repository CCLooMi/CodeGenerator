package com.jetbrick;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.GlobalConfig;
import com.ccloomi.core.util.Paths;
import com.ccloomi.core.util.StringUtil;
import com.util.DbUtil;

/**© 2015-2018 Chenxj Copyright
 * 类    名：Functions
 * 类 描 述：
 * 作    者：chenxj
 * 邮    箱：chenios@foxmail.com
 * 日    期：2018年12月16日-下午3:45:36
 */
public class Functions {
	private static final Set<Class<?>>baseTypeSet=new HashSet<>();
	static {
		baseTypeSet.add(String.class);
		baseTypeSet.add(Integer.class);
		baseTypeSet.add(Long.class);
		baseTypeSet.add(Float.class);
		baseTypeSet.add(Boolean.class);
		baseTypeSet.add(Double.class);
		baseTypeSet.add(byte[].class);
	}
	public static String up1(String s) {
		return StringUtil.upperCaseFirstLatter(s);
	}
	public static String properties(String[]s) {
		return "private "+DbUtil.getJavaType(s).getSimpleName()+" "+s[0]+";";
	}
	/**
	 * 添加文件名或包添加前缀
	 * @date 2018年12月27日 上午11:15:06
	 * @author chenxianjun
	 * @since version
	 * @param s
	 * @return
	 */
	public static String pf(String s) {
		if(s!=null&&s.length()>0) {
			return s+".";
		}
		return null;
	}
	public static String javaType(String[]s) {
		return DbUtil.getJavaType(s).getSimpleName();
	}
	public static String IF(boolean c,String a,String b) {
		return c?a:b;
	}
	public static String imports(List<String[]>sa) {
		StringBuilder sb=new StringBuilder();
		char sep=0;
		for(String[]s:sa) {
			Class<?>c=DbUtil.getJavaType(s);
			if(!baseTypeSet.contains(c)) {
				if(sep!=0) {
					sb.append(sep);
				}else {
					sep='\n';
				}
				sb.append("import ").append(c.getName()).append(";");
			}
		}
		return sb.toString();
	}
	public static String link(String prefix,String space,List<String>sa) {
		StringBuilder sb=new StringBuilder();
		int i=0;
		for(String s:sa) {
			Path file=Paths.getUserDirFile("public",prefix,s).toPath();
			if(i!=0) {
				sb.append(space);
			}
			sb.append("<link rel=\"stylesheet\" href=\"")
			.append(prefix)
			.append(s)
			.append('?')
			.append(GlobalConfig.getFileHash(file))
			.append("\">\n");
			i++;
		}
		return sb.toString();
	}
	public static String js(String prefix,String space,List<String>sa) {
		StringBuilder sb=new StringBuilder();
		int i=0;
		for(String s:sa) {
			Path file=Paths.getUserDirFile("public",prefix,s).toPath();
			if(i!=0) {
				sb.append(space);
			}
			sb.append("<script src=\"")
			.append(prefix)
			.append(s)
			.append('?')
			.append(GlobalConfig.getFileHash(file))
			.append("\"></script>\n");
			i++;
		}
		return sb.toString();
	}
	/**
	 * 描述：路由配置
	 * 作者：chenxj
	 * 日期：2018年12月31日 - 上午8:29:31
	 * @param lss
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String sts(List<List<Object>>lss) {
		StringBuilder sb=new StringBuilder();
		sb.append('[');
		for(List<Object>ls:lss) {
			sb.append('[');
			int lsl=ls.size();
			for(int i=0;i<lsl;i++) {
				Object o=ls.get(i);
				if(o instanceof String) {
					sb.append('\'')
					.append(o);
					if(i==2) {
						Path file=Paths.getUserDirFile("public",(String)o).toPath();
						sb.append('?')
						.append(GlobalConfig.getFileHash(file));
					}
					sb.append("',");
				}else {
					sb.append('[');
					List<String>ol=(List<String>)o;
					Path file=null;
					for(String s:ol) {
						sb.append('\'')
						.append(s);
						if(s.charAt(s.length()-1)!='&') {
							sb.append('?');
							file=Paths.getUserDirFile("public",s).toPath();
						}else {
							//window 下对带参数路径会出问题
							file=Paths.getUserDirFile("public",s.replaceAll("\\?.+", "")).toPath();
						}
						sb.append(GlobalConfig.getFileHash(file))
						.append("',");
					}
					if(ol.size()>0) {
						sb.setCharAt(sb.length()-1, ']');
					}else {
						sb.append(']');
					}
					sb.append(',');
				}
			}
			if(ls.size()>0) {
				sb.setCharAt(sb.length()-1, ']');
			}else {
				sb.append(']');
			}
			sb.append(',');
		}
		if(lss.size()>0) {
			sb.setCharAt(sb.length()-1, ']');
		}else {
			sb.append(']');
		}
		return sb.toString();
	}
}
