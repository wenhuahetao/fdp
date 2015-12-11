package com.fline.hadoop.data.core.engine;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;

import com.fline.hadoop.data.client.DataProgressListener;
import com.fline.hadoop.data.common.HDFSOperator;
import com.fline.hadoop.data.common.analysis.Analyzer;
import com.fline.hadoop.data.common.analysis.ContentReader;
import com.fline.hadoop.data.common.datasource.InputDataSource;
import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource.FileInputDataSource;
import com.fline.hadoop.data.common.datasource.impl.HdfsDataSource;
import com.fline.hadoop.data.common.datasource.impl.HdfsDataSource.HdfsOutputDataSource;
import com.fline.hadoop.data.common.event.ProgressSource;
import com.fline.hadoop.data.core.writer.RecordsWriter;
import com.fline.hadoop.data.core.writer.RecordsWriterFactory;

public class LocalFileInputEngine {
	/**
	 * upload local file to hdfs.
	 * 
	 * @param fileinputdatasource
	 * @param outputdatasource
	 * @throws Exception
	 */
	public static void uploadFile2HDFS(InputDataSource fileinputdatasource,
			OutputDataSource outputdatasource) throws Exception {
		if (!(fileinputdatasource instanceof FileInputDataSource)) {
			throw new Exception(
					"uploadFile2HDFS does not support InputDataSource : "
							+ fileinputdatasource.getClass().toString());
		}
		HashMap<String, String> inputconfigMap = fileinputdatasource
				.getDataSourceConfig();
		if (outputdatasource instanceof HdfsOutputDataSource) {
			Configuration conf = new Configuration();
			HashMap<String, String> configMap = outputdatasource
					.getDataSourceConfig();
			conf.set("fs.defaultFS",
					configMap.get(HdfsDataSource.CONFIG_HDFS_CONNECTIONURI));
			File confdir = new File(
					configMap.get(HdfsDataSource.CONFIG_HDFS_CONFDIR));
			if (confdir.exists()) {
				String[] files = confdir.list();
				for (String file : files) {
					if (file.endsWith("-site.xml")) {
						conf.addResource(new FileInputStream(new File(confdir
								.getAbsolutePath() + "/" + file)));
					}
				}
			}
			HDFSOperator operator = new HDFSOperator(conf);

			if (inputconfigMap.get(FileInputDataSource.CONFIG_ANALYSIS_READER)
					.endsWith(FileInputDataSource.ZIP_READER)) {
				operator.uploadZipFile(inputconfigMap
						.get(FileDataSource.CONFIG_FILE_PATH), configMap
						.get(HdfsOutputDataSource.CONFIG_HDFS_OUTPUTPATH), null);
				operator.close();
			} else {
				operator.uploadFile(
						inputconfigMap.get(FileDataSource.CONFIG_FILE_PATH),
						configMap
								.get(HdfsOutputDataSource.CONFIG_HDFS_OUTPUTPATH)
								+ "/"
								+ new File(inputconfigMap
										.get(FileDataSource.CONFIG_FILE_PATH))
										.getName(), true, null);
				operator.close();
			}
		}
	}

	/**
	 * write data to outputdatasource from fileinputdatasource
	 * 
	 * @param fileinputdatasource
	 * @param outputdatasource
	 * @param listener
	 * @throws Exception
	 */
	public static void transDataTo(FileInputDataSource fileinputdatasource,
			OutputDataSource outputdatasource, Analyzer analyzer,
			DataProgressListener listener) throws Exception {
		if (outputdatasource instanceof HdfsOutputDataSource) {
			uploadFile2HDFS(fileinputdatasource, outputdatasource);
		} else {
			String contentreaderclass = fileinputdatasource
					.getDataSourceConfig().get(
							FileInputDataSource.CONFIG_ANALYSIS_READER);
			ContentReader reader = (ContentReader) Class.forName(
					contentreaderclass).newInstance();
			reader.setupReader(fileinputdatasource.getDataSourceConfig());
			RecordsWriter writer = RecordsWriterFactory
					.createRecordsWriter(outputdatasource);

			ProgressSource source = new ProgressSource();
			source.addProgressListener(listener);
			byte[] contentbytes = null;
			while ((contentbytes = reader.getNextBytes()) != null) {
				analyzer.analyse(contentbytes);
			}
			analyzer.output(writer);
			reader.close();
			writer.close();
		}
	}
}
