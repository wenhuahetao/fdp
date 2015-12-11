package com.fline.hadoop.data.util.rdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import com.fline.hadoop.data.common.Constant;
import com.fline.hadoop.data.common.datasource.impl.RDBDataSource;
import com.fline.hadoop.data.common.datasource.impl.RDBDataSource.RDBInputDataSource;
import com.fline.hadoop.data.util.DB_ORM;

public class MysqlDB implements DB_ORM {
	private Connection con = null;
	private String url = null;
	private String ip = null;
	private int port = 0;
	private String dbname = null;
	private String dbtype = "mysql";
	private String driver = "com.mysql.jdbc.Driver";
	private String username = null;
	private String passwd = null;
	private HashMap<String, List<String[]>> table2description = null;

	private static Logger logger = Logger.getLogger(MysqlDB.class);

	
	public MysqlDB(String ip, int port, String dbname, String username,
			String passwd) {
		this.ip = ip;
		this.port = port;
		this.dbname = dbname;
		this.username = username;
		this.passwd = passwd;
		this.table2description = new HashMap<String, List<String[]>>();
		try {
			if (dbtype.equals("mysql")) {
				Class.forName(driver);
				url = "jdbc:mysql://" + ip + ":" + port + "/" + dbname;
			} else if (dbtype.equals("oracle")) {
				url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + dbname;
			} else {
				throw new Exception(
						"Error input dbtype. current only support mysql and oracle... but input is "
								+ dbtype);
			}
			System.out.println("url INFO :" + url);
			System.out.println("username INFO :" + username);
			System.out.println("passwd INFO :" + passwd);
			con = DriverManager.getConnection(url, username, passwd);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public List<String[]> getFields(String tableName) {
		System.out.println("getFields tableName:"+tableName);
		if (this.table2description.get(tableName.toUpperCase()) == null) {
			System.out.println("getTables:" +tableName );
			getTables();
		}else{
			System.out.println("not go in getTables:" +tableName );
		}
		return this.table2description.get(tableName.toUpperCase());
	}

	@Override
	public HashMap<String, String> createDBDescription(String tableName,
			String[] fields, String[] otherparams) {
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap
				.put(RDBDataSource.CONFIG_JDBC_CONNECTIONSTRING,
						"jdbc:mysql://" + this.ip + ":" + this.port + "/"
								+ this.dbname);
		configMap.put(RDBDataSource.CONFIG_JDBC_DRIVER, Constant.MYSQL_DRIVER);
		configMap.put(RDBDataSource.CONFIG_JDBC_USERNAME, username);
		configMap.put(RDBDataSource.CONFIG_JDBC_USERPASSWD, passwd);
		if (fields.length >= 1) {
			StringBuilder sb = new StringBuilder();
			sb.append(fields[0]);
			for (int i = 1; i < fields.length; i++) {
				sb.append(',');
				sb.append(fields[i]);
			}
			configMap
					.put(RDBInputDataSource.CONFIG_JDBC_COLUMNS, sb.toString());
		}
		if (otherparams.length > 0) {
			String partitioncolinfo = otherparams[0];
			configMap.put(RDBInputDataSource.CONFIG_JDBC_PARTITIONCOLUMN,
					partitioncolinfo);
		}
		configMap.put(RDBInputDataSource.CONFIG_JDBC_TABLE, tableName);
		return configMap;
	}

	public void setGrepCondition(String sqlcondition) {

	}

	public void setGrepCondition(String classname, String methodname,
			String[] tablefields) {

	}

	@Override
	public void close() {
		try {
			con.close();
		} catch (Exception e) {
			logger.error("close MysqlDB error.", e);
		}
	}

	@Override
	public String[] getTables() {
		// TODO Auto-generated method stub
		this.table2description.clear();
		try {
			Statement stmt = con.createStatement();
			String querySql = "SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = \""
					+ this.dbname + "\"";
			System.out.println("querySql:"+querySql);
			ResultSet rs = stmt.executeQuery(querySql);
			
			while (rs.next()) {
				String tablename = rs.getString("TABLE_NAME").toUpperCase();
				// String tablename = rs.getString(1);
				List<String[]> columndefs = this.table2description
						.get(tablename);
				if (columndefs == null) {
					columndefs = new ArrayList<String[]>();
					this.table2description.put(tablename, columndefs);
				}
				String[] columdef = new String[2];
				columdef[0] = rs.getString("COLUMN_NAME");
				columdef[1] = rs.getString("COLUMN_TYPE");
				columndefs.add(columdef);
			}
			rs.close();
			stmt.close();
			System.out.println("table2description size is :"+table2description.size());
			return this.table2description.keySet().toArray(
					new String[this.table2description.keySet().size()]);
		} catch (Exception e) {
			logger.error("getTables error.", e);
			return null;
		}
	}

	public String[] getPriKeys(String tablename) {
		try {
			Statement stmt = con.createStatement();
			String querySql = "SELECT  COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = \""
					+ this.dbname
					+ "\" and TABLE_NAME=\""
					+ tablename
					+ "\" and COLUMN_KEY=\"PRI\"";
			ResultSet rs = stmt.executeQuery(querySql);
			List<String> cols = new ArrayList<String>();
			while (rs.next()) {
				String col = rs.getString("COLUMN_NAME");
				cols.add(col);
			}
			rs.close();
			stmt.close();
			return cols.toArray(new String[cols.size()]);
		} catch (Exception e) {
			logger.error("getTables error.", e);
			return null;
		}
	}

	public int countTableSize(String tablename) {
		try {
			Statement stmt = con.createStatement();
			String querySql = "SELECT count(*) from " + tablename;
			ResultSet rs = stmt.executeQuery(querySql);
			int count = 0;
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
			stmt.close();
			return count;
		} catch (Exception e) {
			logger.error("getTables error.", e);
			return -1;
		}
	}

	public static void main(String[] args) throws Exception {
		MysqlDB rdb = new MysqlDB("121.40.214.176", 3306, "Feixian", "zzl",
				"123zzl");
		String[] tables = rdb.getTables();
		for (String table : tables) {
			System.out.println(table);
		}
		// List<String[]> cols = rdb.getFields("sqoop_test");
		String[] cols = rdb.getPriKeys("sqoop_test");
		// for (String[] col : cols) {
		// System.out.println(col[0] + "\t" + col[1]);
		// }
		for (String col : cols) {
			System.out.println(col);
		}
		System.out.println(rdb.countTableSize("sqoop_test"));
	}

	@Override
	public String getUrl() {
		return this.url;
	}
	
	@Override
	public String getDriver() {
		return driver;
	}
}
