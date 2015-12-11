package com.fline.hadoop.data.common.datasource.impl;

import java.util.HashMap;

import com.fline.hadoop.data.common.annotation.DataSourceType;
import com.fline.hadoop.data.common.datasource.OutputDataSource;

public class HBaseDataSource {
	public static final String CONFIG_HBASE_ZOOKEEPERLIST = "linkConfig.zookeeperlist";
	public static final String CONFIG_HBASE_ZKNODE = "linkConfig.zookeepernode";

	public static class HBaseOutputDataSource extends OutputDataSource {
		@DataSourceType(remote = true)
		public static final String CONFIG_HBASE_TABLENAME = "toJobConfig.tablename";
		public static final String CONFIG_HBASE_COLUMNSMAP = "toJobConfig.columnsMap";
		public static final String CONFIG_HBASE_ROWKEYGENEREATEDWAY = "toJobConfig.rowkeyGeneratedWay";
		public static final String ROWKEY_GENERATED_BY_NORMAL = "normal";
		public static final String CONFIG_HBASE_ROWKEYPARAMS = "toJobConfig.rowkeyParams";
		public static final String CONFIG_HBASE_CREATETABLE = "toJobConfig.createTable";

		public HBaseOutputDataSource(HashMap<String, String> configMap,
				String datasourcename) {
			super(configMap, datasourcename);
		}

		@Override
		public HashMap<String, String> getConfigItems() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getDataSourceDigest() {
			// TODO Auto-generated method stub
			int value = 0;
			value += this.configMap.get(CONFIG_HBASE_ZKNODE).hashCode();
			value += this.configMap.get(CONFIG_HBASE_ROWKEYPARAMS).hashCode();
			value += this.configMap.get(CONFIG_HBASE_COLUMNSMAP).hashCode();
			value += this.configMap.get(CONFIG_HBASE_TABLENAME).hashCode();
			if (value < 0)
				value = -value;
			return "hbase_"
					+ this.configMap.get(CONFIG_HBASE_ZOOKEEPERLIST).split(",")[0]
					+ String.valueOf(value);
		}

		@Override
		public String validDataSource() {
			// TODO Auto-generated method stub
			if (this.configMap.get(CONFIG_HBASE_ZOOKEEPERLIST) == null
					|| this.configMap.get(CONFIG_HBASE_ZOOKEEPERLIST).length() <= 0) {
				return "hbase config - zookeeerlist has not been set.";
			} else if (this.configMap.get(CONFIG_HBASE_ZKNODE) == null) {
				this.configMap.put(CONFIG_HBASE_ZKNODE, "/hbase");
			}
			if (this.configMap.get(CONFIG_HBASE_TABLENAME) == null) {
				return "hbase tablename == null";
			} else if (this.configMap.get(CONFIG_HBASE_COLUMNSMAP) == null) {
				return "hbase columnsMap == null";
			} else if (this.configMap.get(CONFIG_HBASE_ROWKEYGENEREATEDWAY) == null) {
				return "hbase rowkeygeneratedway == null";
			}
			if (this.configMap.get(CONFIG_HBASE_CREATETABLE) == null) {
				this.configMap.put(CONFIG_HBASE_CREATETABLE, "false");
			}
			return null;
		}

		@Override
		public boolean isremote() {
			// TODO Auto-generated method stub
			return true;
		}
	}
}
