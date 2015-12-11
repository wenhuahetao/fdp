package com.hetao.service;

import java.util.List;

import com.fline.hadoop.data.util.DB_ORM;
import com.hetao.bean.DbInfo;
import com.hetao.bean.TableInfo;

public interface HbaseService {
	
	public void uploadCSV2HBASE(String filepath,String schema);
	public void uploadZIPCSV2HBASE(String filepath,String schema);
	public List<String> listTable();
	public TableInfo listDataByTable(String tableName);
	public List<String> getDataBaseTable(DbInfo dbInfo);
	public List<String[]> getDataBaseField(DbInfo dbInfo,String tablename);
	public void transRDB2HBASEWithIndexOnSolr(String connectionurl,String dbName,
			String driver, String username, String password, String tablename,
			String rdbcolumns, String partitioncolumn, int linenum,
			String hbasetable, String hbasecolumns, String rowkeyparam,
			String solrmasterurl, String label);
	public void createTable(String tablename,String[] familyName);
	public void update(String tablename, String colnames, String line,byte[] rowkey) throws Exception;
	public void delete(String tablename, byte[] rowkey) throws Exception;
	public List<Object> searchData(String tablename, byte[] startRowkey,byte[] endRowkey);
	public List<Object[]> searchContents(String tablename, byte[] startRowkey,byte[] endRowkey);
	public Object[] searchContent(String tablename, byte[] rowkey)throws Exception;
	public DB_ORM getDB(DbInfo dbInfo);
	public List<String[]> getDataBaseField(DB_ORM rdb, String tablename);
}
