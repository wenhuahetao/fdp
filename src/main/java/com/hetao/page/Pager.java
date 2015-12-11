package com.hetao.page;

import java.util.ArrayList;
import java.util.List;

public class Pager {
	
	/**
	 * 当前页码
	 */
	private int currentPage;
	/**
	 * 总页数
	 */
	private int totalPage;
	/**
	 * 总行数
	 */
	private int totalRows;
	/**
	 * 每页显示条数
	 */
	private int avgRows = 5;
	/**
	 * 原集合
	 */
	private List list;

	public Pager() {
		super();
	}
	
	public Pager(int currentPage, int avgRows, List list) {
		super();
		this.currentPage = currentPage;
		this.avgRows = avgRows;
		this.list = list;
		this.totalRows = list.size();
		this.totalPage = (this.totalRows - 1) / this.avgRows + 1;
	}

	public List getPagerList() {
		List newList = new ArrayList();
		for(int i = (currentPage - 1) * avgRows; i < totalRows && i < currentPage * avgRows; i++) {
			newList.add(list.get(i));
		}
		return newList;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public int getAvgRows() {
		return avgRows;
	}
	public void setAvgRows(int avgRows) {
		this.avgRows = avgRows;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public List getList() {
		return list;
	}

	public void setList(List list) {
		this.list = list;
	}

}