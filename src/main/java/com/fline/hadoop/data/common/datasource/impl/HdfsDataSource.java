package com.fline.hadoop.data.common.datasource.impl;

import java.util.HashMap;

import com.fline.hadoop.data.common.datasource.OutputDataSource;

public class HdfsDataSource {
	public static final String CONFIG_HDFS_CONNECTIONURI = "linkConfig.uri";
	public static final String CONFIG_HDFS_CONFDIR = "linkConfig.confDir";

	public static class HdfsOutputDataSource extends OutputDataSource {

		public static final String CONFIG_HDFS_OUTPUTPATH = "toJobConfig.outputDirectory";

		public HdfsOutputDataSource(HashMap<String, String> configMap,
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
			return null;
		}

		@Override
		public String validDataSource() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isremote() {
			// TODO Auto-generated method stub
			return true;
		}

	}
}
