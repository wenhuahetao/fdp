package com.fline.hadoop.data.core.writer;

import java.util.Map;

import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource.FileInputDataSource;

public abstract class AbstractRecordsWriter implements RecordsWriter {
	protected Map<String, String> config = null;
	protected int[] resulttypes;

	public AbstractRecordsWriter(OutputDataSource outputdatasource) {
		config = outputdatasource.getDataSourceConfig();
		String resulttypedef = config
				.get(FileInputDataSource.CONFIG_ANALZYER_RESULTTYPE);
		if (resulttypedef != null) {
			String[] tmpresulttypes = resulttypedef.split(",");
			resulttypes = new int[tmpresulttypes.length];
			for (int i = 0; i < resulttypes.length; i++) {
				resulttypes[i] = Integer.valueOf(tmpresulttypes.length);
			}
		}
	}

	public AbstractRecordsWriter(Map<String, String> config) {
		this.config = config;
	}

	protected Object[] format(String[] sourcedata) {
		if (resulttypes == null || resulttypes.length < sourcedata.length) {
			return sourcedata;
		} else {
			Object[] retdata = new Object[sourcedata.length];
			for (int i = 0; i < sourcedata.length; i++) {
				if (sourcedata[i] == null) {
					retdata[i] = null;
					continue;
				}
				switch (resulttypes[i]) {
				case FileInputDataSource.INT_TYPE:
					retdata[i] = Integer.valueOf(sourcedata[i]);
					break;
				case FileInputDataSource.LONG_TYPE:
					retdata[i] = Long.valueOf(sourcedata[i]);
					break;
				case FileInputDataSource.DOUBLE_TYPE:
					retdata[i] = Double.valueOf(sourcedata[i]);
					break;
				default:
					retdata[i] = sourcedata[i];
					break;
				}
			}
			return retdata;
		}
	}
}
