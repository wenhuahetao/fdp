package com.fline.hadoop.data.util.rdb;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import com.fline.hadoop.data.common.datasource.impl.RDBDataSource;
import com.fline.hadoop.data.common.datasource.impl.RDBDataSource.RDBInputDataSource;
import com.fline.hadoop.data.util.DB_ORM;

public class OracleDB implements DB_ORM {
	private Connection con = null;
	private String url = null;
	private String ip = null;
	private int port = 0;
	private String dbname = null;
	private String dbtype = "oracle";
	private String driver = "oracle.jdbc.driver.OracleDriver";
	private String username = null;
	private String passwd = null;
	private HashMap<String, List<String[]>> table2description = null;

	private static Logger logger = Logger.getLogger(OracleDB.class);
	
	public OracleDB(String ip, int port, String dbname, String username,
			String passwd) {
		this.ip = ip;
		this.port = port;
		this.dbname = dbname;
		this.username = username;
		this.passwd = passwd;
		this.table2description = new HashMap<String, List<String[]>>();
		try {
			if (dbtype.equals("oracle")) {
				Class.forName(driver);
				url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + dbname;
			} else {
				throw new Exception(
						"Error input dbtype. current only support mysql and oracle... but input is "
								+ dbtype);
			}
			con = DriverManager.getConnection(url, username, passwd);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String[]> getFields(String tableName) {
		if (this.table2description.get(tableName) == null) {
			getTables();
		}
		return this.table2description.get(tableName.toUpperCase());
	}

	@Override
	public HashMap<String, String> createDBDescription(String tableName,
			String[] fields, String[] otherparams) {
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap
				.put(RDBDataSource.CONFIG_JDBC_CONNECTIONSTRING,"jdbc:oracle:thin:@" + ip + ":" + port + ":" + dbname);
		configMap.put(RDBDataSource.CONFIG_JDBC_DRIVER, "oracle.jdbc.driver.OracleDriver");
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
	
	
	public List<String[]> getTableNameList(String tableName) throws SQLException {
		DatabaseMetaData dbmd = con.getMetaData();
		ResultSet rs = dbmd.getColumns(null, "%", tableName, "%");
		List<String[]> tableNameList = new ArrayList<String[]>();
		while (rs.next()) {
			String tablename = rs.getString("TABLE_NAME");
			List<String[]> columndefs = this.table2description
					.get(tablename);
			if (columndefs == null) {
				columndefs = new ArrayList<String[]>();
				this.table2description.put(tablename, columndefs);
			}
			String[] columdef = new String[2];
			columdef[0] = rs.getString("COLUMN_NAME");
			columdef[1] = rs.getString("TYPE_NAME");
			columndefs.add(columdef);
		}
		return tableNameList;
	}
	
	@Override
	public String[] getTables() {
		this.table2description.clear();
		try {
			DatabaseMetaData dbmd = con.getMetaData();
			ResultSet rs = dbmd.getTables(con.getCatalog(), this.username.toUpperCase(), null, new String[] { "TABLE" });
			while (rs.next()) {
				String tablename = rs.getString("TABLE_NAME");
				List<String[]> columndefs = this.table2description.get(tablename);
				if (columndefs == null) {
					columndefs = new ArrayList<String[]>();
					this.table2description.put(tablename, columndefs);
				}
				columndefs.addAll(getTableNameList(tablename));
			}
			return this.table2description.keySet().toArray(new String[this.table2description.keySet().size()]);
		} catch (Exception e) {
			logger.error("getTables error.", e);
			return null;
		}
	}

	public String[] getPriKeys(String tableName) {
		try {
			DatabaseMetaData dbmd = con.getMetaData();
			ResultSet rs = dbmd.getColumns(null, "%", tableName.toUpperCase(), "%");
			List<String> cols = new ArrayList<String>();
			while (rs.next()) {
				cols.add(rs.getString("COLUMN_NAME"));
			}
			return cols.toArray(new String[cols.size()]);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
		OracleDB rdb = new OracleDB("localhost", 1521, "orcl", "hetaotest",
				"hetaotest");
	
		String[] tables = rdb.getTables();
		for (String table : tables) {
			System.out.println(table);
		}
		// List<String[]> cols = rdb.getFields("sqoop_test");
		String[] cols = rdb.getPriKeys("test1");
		// for (String[] col : cols) {
		// System.out.println(col[0] + "\t" + col[1]);
		// }
		for (String col : cols) {
			System.out.println(col);
		}
		List<String[]> list = rdb.getTableNameList("test1");
		for (String[] strings : list) {
			System.out.println(strings);
		}
		System.out.println(rdb.countTableSize("test1"));
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
