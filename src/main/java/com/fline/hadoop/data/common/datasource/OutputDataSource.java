package com.fline.hadoop.data.common.datasource;

import java.util.HashMap;

public abstract class OutputDataSource implements DataSource {
	protected HashMap<String, String> configMap = null;
	protected String datasourcename = null;
	public OutputDataSource(HashMap<String, String> configMap,
			String datasourcename) {
		this.configMap = new HashMap<String, String>();
		this.configMap.putAll(configMap);
		this.datasourcename = datasourcename;
	}

	@Override
	public HashMap<String, String> getDataSourceConfig() {
		// TODO Auto-generated method stub
		return configMap;
	}

	public void setDataSourceName(String datasourcename) {
		this.datasourcename = datasourcename;
	}

	@Override
	public String getDataSourceName() {
		return this.datasourcename;
	}
}
