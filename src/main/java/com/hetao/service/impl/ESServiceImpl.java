package com.hetao.service.impl;

import java.util.HashMap;
import com.fline.hadoop.data.client.DataProgressListener;
import com.fline.hadoop.data.client.DataTransporter;
import com.fline.hadoop.data.common.Constant;
import com.fline.hadoop.data.common.datasource.InputDataSource;
import com.fline.hadoop.data.common.datasource.InputDataSourceFactory;
import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource;
import com.fline.hadoop.data.common.datasource.impl.SolrDataSource;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource.FileInputDataSource;
import com.fline.hadoop.data.common.datasource.impl.SolrDataSource.SolrOutputDataSource;
import com.fline.hadoop.data.common.event.ProgressEvent;
import com.fline.hadoop.data.common.event.ProgressSource;
import com.hetao.service.ESService;
import com.hetao.util.ResourceUtils;

public class ESServiceImpl implements ESService {

	@Override
	public void uploadFile2ES(String filepath,String schema) {
		System.setProperty("hadoop.home.dir",
				"N:/hadoop-common-2.2.0-bin-master");
		// File Config
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(FileDataSource.CONFIG_FILE_PATH,
				filepath);
		configMap.put(FileInputDataSource.CONFIG_ANALYSIS_READER,
				FileInputDataSource.LINE_READER);
		configMap.put(FileInputDataSource.CONFIG_ANALYSZER_DRIVER,
				FileInputDataSource.CSV_ANALYZER);
		configMap.put(FileInputDataSource.CONFIG_ANALYZER_SCHEMA, schema);
		configMap.put(FileInputDataSource.CONFIG_ANALZYER_RESULTTYPE, "1,4");
		InputDataSource inputdatasource = InputDataSourceFactory
				.createInputDataSource(Constant.NORMAL_FILE_DATASOURCE,
						configMap);
		// solr CONFIG
		HashMap<String, String> solrconfig = new HashMap<String, String>();
		solrconfig.put(SolrDataSource.CONFIG_SOLR_MASTERURL,
				ResourceUtils.getSolrMasterUrl());
		solrconfig.put(SolrOutputDataSource.CONFIG_SOLR_COLUMNS, "id,name");
		solrconfig.put(SolrOutputDataSource.CONFIG_SOLR_INSTANCE,
				"core_for_test");
		OutputDataSource solr = new SolrOutputDataSource(solrconfig,
				Constant.SOLR_DATASOURCE_NAME);
		DataProgressListener listener = new DataProgressListener() {
			@Override
			public void handleEvent(ProgressEvent e) {
				// TODO Auto-generated method stub
				if (e.getSource() instanceof ProgressSource) {
					System.out.println(((ProgressSource) e.getSource())
							.getStat());
				}
			}
		};
		try {
			DataTransporter.createTransJob(inputdatasource, solr, listener);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}
}
