package com.hetao.fx;

import java.util.HashMap;

import junit.framework.TestCase;

import com.fline.hadoop.data.client.DataProgressListener;
import com.fline.hadoop.data.client.DataTransporter;
import com.fline.hadoop.data.common.Constant;
import com.fline.hadoop.data.common.datasource.InputDataSource;
import com.fline.hadoop.data.common.datasource.InputDataSourceFactory;
import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.common.datasource.OutputDataSourceFactory;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource.FileInputDataSource;
import com.fline.hadoop.data.common.datasource.impl.HBaseDataSource;
import com.fline.hadoop.data.common.datasource.impl.HBaseDataSource.HBaseOutputDataSource;
import com.fline.hadoop.data.common.event.ProgressEvent;
import com.fline.hadoop.data.common.event.ProgressSource;

public class UploadFile2Hbase extends TestCase {
	public void testuploadCSV2HBASE() throws Exception {
		System.setProperty("hadoop.home.dir",
				"N:/hadoop-common-2.2.0-bin-master");
		// File Config
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(FileDataSource.CONFIG_FILE_PATH,
				"D:/multicar_error_bayonet.csv");
		configMap.put(FileInputDataSource.CONFIG_ANALYSIS_READER,
				FileInputDataSource.LINE_READER);
		configMap.put(FileInputDataSource.CONFIG_ANALYSZER_DRIVER,
				FileInputDataSource.CSV_ANALYZER);
		configMap.put(FileInputDataSource.CONFIG_ANALYZER_SCHEMA, "\t");
		InputDataSource inputdatasource = InputDataSourceFactory
				.createInputDataSource(Constant.NORMAL_FILE_DATASOURCE,
						configMap);
		// HBASE CONFIG
		HashMap<String, String> hbaseconfigMap = new HashMap<String, String>();
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZOOKEEPERLIST,
				"hdp-master:2181");
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZKNODE,
				"/hbase-unsecure");
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_TABLENAME,
				"sqoop_csv_hbase_table");
		hbaseconfigMap
				.put(HBaseOutputDataSource.CONFIG_HBASE_COLUMNSMAP,
						"fm:key,f1:id,f1:idname,f1:time,f1:lat,f1:ln,f2:id,f2:idname,f2:time,f2:lat,f2:ln,fm:dis,fm:cos");
		hbaseconfigMap.put(
				HBaseOutputDataSource.CONFIG_HBASE_ROWKEYGENEREATEDWAY,
				HBaseOutputDataSource.ROWKEY_GENERATED_BY_NORMAL);
		hbaseconfigMap
				.put(HBaseOutputDataSource.CONFIG_HBASE_ROWKEYPARAMS, "0");
		OutputDataSource hbase = OutputDataSourceFactory 
				.createOutputDataSource(Constant.HBASE_DATASOURCE,
						hbaseconfigMap);
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
		DataTransporter.createTransJob(inputdatasource, hbase, listener);
	}

	public void testuploadZIPCSV2HBASE() throws Exception {
		System.setProperty("hadoop.home.dir",
				"N:/hadoop-common-2.2.0-bin-master");
		// File Config
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(FileDataSource.CONFIG_FILE_PATH,
				"./testinput/small_multicar_input.zip");
		configMap.put(FileInputDataSource.CONFIG_ANALYSIS_READER,
				FileInputDataSource.ZIP_READER);
		configMap.put(FileInputDataSource.CONFIG_ANALYSZER_DRIVER,
				FileInputDataSource.CSV_ANALYZER);
		configMap.put(FileInputDataSource.CONFIG_ANALYZER_SCHEMA, "\t");
		InputDataSource inputdatasource = InputDataSourceFactory
				.createInputDataSource(Constant.NORMAL_FILE_DATASOURCE,
						configMap);
		// HBASE CONFIG
		HashMap<String, String> hbaseconfigMap = new HashMap<String, String>();
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZOOKEEPERLIST,
				"hdp-master:2181");
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZKNODE,
				"/hbase-unsecure");
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_TABLENAME,
				"sqoop_zipcsv_hbase_table");
		hbaseconfigMap
				.put(HBaseOutputDataSource.CONFIG_HBASE_COLUMNSMAP,
						"fm:key,f1:id,f1:idname,f1:time,f1:lat,f1:ln,f2:id,f2:idname,f2:time,f2:lat,f2:ln,fm:dis,fm:cos");
		hbaseconfigMap.put(
				HBaseOutputDataSource.CONFIG_HBASE_ROWKEYGENEREATEDWAY,
				HBaseOutputDataSource.ROWKEY_GENERATED_BY_NORMAL);
		hbaseconfigMap
				.put(HBaseOutputDataSource.CONFIG_HBASE_ROWKEYPARAMS, "0");
		OutputDataSource hbase = OutputDataSourceFactory
				.createOutputDataSource(Constant.HBASE_DATASOURCE,
						hbaseconfigMap);
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
		DataTransporter.createTransJob(inputdatasource, hbase, listener);
	}
}
