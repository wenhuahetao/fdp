package com.fline.hadoop.data.common.datasource.impl;

import java.util.HashMap;

import com.fline.hadoop.data.common.datasource.OutputDataSource;

public class ElasticSearchDataSource {
	public static final String CONFIG_ES_HOSTNAME = "linkConfig.esmastername";
	public static final String CONFIG_ES_HOSTPORT = "linkConfig.esmasterport";

	public static class ESOutputDataSource extends OutputDataSource {
		public static final String CONFIG_ES_INDEXNAME = "toJobConfig.index";
		public static final String CONFIG_ES_INDEXTYPE = "toJobConfig.type";
		public static final String CONFIG_ES_COLUMNNAMES = "toJobConfig.columns";

		public ESOutputDataSource(HashMap<String, String> configMap,
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
			return "es_"
					+ (this.configMap.get(CONFIG_ES_HOSTNAME)
							+ this.configMap.get(CONFIG_ES_HOSTPORT)
							+ this.configMap.get(CONFIG_ES_INDEXNAME)
							+ this.configMap.get(CONFIG_ES_INDEXTYPE) + this.configMap
								.get(CONFIG_ES_COLUMNNAMES)).hashCode();
		}

		@Override
		public String validDataSource() {
			// TODO Auto-generated method stub
			if (this.configMap.get(CONFIG_ES_HOSTNAME) == null) {
				return "ElasticSearchDataSource.CONFIG_ES_HOSTNAME could not be null.";
			} else if (this.configMap.get(CONFIG_ES_HOSTPORT) == null) {
				System.err
						.println("[Warning]ElasticSearchDataSource.validDataSource - use default elasticserach port = 9300");
				this.configMap.put(CONFIG_ES_HOSTPORT, "9300");
			}
			if (this.configMap.get(CONFIG_ES_INDEXNAME) == null) {
				return "ESOutputDataSource.CONFIG_ES_INDEXNAME could not be null.";
			}

			if (this.configMap.get(CONFIG_ES_INDEXTYPE) == null) {
				return "ESOutputDataSource.CONFIG_ES_INDEXTYPE could not be null.";
			}

			if (this.configMap.get(CONFIG_ES_COLUMNNAMES) == null) {
				return "ESOutputDataSource.CONFIG_ES_COLUMNNAMES could not be null. It will be used to create es json data.";
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
