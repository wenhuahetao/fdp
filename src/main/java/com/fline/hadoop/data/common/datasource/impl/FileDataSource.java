package com.fline.hadoop.data.common.datasource.impl;

import java.io.File;
import java.util.HashMap;

import com.fline.hadoop.data.common.datasource.InputDataSource;

public class FileDataSource {
	public static final String CONFIG_FILE_PATH = "linkConfig.filepath";

	public static class FileInputDataSource extends InputDataSource {

		public static final String CONFIG_ANALYSZER_DRIVER = "jobConfig.analyszer.dirver";
		public static final String LOG_ANALYZER = "com.fline.hadoop.data.common.analysis.impl.LogAnalyzer";
		public static final String CSV_ANALYZER = "com.fline.hadoop.data.common.analysis.impl.CSVAnalyzer";
		public static final String TOKEN_ANALYZER = "com.fline.hadoop.data.common.analysis.impl.TokenAnalyzer";

		public static final String CONFIG_ANALYSIS_READER = "jobConfig.analysis.reader";
		public static final String LINE_READER = "com.fline.hadoop.data.common.analysis.impl.FileContentReader";
		public static final String ZIP_READER = "com.fline.hadoop.data.common.analysis.impl.ZipContentReader";
		public static final String FileInfoReader = "com.fline.hadoop.data.common.analysis.impl.FileInfoTokenReader";
		public static final String CONFIG_ANALYZER_SCHEMA = "schema";
		public static final String CONFIG_ANALZYER_RESULTTYPE = "resulttype";
		public static final int INT_TYPE = 1;
		public static final int DOUBLE_TYPE = 2;
		public static final int LONG_TYPE = 3;
		public static final int STRING_TYPE = 4;

		public FileInputDataSource(HashMap<String, String> configMap,
				String datasourcename) {
			super(configMap, datasourcename);
			// TODO Auto-generated constructor stub
		}

		@Override
		public HashMap<String, String> getConfigItems() {
			return null;
		}

		@Override
		public String getDataSourceDigest() {
			return null;
		}

		@Override
		public String validDataSource() {
			if (new File(this.configMap.get(CONFIG_FILE_PATH)).exists() == false) {
				return this.configMap.get(CONFIG_FILE_PATH)
						+ "does not exists.";
			}
			if (configMap.get(CONFIG_ANALYSIS_READER) == null) {
				configMap.put(CONFIG_ANALYSIS_READER, LINE_READER);
			}
			if (configMap.get(CONFIG_ANALYSZER_DRIVER) == null) {
				configMap.put(CONFIG_ANALYSZER_DRIVER, CSV_ANALYZER);
			}
			return null;
		}

		@Override
		public boolean isremote() {
			return false;
		}
	}
}
