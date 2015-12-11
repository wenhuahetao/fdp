package com.hetao.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.fline.hadoop.data.client.DataProgressListener;
import com.fline.hadoop.data.client.DataTransporter;
import com.fline.hadoop.data.common.Constant;
import com.fline.hadoop.data.common.datasource.InputDataSource;
import com.fline.hadoop.data.common.datasource.InputDataSourceFactory;
import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.common.datasource.OutputDataSourceFactory;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource;
import com.fline.hadoop.data.common.datasource.impl.HBaseDataSource;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource.FileInputDataSource;
import com.fline.hadoop.data.common.datasource.impl.HBaseDataSource.HBaseOutputDataSource;
import com.fline.hadoop.data.common.event.ProgressEvent;
import com.fline.hadoop.data.common.event.ProgressSource;
import com.fline.hadoop.data.util.DB_ORM;
import com.fline.hadoop.data.util.bigdata.HBaseOperator;
import com.fline.hadoop.data.util.rdb.MysqlDB;
import com.fline.hadoop.data.util.rdb.OracleDB;
import com.hetao.bean.ColValue;
import com.hetao.bean.DbInfo;
import com.hetao.bean.HbaseInfo;
import com.hetao.bean.TableInfo;
import com.hetao.service.HbaseService;
import com.hetao.util.ResourceUtils;

@Service("hbaseService")
public class HbaseServiceImpl implements HbaseService {

	public void update(String tablename, String colnames, String line,byte[] rowkey) throws Exception{
		HBaseOperator operator = new HBaseOperator();
		operator.update(tablename, colnames, line, rowkey);
	}
	public void delete(String tablename, byte[] rowkey) throws Exception{
		HBaseOperator operator = new HBaseOperator();
		operator.delete(tablename, rowkey);
	}
	@Override
	public void transRDB2HBASEWithIndexOnSolr(String connectionurl, String dbName,
			String driver, String username, String password, String tablename,
			String rdbcolumns, String partitioncolumn, int linenum,
			String hbasetable, String hbasecolumns, String rowkeyparam,
			String solrmasterurl, String label){
		try {
			String newConnectionUrl = "";
			if(driver.contains("mysql")) {
				newConnectionUrl = "jdbc:mysql://"+connectionurl+":3306/"+dbName;
			} else {
				//TODO construct oracle connection url 
			}
			DataProgressListener listener = new DataProgressListener() {
				@Override
				public void handleEvent(ProgressEvent e) {
					if (e.getSource() instanceof ProgressSource) {
						System.out.println(((ProgressSource) e.getSource())
								.getStat());
					}
				}
			};
			DataTransporter.transRDB2HBASEWithIndexOnSolr(newConnectionUrl, driver, username, password, tablename, rdbcolumns, partitioncolumn, linenum, hbasetable, hbasecolumns, rowkeyparam, solrmasterurl, label,listener);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void createTable(String tablename,String[] familyName){
		 try {
			HBaseOperator operator = new HBaseOperator();
			 operator.createTable(tablename, familyName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public DB_ORM getDB(DbInfo dbInfo){
		DB_ORM rdb = null;
		if(dbInfo.getDbtype().equals("mysql")){
			rdb = new MysqlDB(dbInfo.getUrl(), 3306, dbInfo.getDbname(), dbInfo.getUsername(),dbInfo.getPasswd());
		}else if(dbInfo.getDbtype().equals("oracle")){
			rdb = new OracleDB(dbInfo.getUrl(), 1521, dbInfo.getDbname(), dbInfo.getUsername(),dbInfo.getPasswd());
		}
		return rdb;
	}
	
	@Override
	public List<String[]> getDataBaseField(DB_ORM rdb, String tablename){
		List<String[]> cols = null;
		if( rdb!=null){
			cols = rdb.getFields(tablename);
		}
		return cols;
	}
	
	@Override
	public List<String[]> getDataBaseField(DbInfo dbInfo, String tablename){
		DB_ORM rdb =  getDB(dbInfo);
		List<String[]> cols = null;
		if( rdb!=null){
			cols = rdb.getFields(tablename);
		}
		return cols;
	}
	
	@Override
	public List<String> getDataBaseTable(DbInfo dbInfo){
		MysqlDB rdb = new MysqlDB(dbInfo.getUrl(), 3306, dbInfo.getDbname(), dbInfo.getUsername(),
				dbInfo.getPasswd());
		String[] tables = rdb.getTables();
		for (String table : tables) {
			System.out.println(table);
		}
		return  Arrays.asList(tables);
	}
	
	/**
	 * hbase list data of table
	 */
	@Override
	public TableInfo listDataByTable(String tableName){
		 try {
			 TableInfo table = new TableInfo();
			 Map<String, Set<String>> familyColums = new HashMap<String,Set<String>>();
			 List<HbaseInfo> hbaseList = new ArrayList<HbaseInfo>();
			 HBaseOperator operator = new HBaseOperator();
			 List<Object[]> results = operator.show(tableName, 13);
			 Set<String> set = new HashSet<String>();
			 for (Object[] obj : results) {
				 HbaseInfo hbaseInfo = new HbaseInfo();
				 hbaseInfo.setRowkey(new String((byte[])obj[0]));
				 Map<String,List<ColValue>> map = new HashMap<String,List<ColValue>>();
				 for (int i = 1; i < obj.length; i++) {
					String fv = obj[i].toString();
					String colum="";
					String family="";
					String colVal="";
					try {
						colum = fv.split(":")[0];
						family = fv.split(":")[1].split("=")[0];
						colVal = fv.split(":")[1].split("=")[1];
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Set<String> colums = null;
					if(familyColums.containsKey(family)){
						colums = familyColums.get(family);
						
					}else{
						colums = new HashSet<String>();
					}
					colums.add(colum);
					familyColums.put(family, colums);
					
					ColValue cv = new ColValue();
					List<ColValue> cols = null;
					if(map.containsKey(family)){
						 cols = map.get(family);
					}else{
						 cols = new ArrayList<ColValue>();
					}
					cv.setColum(colum);
					cv.setValue(colVal);
					cols.add(cv);
					map.put(family, cols);
				 }
				 hbaseInfo.setFamily(map);
				 hbaseList.add(hbaseInfo);
			 }
			 table.setFamilyColums(familyColums);
			 table.setHbaselist(hbaseList);
			 return table;
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * hbase list table
	 */
	@Override
	public List<String> listTable(){
		try {
			List<String> list = new ArrayList<String>();
			HBaseOperator operator = new HBaseOperator();
			String[] tablenames = operator.getTables();
			for (String tablename : tablenames) {
				list.add(tablename);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void uploadCSV2HBASE(String filepath,String schema) {
		System.setProperty("hadoop.home.dir","N:/hadoop-common-2.2.0-bin-master");
		// File Config
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(FileDataSource.CONFIG_FILE_PATH,filepath);
		configMap.put(FileInputDataSource.CONFIG_ANALYSIS_READER,FileInputDataSource.LINE_READER);
		configMap.put(FileInputDataSource.CONFIG_ANALYSZER_DRIVER,FileInputDataSource.CSV_ANALYZER);
		configMap.put(FileInputDataSource.CONFIG_ANALYZER_SCHEMA, schema);
		InputDataSource inputdatasource = InputDataSourceFactory.createInputDataSource(Constant.NORMAL_FILE_DATASOURCE,configMap);
		// HBASE CONFIG
		HashMap<String, String> hbaseconfigMap = new HashMap<String, String>();
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZOOKEEPERLIST,ResourceUtils.getRemoteLinuxIP()+ ":2181");
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZKNODE,"/hbase-unsecure");
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_TABLENAME,"sqoop_csv_hbase_table");
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_COLUMNSMAP,
						"fm:key,f1:id,f1:idname,f1:time,f1:lat,f1:ln,f2:id,f2:idname,f2:time,f2:lat,f2:ln,fm:dis,fm:cos");
		hbaseconfigMap.put(
				HBaseOutputDataSource.CONFIG_HBASE_ROWKEYGENEREATEDWAY,
				HBaseOutputDataSource.ROWKEY_GENERATED_BY_NORMAL);
		hbaseconfigMap
				.put(HBaseOutputDataSource.CONFIG_HBASE_ROWKEYPARAMS, "0");
		OutputDataSource hbase = OutputDataSourceFactory 
				.createOutputDataSource(Constant.HBASE_DATASOURCE,
						hbaseconfigMap);
		DataProgressListener listener = new DataProgressListener() {
			@Override
			public void handleEvent(ProgressEvent e) {
				// TODO Auto-generated method stub
				if (e.getSource() instanceof ProgressSource) {
					System.out.println(((ProgressSource) e.getSource())
							.getStat());
				}
			}
		};
		try {
			DataTransporter.createTransJob(inputdatasource, hbase, listener);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	@Override
	public void uploadZIPCSV2HBASE(String filepath, String schema) {
		System.setProperty("hadoop.home.dir",
				"N:/hadoop-common-2.2.0-bin-master");
		// File Config
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(FileDataSource.CONFIG_FILE_PATH,filepath);
		configMap.put(FileInputDataSource.CONFIG_ANALYSIS_READER,
				FileInputDataSource.ZIP_READER);
		configMap.put(FileInputDataSource.CONFIG_ANALYSZER_DRIVER,
				FileInputDataSource.CSV_ANALYZER);
		configMap.put(FileInputDataSource.CONFIG_ANALYZER_SCHEMA, schema);
		InputDataSource inputdatasource = InputDataSourceFactory
				.createInputDataSource(Constant.NORMAL_FILE_DATASOURCE,
						configMap);
		// HBASE CONFIG
		HashMap<String, String> hbaseconfigMap = new HashMap<String, String>();
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZOOKEEPERLIST,
				ResourceUtils.getRemoteLinuxIP() + ":2181");
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZKNODE,
				"/hbase-unsecure");
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_TABLENAME,
				"sqoop_zipcsv_hbase_table");
		hbaseconfigMap
				.put(HBaseOutputDataSource.CONFIG_HBASE_COLUMNSMAP,
						"fm:key,f1:id,f1:idname,f1:time,f1:lat,f1:ln,f2:id,f2:idname,f2:time,f2:lat,f2:ln,fm:dis,fm:cos");
		hbaseconfigMap.put(
				HBaseOutputDataSource.CONFIG_HBASE_ROWKEYGENEREATEDWAY,
				HBaseOutputDataSource.ROWKEY_GENERATED_BY_NORMAL);
		hbaseconfigMap
				.put(HBaseOutputDataSource.CONFIG_HBASE_ROWKEYPARAMS, "0");
		OutputDataSource hbase = OutputDataSourceFactory
				.createOutputDataSource(Constant.HBASE_DATASOURCE,
						hbaseconfigMap);
		DataProgressListener listener = new DataProgressListener() {
			@Override
			public void handleEvent(ProgressEvent e) {
				// TODO Auto-generated method stub
				if (e.getSource() instanceof ProgressSource) {
					System.out.println(((ProgressSource) e.getSource())
							.getStat());
				}
			}
		};
		try {
			DataTransporter.createTransJob(inputdatasource, hbase, listener);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	@Override
	public List<Object> searchData(String tablename, byte[] startRowkey,
			byte[] endRowkey) {
		try {
			HBaseOperator operator = new HBaseOperator();
			return operator.searchData(tablename, startRowkey, endRowkey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public List<Object[]> searchContents(String tablename, byte[] startRowkey,
			byte[] endRowkey) {
		try {
			HBaseOperator operator = new HBaseOperator();
			return operator.searchContents(tablename, startRowkey, endRowkey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public Object[] searchContent(String tablename, byte[] rowkey)
			throws Exception {
		try {
			HBaseOperator operator = new HBaseOperator();
			return	operator.searchContent(tablename, rowkey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
