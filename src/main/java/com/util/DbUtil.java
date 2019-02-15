package com.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

/**
 * 类    名：DbUtil
 * 类 描 述：
 * 作    者：Chenxj
 * 日    期：2016年1月9日-上午11:48:38
 */
public class DbUtil {
	static final String sql_tables="SELECT TABLE_NAME FROM information_schema.`TABLES` WHERE TABLE_SCHEMA=?";
	static final String sql_columns="SELECT COLUMN_NAME,DATA_TYPE,CHARACTER_MAXIMUM_LENGTH,CHARACTER_SET_NAME,COLUMN_COMMENT,"+
	"IF(DATA_TYPE='enum',SUBSTR(COLUMN_TYPE,6,LENGTH(COLUMN_TYPE)-6),COLUMN_TYPE) AS COLUMN_TYPE"+
			",COLUMN_DEFAULT FROM information_schema.`COLUMNS` WHERE TABLE_SCHEMA=? AND TABLE_NAME=?";
	static String driver="com.mysql.cj.jdbc.Driver";
	static String url="jdbc:mysql://localhost:3306/?serverTimezone=GMT";
	static String user="root";
	static String pass="123456";
	
	public static DbUtil driver(String driver) {
		DbUtil.driver=driver;
		return null;
	}
	public static DbUtil url(String url) {
		DbUtil.url=url;
		return null;
	}
	public static DbUtil user(String user) {
		DbUtil.user=user;
		return null;
	}
	public static DbUtil password(String pass) {
		DbUtil.pass=pass;
		return null;
	}
	public static void tableTraverse(String schemaName,String tableName,TableFilter tf)throws Exception {
		Class.forName(driver);
		Connection conn=DriverManager.getConnection(url, user, pass);
		PreparedStatement stmt=conn.prepareStatement(sql_columns);
		stmt.setString(1, schemaName);
		stmt.setString(2, tableName);
		ResultSet result=stmt.executeQuery();
		ResultSetMetaData md=result.getMetaData();
		List<String[]>ls=new ArrayList<>();
		while(result.next()) {
			String[]sa=new String[md.getColumnCount()];
			for(int i=1;i<=md.getColumnCount();i++) {
				sa[i-1]=result.getString(i);
			}
			ls.add(sa);
		}
		tf.doFilter(tableName,ls);
	}
	public static void schemaTraverse(String schemaName,TableFilter tf)throws Exception {
		List<String>tables=new ArrayList<>();
		Class.forName(driver);
		Connection conn=DriverManager.getConnection(url, user, pass);
		PreparedStatement stmt=conn.prepareStatement(sql_tables);
		stmt.setString(1, schemaName);
		ResultSet result=stmt.executeQuery();
		
		ResultSetMetaData md=result.getMetaData();
		while(result.next()) {
			for(int i=1;i<=md.getColumnCount();i++) {
				tables.add(result.getString(i));
			}
		}
		stmt.close();
		
		stmt=conn.prepareStatement(sql_columns);
		stmt.setString(1, schemaName);
		
		for(String table:tables) {
			stmt.setString(2, table);
			result=stmt.executeQuery();
			md=result.getMetaData();
			List<String[]>ls=new ArrayList<>();
			while(result.next()) {
				String[]sa=new String[md.getColumnCount()];
				for(int i=1;i<=md.getColumnCount();i++) {
					sa[i-1]=result.getString(i);
				}
				ls.add(sa);
			}
			tf.doFilter(table,ls);
		}
		stmt.close();
		conn.close();
	}
	public static List<String[]> tableColumnsData(String schemaName,String tableName) throws Exception{
		List<String[]>ls=new ArrayList<>();
		Class.forName(driver);
		Connection conn=DriverManager.getConnection(url, user, pass);
		PreparedStatement stmt=conn.prepareStatement(sql_columns);
		stmt.setString(1, schemaName);
		stmt.setString(2, tableName);
		ResultSet result=stmt.executeQuery();
		ResultSetMetaData md=result.getMetaData();
		while(result.next()) {
			String[]sa=new String[md.getColumnCount()];
			for(int i=1;i<=md.getColumnCount();i++) {
				sa[i-1]=result.getString(i);
			}
			ls.add(sa);
		}
		return ls;
	}
	public static interface TableFilter{
		public void doFilter(String table,List<String[]>sa);
	}
	public static Class<?> getJavaType(String[]columnMetaData){
		if("bit".equalsIgnoreCase(columnMetaData[1])){
			if(Long.valueOf(columnMetaData[2])==1){
				return Boolean.class;
			}else{
				return byte[].class;
			}
		}else if("tinyint".equalsIgnoreCase(columnMetaData[1])){
			return Integer.class;
		}else if("bool".equalsIgnoreCase(columnMetaData[1])
				||"boollean".equalsIgnoreCase(columnMetaData[1])){
			return Boolean.class;
		}else if("smallint".equalsIgnoreCase(columnMetaData[1])){
			return Integer.class;
		}else if("mediumint".equalsIgnoreCase(columnMetaData[1])){
			return Integer.class;
		}else if("int".equalsIgnoreCase(columnMetaData[1])
				||"integer".equalsIgnoreCase(columnMetaData[1])){
			return Integer.class;
		}else if("bigint".equalsIgnoreCase(columnMetaData[1])){
			return Long.class;
		}else if("real".equalsIgnoreCase(columnMetaData[1])){
			return Float.class;
		}else if("double".equalsIgnoreCase(columnMetaData[1])){
			return Double.class;
		}else if("float".equalsIgnoreCase(columnMetaData[1])){
			return Float.class;
		}else if("decimal".equalsIgnoreCase(columnMetaData[1])){
			return BigDecimal.class;
		}else if("date".equalsIgnoreCase(columnMetaData[1])){
			return Date.class;
		}else if("datetime".equalsIgnoreCase(columnMetaData[1])){
			return Date.class;
		}else if("timestamp".equalsIgnoreCase(columnMetaData[1])){
			return Long.class;
		}else if("time".equalsIgnoreCase(columnMetaData[1])){
			return Time.class;
		}else if("year".equalsIgnoreCase(columnMetaData[1])){
			return String.class;
		}else if("char".equalsIgnoreCase(columnMetaData[1])){
			if("binary".equalsIgnoreCase(columnMetaData[3])){
				return byte[].class;
			}else{
				return String.class;
			}
		}else if("varchar".equalsIgnoreCase(columnMetaData[1])){
			if("binary".equalsIgnoreCase(columnMetaData[3])){
				return byte[].class;
			}else{
				return String.class;
			}
		}else if("binary".equalsIgnoreCase(columnMetaData[1])){
			return byte[].class;
		}else if("varbinary".equalsIgnoreCase(columnMetaData[1])){
			return byte[].class;
		}else if("tinyblob".equalsIgnoreCase(columnMetaData[1])){
			return byte[].class;
		}else if("blob".equalsIgnoreCase(columnMetaData[1])){
			return byte[].class;
		}else if("text".equalsIgnoreCase(columnMetaData[1])){
			return String.class;
		}else if("mediumblog".equalsIgnoreCase(columnMetaData[1])){
			return byte[].class;
		}else if("mediumtext".equalsIgnoreCase(columnMetaData[1])){
			return String.class;
		}else if("longblob".equalsIgnoreCase(columnMetaData[1])){
			return byte[].class;
		}else if("longtext".equalsIgnoreCase(columnMetaData[1])){
			return String.class;
		}else if("enum".equalsIgnoreCase(columnMetaData[1])){
			return String.class;
		}else if("set".equalsIgnoreCase(columnMetaData[1])){
			return String.class;
		}else if("point".equalsIgnoreCase(columnMetaData[1])){
			return String.class;
		}else if("linestring".equalsIgnoreCase(columnMetaData[1])){
			return String.class;
		}else if("polygon".equalsIgnoreCase(columnMetaData[1])){
			return String.class;
		}else if("geometry".equalsIgnoreCase(columnMetaData[1])){
			return String.class;
		}else if("multipoint".equalsIgnoreCase(columnMetaData[1])){
			return String.class;
		}else if("multilinestring".equalsIgnoreCase(columnMetaData[1])){
			return String.class;
		}else if("multipolygon".equalsIgnoreCase(columnMetaData[1])){
			return String.class;
		}else if("geometrycollection".equalsIgnoreCase(columnMetaData[1])){
			return String.class;
		}else{
			throw new RuntimeException("未知的数据库类型");
		}
	}
}
