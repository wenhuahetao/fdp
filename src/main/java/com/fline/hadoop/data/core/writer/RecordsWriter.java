package com.fline.hadoop.data.core.writer;

import java.util.List;

public interface RecordsWriter {
	public void writeRecords(List<String[]> records);

	public void writeObjRecords(List<Object[]> records);

	public void close();
}
