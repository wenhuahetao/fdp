package com.hetao.bean;

import java.util.List;
import java.util.Map;

public class HbaseInfo {

	private String rowkey;
	private Map<String, List<ColValue>> family;

	public String getRowkey() {
		return rowkey;
	}

	public void setRowkey(String rowkey) {
		this.rowkey = rowkey;
	}

	public Map<String, List<ColValue>> getFamily() {
		return family;
	}

	public void setFamily(Map<String, List<ColValue>> family) {
		this.family = family;
	}
}
