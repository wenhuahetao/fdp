package com.fline.hadoop.data.common.analysis.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import com.fline.hadoop.data.common.analysis.ContentReader;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource;

public class DocReader implements ContentReader {
	Logger logger = Logger.getLogger(DocReader.class);
	private int totalsize = 0;
	private BufferedReader reader = null;

	@Override
	public byte[] getNextBytes() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setupReader(Map<String, String> params) {
		if (params == null || params.size() < 1) {
			logger.error("setupReader failed. params.size<1");
			return false;
		} else {
			String filepath = params.get(FileDataSource.CONFIG_FILE_PATH);
			try {
				FileInputStream fis = new FileInputStream(new File(filepath));
				totalsize = fis.available();
				XWPFWordExtractor extractor = new XWPFWordExtractor(
						new XWPFDocument(fis));
				reader = new BufferedReader(new StringReader(
						extractor.getText()));
				fis.close();
				extractor.close();
				return true;
			} catch (Exception e) {
				return false;
			}

		}
	}

	@Override
	public double getTotalSize() {
		// TODO Auto-generated method stub
		return totalsize;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		reader.close();
	}

	public static void main(String[] args) throws Exception {
		String filepath = "C:\\Users\\zhongliang\\Desktop\\车辆监控性能解决方法设计v1.0.0.docx";
		FileInputStream fis = new FileInputStream(new File(filepath));
		XWPFWordExtractor extractor = new XWPFWordExtractor(new XWPFDocument(
				fis));
		System.out.println(extractor.getText());
		fis.close();
		extractor.close();
	}
}
