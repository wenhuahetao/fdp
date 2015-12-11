package com.fline.hadoop.data.common.datasource.impl;

import java.util.HashMap;

import com.fline.hadoop.data.common.Constant;
import com.fline.hadoop.data.common.datasource.InputDataSource;

public class RDBDataSource {
	// linkConfig
	public static final String CONFIG_JDBC_CONNECTIONSTRING = "linkConfig.connectionString";
	public static final String CONFIG_JDBC_DRIVER = "linkConfig.jdbcDriver";
	public static final String CONFIG_JDBC_USERNAME = "linkConfig.username";
	public static final String CONFIG_JDBC_USERPASSWD = "linkConfig.password";

	public static class RDBInputDataSource extends InputDataSource {
		// fromConfig
		public static final String CONFIG_JDBC_SCHEMA = "fromJobConfig.schemaName";
		public static final String CONFIG_JDBC_TABLE = "fromJobConfig.tableName";
		public static final String CONFIG_JDBC_COLUMNS = "fromJobConfig.columns";
		public static final String CONFIG_SQL = "fromJobConfig.sql";
		public static final String CONFIG_JDBC_PARTITIONCOLUMN = "fromJobConfig.partitionColumn";
		public static final String CONFIG_INCRE_COLUMN = "incrementalRead.checkColumn";
		public static final String CONFIG_INCRE_LASTVALUE = "incrementalRead.lastValue";

		public RDBInputDataSource(HashMap<String, String> configMap,
				String datasourcename) {
			super(configMap, datasourcename);
			// TODO Auto-generated constructor stub
		}

		@Override
		public HashMap<String, String> getConfigItems() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDataSourceDigest() {
			// TODO Auto-generated method stub
			int value = this.configMap.get(CONFIG_JDBC_USERNAME).hashCode()
					+ this.configMap.get(CONFIG_JDBC_CONNECTIONSTRING)
							.hashCode()
					+ ((this.configMap.get(CONFIG_JDBC_COLUMNS) == null) ? "null"
							.hashCode() : (this.configMap
							.get(CONFIG_JDBC_COLUMNS).hashCode()))
					+ ((this.configMap.get(CONFIG_JDBC_TABLE) == null) ? "null"
							.hashCode() : (this.configMap
							.get(CONFIG_JDBC_TABLE).hashCode()));
			if (value < 0) {
				value = -value;
			}
			return "rdb_" + String.valueOf(value);
		}

		@Override
		public String validDataSource() {
			// TODO Auto-generated method stub
			if (this.configMap.get(CONFIG_JDBC_CONNECTIONSTRING) == null) {
				return "RDBInputDataSource.CONNECTIONSTRING has not been set... please added it to configMap.";
			} else if (this.configMap.get(CONFIG_JDBC_DRIVER) == null) {
				if (this.configMap.get(CONFIG_JDBC_CONNECTIONSTRING)
						.startsWith("jdbc:mysql")) {
					this.configMap.put(CONFIG_JDBC_DRIVER,
							Constant.MYSQL_DRIVER);
				} else {
					return "unknown jdbc driver... It has not been set, and connection is not default mysql connection";
				}
			}
			if (this.configMap.get(CONFIG_JDBC_USERNAME) == null) {
				return "JDBC_USER_NAME == null";
			} else if (this.configMap.get(CONFIG_JDBC_USERPASSWD) == null) {
				return "JDBC_USER_PASSWD == null";
			} else if (this.configMap.get(CONFIG_JDBC_TABLE) == null) {
				return "JDBC_TABLE has not been set.";
			} else {
				return null;
			}
		}

		@Override
		public boolean isremote() {
			// TODO Auto-generated method stub
			return true;
		}

	}
}
