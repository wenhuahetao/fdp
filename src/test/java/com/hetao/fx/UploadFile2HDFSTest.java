package com.hetao.fx;

import java.util.HashMap;

import junit.framework.TestCase;

import com.fline.hadoop.data.common.Constant;
import com.fline.hadoop.data.common.datasource.InputDataSource;
import com.fline.hadoop.data.common.datasource.InputDataSourceFactory;
import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.common.datasource.OutputDataSourceFactory;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource;
import com.fline.hadoop.data.common.datasource.impl.HdfsDataSource;
import com.fline.hadoop.data.core.engine.LocalFileInputEngine;

public class UploadFile2HDFSTest extends TestCase {
	public void testUploadFile2HDFSTest() throws Exception {
		System.setProperty("hadoop.home.dir",
				"N:/hadoop-common-2.2.0-bin-master");
		// File Config
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(FileDataSource.CONFIG_FILE_PATH, "./pom.xml");
		InputDataSource fin = InputDataSourceFactory.createInputDataSource(
				Constant.NORMAL_FILE_DATASOURCE, configMap);
		// hdfs Config
		HashMap<String, String> hdfsconfigMap = new HashMap<String, String>();
		hdfsconfigMap.put(HdfsDataSource.CONFIG_HDFS_CONNECTIONURI,
				"hdfs://112.33.1.202:8020"); 
		hdfsconfigMap.put(HdfsDataSource.CONFIG_HDFS_CONFDIR, "./hadoop-conf");
		hdfsconfigMap.put(
				HdfsDataSource.HdfsOutputDataSource.CONFIG_HDFS_OUTPUTPATH,
				"./");
		OutputDataSource fou = OutputDataSourceFactory.createOutputDataSource(
				Constant.HDFS_DATASOURCE, hdfsconfigMap);
		LocalFileInputEngine.uploadFile2HDFS(fin, fou);
	}
}
