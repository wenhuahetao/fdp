package com.fline.hadoop.data.core.writer;

import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.ElasticSearchDataSource.ESOutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.HBaseDataSource.HBaseOutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.SolrDataSource.SolrOutputDataSource;

public class RecordsWriterFactory {
	public static RecordsWriter createRecordsWriter(
			OutputDataSource outputdatasource) throws Exception {
		if (outputdatasource instanceof HBaseOutputDataSource) {
			return new HBaseRecordsWriter(outputdatasource);
		} else if (outputdatasource instanceof ESOutputDataSource) {
			return new ESRecordsWriter(outputdatasource);
		} else if (outputdatasource instanceof SolrOutputDataSource) {
			return new SolrRecordsWriter(outputdatasource);
		} else {
			System.err.println("Unsupport RecordsWriter");
		}
		return null;
	}
}
