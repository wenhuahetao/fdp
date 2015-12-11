package com.fline.hadoop.data.common.analysis.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import com.fline.hadoop.data.common.analysis.ContentReader;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource;

public class ZipContentReader implements ContentReader {

	private static Logger logger = Logger.getLogger(ZipContentReader.class);
	private Enumeration<ZipEntry> zipelements = null;
	private ZipFile zipFile = null;
	private BufferedReader br = null;
	private double filelen = 0;

	@Override
	public byte[] getNextBytes() throws Exception {
		// TODO Auto-generated method stub
		if (br == null) {
			return null;
		} else {
			if (br.ready()) {
				return br.readLine().getBytes();
			} else {
				br.close();
				br = null;
				while (zipelements.hasMoreElements()) {
					ZipEntry zipEntry = (ZipEntry) zipelements.nextElement();
					if (zipEntry.isDirectory()) {
						continue;
					} else {
						InputStream is = zipFile.getInputStream(zipEntry);
						br = new BufferedReader(new InputStreamReader(is));
						break;
					}
				}
			}
		}
		if (br == null) {
			return null;
		} else {
			return br.readLine().getBytes();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean setupReader(Map<String, String> params) {
		// TODO Auto-generated method stub
		String filepath = params.get(FileDataSource.CONFIG_FILE_PATH);
		if (filepath == null) {
			logger.error("zipContentReader has not set filepath. please use key = FileDataSource.CONFIG_FILE_PATH");
			return false;
		} else {
			try {
				File tmpfile = new File(filepath);
				if (tmpfile.exists() == false) {
					throw new Exception("file does not exists.");
				} else {
					filelen = tmpfile.length();
				}
				zipFile = new ZipFile(filepath);
				zipelements = zipFile.getEntries();
				while (zipelements.hasMoreElements()) {
					ZipEntry zipEntry = (ZipEntry) zipelements.nextElement();
					if (zipEntry.isDirectory()) {
						continue;
					} else {
						InputStream is = zipFile.getInputStream(zipEntry);
						br = new BufferedReader(new InputStreamReader(is));
						break;
					}
				}
				return true;
			} catch (Exception e) {
				logger.error("uploadZipFile error", e);
				return false;
			}
		}
	}

	@Override
	public double getTotalSize() {
		// TODO Auto-generated method stub
		return filelen;
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

}
