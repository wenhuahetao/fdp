package com.fline.hadoop.data.common.analysis.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.fline.hadoop.data.common.analysis.Analyzer;
import com.fline.hadoop.data.core.writer.RecordsWriter;

public class CSVAnalyzer implements Analyzer {
	private boolean ready = false;
	private String splitgap = null;
	private List<String[]> records = null;

	public CSVAnalyzer(String splitgap) {
		records = new ArrayList<String[]>();
		if (splitgap == null) {
			ready = false;
		} else {
			this.splitgap = splitgap;
			ready = true;
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
		String[] splits = s.split(splitgap);
		records.add(splits);
	}

	@Override
	public void output(RecordsWriter writer) {
		// TODO Auto-generated method stub
		writer.writeRecords(records);
	}

	@Override
	public double compareSimilarity(String data1, String data2) {
		// TODO Auto-generated method stub
		String[] splits_1 = data1.split(splitgap);
		String[] splits_2 = data2.split(splitgap);
		HashSet<String> set = new HashSet<String>();
		for (String split : splits_1) {
			set.add(split);
		}
		int matched = 0;
		for (String split : splits_2) {
			if (set.contains(split)) {
				matched++;
			}
		}
		return ((double) matched) / Math.max(splits_1.length, splits_2.length);
	}
}
