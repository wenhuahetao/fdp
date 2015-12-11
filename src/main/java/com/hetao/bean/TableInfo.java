package com.hetao.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TableInfo {

	private Map<String, Set<String>> familyColums = new HashMap<String,Set<String>>();
	private List<HbaseInfo> hbaselist = new ArrayList<HbaseInfo>();
	public Map<String, Set<String>> getFamilyColums() {
		return familyColums;
	}
	public void setFamilyColums(Map<String, Set<String>> familyColums) {
		this.familyColums = familyColums;
	}
	public List<HbaseInfo> getHbaselist() {
		return hbaselist;
	}
	public void setHbaselist(List<HbaseInfo> hbaselist) {
		this.hbaselist = hbaselist;
	}

}
