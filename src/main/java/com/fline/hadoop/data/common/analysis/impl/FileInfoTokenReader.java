package com.fline.hadoop.data.common.analysis.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.fline.hadoop.data.common.analysis.ContentReader;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource.FileInputDataSource;

public class FileInfoTokenReader implements ContentReader {
	Logger logger = Logger.getLogger(FileContentReader.class);
	int len = 0;

	private String filename = null;
	private String username = null;
	private long createdTime = 0;
	private String sourceType = "1";
	private String label = null;
	private String hdfspath = null;

	public static final String CONFIG_LABEL = "label";
	public static final String CONFIG_USERNAME = "username";
	public static final String CONFIG_HDFS_PATH = "hdfspath";

	public static final String DIV = "@di_v@";
	private String sentence = null;

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public double getTotalSize() {
		// TODO Auto-generated method stub
		return len;
	}

	@Override
	public boolean setupReader(Map<String, String> params) {
		// TODO Auto-generated method stub
		// only need one param, as filepath
		if (params == null || params.size() < 1) {
			logger.error("setupReader failed. params.size<1");
			return false;
		} else {
			String filepath = params.get(FileDataSource.CONFIG_FILE_PATH);
			File f = new File(filepath);
			filename = f.getName();
			username = params.get(CONFIG_USERNAME);
			label = params.get(CONFIG_LABEL);
			hdfspath = params.get(CONFIG_HDFS_PATH);
			createdTime = System.currentTimeMillis();
			try {

				StringBuilder sb = new StringBuilder();
				sb.append(filename);
				sb.append(DIV);
				sb.append(username);
				sb.append(DIV);
				sb.append(createdTime);
				sb.append(DIV);
				// add content
				if (filepath.toLowerCase().endsWith("docx")) {
					FileInputStream fis = new FileInputStream(
							new File(filepath));
					XWPFWordExtractor extractor = new XWPFWordExtractor(
							new XWPFDocument(fis));
					sb.append(extractor.getText());
					fis.close();
					extractor.close();
				} else if (filepath.toLowerCase().endsWith("doc")) {
					FileInputStream fis = new FileInputStream(
							new File(filepath));
					WordExtractor extractor = new WordExtractor(fis);
					sb.append(extractor.getText());
					fis.close();
					extractor.close();
				} else {
					FileInputStream tmpfis = new FileInputStream(f);
					len = tmpfis.available();
					byte[] contentBuf = new byte[len];
					tmpfis.read(contentBuf);
					tmpfis.close();
					// stringbudiler content
					sb.append(new String(contentBuf));
				}
				sb.append(DIV);
				sb.append(sourceType);
				sb.append(DIV);
				sb.append(label);
				sb.append(DIV);
				sb.append(hdfspath);
				sb.append(DIV);
				sb.append(len);
				sb.append(DIV);
				sentence = sb.toString();
			} catch (Exception e) {
				System.err
						.println("create FileInfoTokenReader error... file not exists. filepath = "
								+ filepath);
				return false;
			}
			return true;
		}
	}

	@Override
	public byte[] getNextBytes() throws Exception {
		// TODO Auto-generated method stub
		if (sentence != null) {
			byte[] retbytes = sentence.getBytes();
			sentence = null;
			return retbytes;
		} else {
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		FileInfoTokenReader fitr = new FileInfoTokenReader();
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(FileDataSource.CONFIG_FILE_PATH,
				"./testinput/small_multicar_input_for_solr_format.csv");
		configMap.put(FileInputDataSource.CONFIG_ANALYSIS_READER,
				FileInputDataSource.FileInfoReader);
		configMap.put(FileInputDataSource.CONFIG_ANALYSZER_DRIVER,
				FileInputDataSource.TOKEN_ANALYZER);
		configMap.put(FileInfoTokenReader.CONFIG_HDFS_PATH, "HDFSPATH");
		configMap.put(FileInfoTokenReader.CONFIG_LABEL, "Testlabel");
		configMap.put(FileInfoTokenReader.CONFIG_USERNAME, "zhongliang");
		configMap.put(FileInputDataSource.CONFIG_ANALYZER_SCHEMA, "\t");
		configMap.put(FileInputDataSource.CONFIG_ANALZYER_RESULTTYPE, "1,4");
		fitr.setupReader(configMap);
		while (fitr.getNextBytes() != null)
			;
		fitr.close();
	}
}
