package com.fline.hadoop.data.common.analysis.impl;

import java.util.Map;

import com.fline.hadoop.data.common.analysis.Analyzer;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource.FileInputDataSource;

public class AnalyzerFactory {

	public static Analyzer createAnalyzer(Map<String, String> config)
			throws Exception {
		String analyzerdriver = config
				.get(FileInputDataSource.CONFIG_ANALYSZER_DRIVER);
		String schema = config.get(FileInputDataSource.CONFIG_ANALYZER_SCHEMA);
		if (analyzerdriver == FileInputDataSource.CSV_ANALYZER) {
			return new CSVAnalyzer(schema);
		} else if (analyzerdriver == FileInputDataSource.LOG_ANALYZER) {
			return new LogAnalyzer(schema);
		} else if (analyzerdriver == FileInputDataSource.TOKEN_ANALYZER){
			return new TokenAnalyzer();
		}else {
			throw new Exception("Unsupport analyzer driver.");
		}
	}

}
