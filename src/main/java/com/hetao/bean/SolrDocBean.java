package com.hetao.bean;

import java.util.Date;

public class SolrDocBean {
	private String filename;
	private String createdTime;
	private String filecontent;
	private String hdfspath;
	private String label;
	private String username;
	private String filesize;
	private String sourceType;

	public String getSourceType() {
		return sourceType;
	}
	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}
	public String getFilesize() {
		return filesize;
	}
	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getCreatedTime() {
		return createdTime;
	}
	public void setCreatedTime(String createdTime) {
		this.createdTime = createdTime;
	}
	public String getFilecontent() {
		return filecontent;
	}
	public void setFilecontent(String filecontent) {
		this.filecontent = filecontent;
	}
	public String getHdfspath() {
		return hdfspath;
	}
	public void setHdfspath(String hdfspath) {
		this.hdfspath = hdfspath;
	}
}
