package com.fline.hadoop.data.common.analysis.impl;

import java.util.ArrayList;
import java.util.List;

import com.fline.hadoop.data.common.analysis.Analyzer;
import com.fline.hadoop.data.core.writer.RecordsWriter;

public class TokenAnalyzer implements Analyzer {
	private boolean ready = false;
	private List<String[]> records = null;

	public TokenAnalyzer() {
		records = new ArrayList<String[]>();
		ready = true;
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
		String[] splits = s.split(FileInfoTokenReader.DIV);
		String[] record = splits;
		records.add(record);
	}

	@Override
	public void output(RecordsWriter writer) {
		// TODO Auto-generated method stub
		writer.writeRecords(records);
	}

	@Override
	public double compareSimilarity(String data1, String data2) {
		// TODO Auto-generated method stub
		if (data1.equals(data2)) {
			return 1;
		} else {
			return 0;
		}
	}
}
