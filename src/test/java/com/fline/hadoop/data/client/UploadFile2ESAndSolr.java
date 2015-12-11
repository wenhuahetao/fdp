package com.fline.hadoop.data.client;

import java.util.HashMap;

import junit.framework.TestCase;

import com.fline.hadoop.data.common.Constant;
import com.fline.hadoop.data.common.datasource.InputDataSource;
import com.fline.hadoop.data.common.datasource.InputDataSourceFactory;
import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.common.datasource.OutputDataSourceFactory;
import com.fline.hadoop.data.common.datasource.impl.ElasticSearchDataSource;
import com.fline.hadoop.data.common.datasource.impl.ElasticSearchDataSource.ESOutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource.FileInputDataSource;
import com.fline.hadoop.data.common.datasource.impl.SolrDataSource;
import com.fline.hadoop.data.common.datasource.impl.SolrDataSource.SolrOutputDataSource;
import com.fline.hadoop.data.common.event.ProgressEvent;
import com.fline.hadoop.data.common.event.ProgressSource;

public class UploadFile2ESAndSolr extends TestCase {
	public void testuploadFile2ES() throws Exception {
		System.setProperty("hadoop.home.dir",
				"N:/hadoop-common-2.2.0-bin-master");
		// File Config
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(FileDataSource.CONFIG_FILE_PATH,
				"./testinput/multicar_error_bayonet.csv");
		configMap.put(FileInputDataSource.CONFIG_ANALYSIS_READER,
				FileInputDataSource.LINE_READER);
		configMap.put(FileInputDataSource.CONFIG_ANALYSZER_DRIVER,
				FileInputDataSource.CSV_ANALYZER);
		configMap.put(FileInputDataSource.CONFIG_ANALYZER_SCHEMA, "\t");
		InputDataSource inputdatasource = InputDataSourceFactory
				.createInputDataSource(Constant.NORMAL_FILE_DATASOURCE,
						configMap);
		// es Config
		HashMap<String, String> esconfigMap = new HashMap<String, String>();
		esconfigMap.put(ElasticSearchDataSource.CONFIG_ES_HOSTNAME,
				"hdp-master");
		esconfigMap.put(ElasticSearchDataSource.CONFIG_ES_HOSTPORT, "9300");
		esconfigMap.put(ESOutputDataSource.CONFIG_ES_INDEXNAME, "testindex");
		esconfigMap.put(ESOutputDataSource.CONFIG_ES_INDEXTYPE, "testtype");
		esconfigMap
				.put(ESOutputDataSource.CONFIG_ES_COLUMNNAMES,
						"fm_key,f1_id,f1_idname,f1_time,f1_lat,f1_ln,f2_id,f2_idname,f2_time,f2_lat,f2_ln,fm_dis,fm_cos");
		OutputDataSource es = OutputDataSourceFactory.createOutputDataSource(
				Constant.ES_DATASOURCE, esconfigMap);
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
		DataTransporter.createTransJob(inputdatasource, es, listener);
	}

	public void testuploadFile2Solr() throws Exception {
		System.setProperty("hadoop.home.dir",
				"N:/hadoop-common-2.2.0-bin-master");
		// File Config
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(FileDataSource.CONFIG_FILE_PATH,
				"./testinput/small_multicar_input_for_solr_format.csv");
		configMap.put(FileInputDataSource.CONFIG_ANALYSIS_READER,
				FileInputDataSource.LINE_READER);
		configMap.put(FileInputDataSource.CONFIG_ANALYSZER_DRIVER,
				FileInputDataSource.CSV_ANALYZER);
		configMap.put(FileInputDataSource.CONFIG_ANALYZER_SCHEMA, "\t");
		configMap.put(FileInputDataSource.CONFIG_ANALZYER_RESULTTYPE, "1,4");
		InputDataSource inputdatasource = InputDataSourceFactory
				.createInputDataSource(Constant.NORMAL_FILE_DATASOURCE,
						configMap);
		// solr CONFIG
		HashMap<String, String> solrconfig = new HashMap<String, String>();
		solrconfig.put(SolrDataSource.CONFIG_SOLR_MASTERURL,
				"http://121.40.99.124:8983/solr/");
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
		DataTransporter.createTransJob(inputdatasource, solr, listener);
	}
}
