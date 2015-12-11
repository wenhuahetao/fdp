package com.fline.hadoop.data.common.datasource.impl;

import java.util.HashMap;

import com.fline.hadoop.data.common.datasource.OutputDataSource;

public class SolrDataSource {
	public static final String CONFIG_SOLR_MASTERURL = "linkConfig.solrmasterurl";

	public static class SolrOutputDataSource extends OutputDataSource {
		public static final String CONFIG_SOLR_INSTANCE = "toJobConfig.instance";
		public static final String CONFIG_SOLR_COLUMNS = "toJobConfig.columns";
		
		//not nessary
		public static final String CONFIG_RDB_TABLENAME = "rdb_tablename";
		public static final String CONFIG_RDB_DBNAME = "rdb_name";
		public static final String CONFIG_LINECOUNT = "rdb_linecount";
		public static final String CONFIG_LABEL = "label";

		public SolrOutputDataSource(HashMap<String, String> configMap,
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
			return "solr_"
					+ (configMap.get(CONFIG_SOLR_MASTERURL)
							+ configMap.get(CONFIG_SOLR_INSTANCE) + configMap
								.get(CONFIG_SOLR_COLUMNS)).hashCode();
		}

		@Override
		public String validDataSource() {
			// TODO Auto-generated method stub
			if (configMap.get(CONFIG_SOLR_MASTERURL) == null) {
				return "solr master url could not be empty. please set configmap with key = SolrDataSource.CONFIG_SOLR_MASTERURL";
			}
			if (configMap.get(CONFIG_SOLR_INSTANCE) == null) {
				return "solr instance could not be empty. please set configmap with key = SolrOutputDataSource.CONFIG_SOLR_INSTANCE";
			}
			if (configMap.get(CONFIG_SOLR_COLUMNS) == null) {
				return "solr columns could not be empty. please set configmap with key = SolrOutputDataSource.CONFIG_SOLR_COLUMNS";
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
