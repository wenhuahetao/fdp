package com.fline.hadoop.data.common.analysis.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.log4j.Logger;

import com.fline.hadoop.data.common.analysis.ContentReader;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource;

public class FileContentReader implements ContentReader {
	Logger logger = Logger.getLogger(FileContentReader.class);
	BufferedReader br = null;
	int len = 0;
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		br.close();
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
			try {
				FileInputStream fis = new FileInputStream(filepath);
				len = fis.available();
				br = new BufferedReader(new InputStreamReader(fis));
			} catch (Exception e) {
				System.err
						.println("create FileContentReader error... file not exists. filepath = "
								+ filepath);
				return false;
			}
			return true;
		}
	}

	@Override
	public byte[] getNextBytes() throws Exception {
		// TODO Auto-generated method stub
		if (br.ready()) {
			return br.readLine().getBytes();
		} else {
			return null;
		}
	}
}
