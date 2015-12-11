package com.fline.hadoop.data.common.analysis.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.fline.hadoop.data.common.analysis.Analyzer;
import com.fline.hadoop.data.core.writer.RecordsWriter;

public class LogAnalyzer implements Analyzer {
	private boolean ready = false;
	private Pattern p = null;
	private static Logger logger = Logger.getLogger(LogAnalyzer.class);
	private List<String[]> records = null;

	public LogAnalyzer(String regex) {
		records = new ArrayList<String[]>();
		try {
			p = Pattern.compile(regex);
			ready = true;
		} catch (Exception e) {
			logger.error("create LogAnalyzer failed.", e);
			ready = false;
		}
	}

	@Override
	public boolean ready() {
		// TODO Auto-generated method stub
		return ready;
	}

	@Override
	public void analyse(byte[] data) {
		// TODO Auto-generated method stub
		String s = new String(data);
		Matcher m = p.matcher(s);
		if (m.find()) {
			String[] record = new String[m.groupCount() - 1];
			for (int i = 1; i < m.groupCount(); i++) {
				record[i - 1] = m.group(i);
			}
			records.add(record);
		} else {
			logger.debug("parse log failed. not data found in line = " + s);
		}
	}

	@Override
	public void output(RecordsWriter writer) {
		// TODO Auto-generated method stub
		writer.writeRecords(records);
	}

	@Override
	public double compareSimilarity(String data1, String data2) {
		// TODO Auto-generated method stub
		String[] record1 = null;
		Matcher m = p.matcher(data1);
		if (m.find()) {
			record1 = new String[m.groupCount() - 1];
			for (int i = 1; i < m.groupCount(); i++) {
				record1[i - 1] = m.group(i);
			}
		}
		String[] record2 = null;
		Matcher m2 = p.matcher(data2);
		if (m2.find()) {
			record2 = new String[m2.groupCount() - 1];
			for (int i = 1; i < m2.groupCount(); i++) {
				record2[i - 1] = m2.group(i);
			}
		}
		HashSet<String> set = new HashSet<String>();
		for (String split : record1) {
			set.add(split);
		}
		int matched = 0;
		for (String split : record2) {
			if (set.contains(split)) {
				matched++;
			}
		}
		return ((double) matched) / Math.max(record1.length, record2.length);
	}

}
