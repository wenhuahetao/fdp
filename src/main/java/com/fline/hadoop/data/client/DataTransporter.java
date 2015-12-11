/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fline.hadoop.data.client;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import com.fline.hadoop.data.common.Constant;
import com.fline.hadoop.data.common.HDFSOperator;
import com.fline.hadoop.data.common.analysis.impl.AnalyzerFactory;
import com.fline.hadoop.data.common.analysis.impl.FileInfoTokenReader;
import com.fline.hadoop.data.common.analysis.impl.ScpReader;
import com.fline.hadoop.data.common.datasource.InputDataSource;
import com.fline.hadoop.data.common.datasource.InputDataSourceFactory;
import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.common.datasource.OutputDataSourceFactory;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource;
import com.fline.hadoop.data.common.datasource.impl.FileDataSource.FileInputDataSource;
import com.fline.hadoop.data.common.datasource.impl.HBaseDataSource;
import com.fline.hadoop.data.common.datasource.impl.HBaseDataSource.HBaseOutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.HdfsDataSource.HdfsOutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.RDBDataSource;
import com.fline.hadoop.data.common.datasource.impl.RDBDataSource.RDBInputDataSource;
import com.fline.hadoop.data.common.datasource.impl.SolrDataSource;
import com.fline.hadoop.data.common.datasource.impl.SolrDataSource.SolrOutputDataSource;
import com.fline.hadoop.data.common.event.ProgressEvent;
import com.fline.hadoop.data.common.event.ProgressSource;
import com.fline.hadoop.data.core.StaticConfiguration;
import com.fline.hadoop.data.core.engine.LocalFileInputEngine;
import com.fline.hadoop.data.core.engine.SqoopEngine;
import com.fline.hadoop.data.util.FileChecker;
import com.fline.hadoop.data.util.bigdata.HBaseOperator;

/**
 * This class is the client for developer user to use fline hadoop platorm's
 * data import function. User can choose the inputdatasource, outputdatasource,
 * & analyzer to handle the import progress.
 * 
 * @author zhongliang
 * 
 */
public class DataTransporter {
	public static final String CONFIG_MAPNUM = "throttlingConfig.numExtractors";
	public static final String ORACLE_JDBC_DIRVER = "oracle.jdbc.driver.OracleDriver";
	public static final String POSTGRE_JDBC_DRIVER = "org.postgresql.Driver";
	public static final String SQLSERVER_JDBC_DRIVER = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
	public static final String DB2_JDBC_DRIVER = "com.ibm.db2.jdbc.net.DB2Driver";
	public static final String MYSQL_JDBC_DRIVER = "com.mysql.jdbc.Driver";

	/**
	 * create a data transport job. The data selected from inputdatasource will
	 * be store into outputdatasource. User can listen the status of the job
	 * using ProgressListener
	 * 
	 * @param inputdatasource
	 *            datasource which is the data from
	 * @param outputdatasource
	 *            datasource which is the data to
	 * @throws Exception
	 */
	public static String createTransJob(InputDataSource inputdatasource,
			OutputDataSource outputdatasource, DataProgressListener listener)
			throws Exception {
		String validResult = null;
		if ((validResult = inputdatasource.validDataSource()) != null) {
			throw new Exception("InputDataSource valid error... Message = "
					+ validResult);
		} else if ((validResult = outputdatasource.validDataSource()) != null) {
			throw new Exception("OutputDataSource valid error...Message = "
					+ validResult);
		}
		if (inputdatasource.isremote() && outputdatasource.isremote()) {
			// remote inputdatasource, call sqoop application.
			// handle hbase columns map.
			SqoopEngine
					.addSqoopJob(inputdatasource, outputdatasource, listener);
		} else {
			if (outputdatasource instanceof HdfsOutputDataSource) {
				// if output is hdfs, then copy file to hdfs, without otherthing
				LocalFileInputEngine.uploadFile2HDFS(inputdatasource,
						outputdatasource);
			} else {
				if (inputdatasource instanceof FileInputDataSource) {
					LocalFileInputEngine.transDataTo(
							(FileInputDataSource) inputdatasource,
							outputdatasource, AnalyzerFactory
									.createAnalyzer(inputdatasource
											.getDataSourceConfig()), listener);
				}
			}
			// local execute
		}
		return null;
	}

	/**
	 * trans rdb data to hbase.
	 * 
	 * @param connectionurl
	 *            rdb url
	 * @param driver
	 *            rdb driver class. such as "com.mysql.jdbc.Driver"
	 * @param username
	 *            rdb login username
	 * @param password
	 *            rdb login password
	 * @param tablename
	 *            rdb table.
	 * @param rdbcolumns
	 *            rdb table column which selected to write to hbase
	 * @param partitioncolumn
	 *            the column which can be used to select data by set a start
	 *            value and end value
	 * @param linenum
	 *            record num
	 * @param hbasetable
	 *            output hbase table name
	 * @param hbasecolumns
	 *            the columns corresponding to rdb columns
	 * @param rowkeyparam
	 *            hbase rowkey generate param.
	 * @param solrmasterurl
	 *            such as http://fdp-master:8983/solr/
	 * @param label
	 *            solr label used for search
	 * @param listener
	 * @throws Exception
	 */
	public static void transRDB2HBASEWithIndexOnSolr(String connectionurl,
			String driver, String username, String password, String tablename,
			String rdbcolumns, String partitioncolumn, int linenum,
			String hbasetable, String hbasecolumns, String rowkeyparam,
			String solrmasterurl, String label, DataProgressListener listener)
			throws Exception {
		HashMap<String, String> rdbconfigMap = new HashMap<String, String>();
		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_CONNECTIONSTRING,
				connectionurl);
		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_DRIVER, driver);
		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_USERNAME, username);
		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_USERPASSWD, password);
		// configMap.put(RDBInputDataSource.CONFIG_JDBC_SCHEMA, "");
		rdbconfigMap.put(RDBInputDataSource.CONFIG_JDBC_COLUMNS, rdbcolumns);  
//		 rdbconfigMap.put(CONFIG_MAPNUM, "2000");
		// rdbconfigMap.put("fromJobConfig.sql", "1=1 limit " + linenum);
		 rdbconfigMap.put(CONFIG_MAPNUM, String.valueOf(linenum / 1000 + 1));
		 System.out.println("config_mapnum*********************:" + linenum);
		if (linenum <= 0) {
			rdbconfigMap.put(RDBInputDataSource.CONFIG_JDBC_TABLE, tablename);
		} else {
			rdbconfigMap.put(RDBInputDataSource.CONFIG_JDBC_TABLE,
					"(select * from " + tablename + " limit " + linenum
							+ " ) as temptable");
		}
		rdbconfigMap.put(RDBInputDataSource.CONFIG_JDBC_PARTITIONCOLUMN,
				partitioncolumn);
		InputDataSource rdb = InputDataSourceFactory.createInputDataSource(
				Constant.RDBS_DATASOURCE, rdbconfigMap);
		// HBASE CONFIG
		HashMap<String, String> hbaseconfigMap = new HashMap<String, String>();
		Configuration conf = new Configuration();
		conf.addResource(new FileInputStream(DataTransporter.class
				.getClassLoader().getResource("").getPath()
				+ "hbase-site.xml"));
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZOOKEEPERLIST,
				conf.get("hbase.zookeeper.quorum"));
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZKNODE,
				conf.get("zookeeper.znode.parent"));
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_TABLENAME,
				hbasetable);
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_COLUMNSMAP,
				hbasecolumns);
		hbaseconfigMap.put(
				HBaseOutputDataSource.CONFIG_HBASE_ROWKEYGENEREATEDWAY,
				HBaseOutputDataSource.ROWKEY_GENERATED_BY_NORMAL);
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_ROWKEYPARAMS,
				rowkeyparam);
		OutputDataSource hbase = OutputDataSourceFactory
				.createOutputDataSource(Constant.HBASE_DATASOURCE,
						hbaseconfigMap);
		// solr meta store
		HttpSolrClient metaclient = new HttpSolrClient(solrmasterurl
				+ "core_for_Meta");
		List<SolrInputDocument> sidocs = new ArrayList<SolrInputDocument>();
		SolrInputDocument sidoc = new SolrInputDocument();
		sidoc.addField("rdbtablename", tablename);
		sidoc.addField("rdbtablecols", generateDynamicSolrColumns(rdbcolumns));
		sidocs.add(sidoc);
		metaclient.add(sidocs);
		metaclient.commit();
		metaclient.close();
		// solr config
		String rdbname = null;
		if (driver.contains("mysql")) {
			rdbname = connectionurl
					.substring(connectionurl.lastIndexOf('/') + 1);
		} else if (driver.contains("oracle")) {
			rdbname = connectionurl
					.substring(connectionurl.lastIndexOf(':') + 1);
		}
		HashMap<String, String> solrconfig = new HashMap<String, String>();
		solrconfig.put(SolrDataSource.CONFIG_SOLR_MASTERURL, solrmasterurl);
		solrconfig.put(SolrOutputDataSource.CONFIG_SOLR_COLUMNS,
				"label=" + label + ",linecount=" + linenum + ",rdbname="
						+ rdbname + ",rdbtablename=" + hbasetable
						+ ",createdTime=" + System.currentTimeMillis() 
						+ ",sourceType=2@di_v@"
						+ generateDynamicSolrColumns(rdbcolumns));
		System.out.println(SolrOutputDataSource.CONFIG_SOLR_COLUMNS+"\t" +  
				"label=" + label + ",linecount=" + linenum + ",rdbname="
						+ rdbname + ",rdbtablename=" + tablename
						+ ",createdTime=" + System.currentTimeMillis()
						+ ",sourceType=2@di_v@"
						+ generateDynamicSolrColumns(rdbcolumns));
		solrconfig.put(SolrOutputDataSource.CONFIG_SOLR_INSTANCE,
				"core_for_searchDB");
		OutputDataSource solr = new SolrOutputDataSource(solrconfig,
				Constant.SOLR_DATASOURCE_NAME);
		DataTransporter.createTransJob(rdb, hbase, null);
		DataTransporter.createTransJob(rdb, solr, listener);
	}

	/**
	 * trans rdb data to hbase with incre condition .
	 * 
	 * @param connectionurl
	 *            rdb url
	 * @param driver
	 *            rdb driver class. such as "com.mysql.jdbc.Driver"
	 * @param username
	 *            rdb login username
	 * @param password
	 *            rdb login password
	 * @param tablename
	 *            rdb table.
	 * @param rdbcolumns
	 *            rdb table column which selected to write to hbase
	 * @param partitioncolumn
	 *            the column which can be used to select data by set a start
	 *            value and end value
	 * @param increCheckColumn
	 *            incre check column.
	 * @param increLastValue
	 *            start column value.
	 * @param linenum
	 *            record num
	 * @param hbasetable
	 *            output hbase table name
	 * @param hbasecolumns
	 *            the columns corresponding to rdb columns
	 * @param rowkeyparam
	 *            hbase rowkey generate param.
	 * @param solrmasterurl
	 *            such as http://fdp-master:8983/solr/
	 * @param label
	 *            solr label used for search
	 * @param listener
	 * @throws Exception
	 */
	public static void transRDBIncre2HBASEWithIndexOnSolr(String connectionurl,
			String driver, String username, String password, String tablename,
			String rdbcolumns, String partitioncolumn, String increCheckColumn,
			String increLastValue, int linenum, String hbasetable,
			String hbasecolumns, String rowkeyparam, String solrmasterurl,
			String label, DataProgressListener listener) throws Exception {
		HashMap<String, String> rdbconfigMap = new HashMap<String, String>();
		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_CONNECTIONSTRING,
				connectionurl);
		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_DRIVER, driver);
		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_USERNAME, username);
		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_USERPASSWD, password);
		// configMap.put(RDBInputDataSource.CONFIG_JDBC_SCHEMA, "");
		rdbconfigMap.put(RDBInputDataSource.CONFIG_JDBC_COLUMNS, rdbcolumns);
		rdbconfigMap.put("fromJobConfig.boundaryQuery", "select min("
				+ partitioncolumn + "),max(" + partitioncolumn + ") from "
				+ tablename + " where " + increCheckColumn + " >= "
				+ increLastValue);
		rdbconfigMap.put(CONFIG_MAPNUM, String.valueOf(linenum / 1000 + 1));
		 System.out.println("config_mapnum*********************:" + linenum);
		if (linenum <= 0) {
			rdbconfigMap.put(RDBInputDataSource.CONFIG_JDBC_TABLE, tablename);
		} else {
			rdbconfigMap.put(RDBInputDataSource.CONFIG_JDBC_TABLE,
					"(select * from " + tablename + " limit " + linenum
							+ " ) as temptable");
		}
		rdbconfigMap.put(RDBInputDataSource.CONFIG_JDBC_PARTITIONCOLUMN,
				partitioncolumn);
		InputDataSource rdb = InputDataSourceFactory.createInputDataSource(
				Constant.RDBS_DATASOURCE, rdbconfigMap);
		// HBASE CONFIG
		HashMap<String, String> hbaseconfigMap = new HashMap<String, String>();
		Configuration conf = new Configuration();
		conf.addResource(new FileInputStream(DataTransporter.class
				.getClassLoader().getResource("").getPath()
				+ "hbase-site.xml"));
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZOOKEEPERLIST,
				conf.get("hbase.zookeeper.quorum"));
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZKNODE,
				conf.get("zookeeper.znode.parent"));
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_TABLENAME,
				hbasetable);
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_COLUMNSMAP,
				hbasecolumns);
		hbaseconfigMap.put(
				HBaseOutputDataSource.CONFIG_HBASE_ROWKEYGENEREATEDWAY,
				HBaseOutputDataSource.ROWKEY_GENERATED_BY_NORMAL);
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_ROWKEYPARAMS,
				rowkeyparam);
		OutputDataSource hbase = OutputDataSourceFactory
				.createOutputDataSource(Constant.HBASE_DATASOURCE,
						hbaseconfigMap);
		// solr meta store
		HttpSolrClient metaclient = new HttpSolrClient(solrmasterurl
				+ "core_for_Meta");
		List<SolrInputDocument> sidocs = new ArrayList<SolrInputDocument>();
		SolrInputDocument sidoc = new SolrInputDocument();
		sidoc.addField("rdbtablename", tablename);
		sidoc.addField("rdbtablecols", generateDynamicSolrColumns(rdbcolumns));
		sidocs.add(sidoc);
		metaclient.add(sidocs);
		metaclient.commit();
		metaclient.close();
		// solr config
		HashMap<String, String> solrconfig = new HashMap<String, String>();
		solrconfig.put(SolrDataSource.CONFIG_SOLR_MASTERURL, solrmasterurl);
		solrconfig.put(
				SolrOutputDataSource.CONFIG_SOLR_COLUMNS,
				"label="
						+ label
						+ ",linecount="
						+ linenum
						+ ",rdbname="
						+ connectionurl.substring(connectionurl
								.lastIndexOf('/')) + ",rdbtablename="
						+ tablename + ",createdTime="
						+ System.currentTimeMillis() + ",sourceType=2@di_v@"
						+ generateDynamicSolrColumns(rdbcolumns));
		solrconfig.put(SolrOutputDataSource.CONFIG_SOLR_INSTANCE,
				"core_for_searchDB");
		OutputDataSource solr = new SolrOutputDataSource(solrconfig,
				Constant.SOLR_DATASOURCE_NAME);
		DataTransporter.createTransJob(rdb, hbase, listener);
		DataTransporter.createTransJob(rdb, solr, null);
	}

	/**
	 * 
	 * @param localfilepath
	 *            local file path
	 * @param hdfspath
	 *            path on hdfs
	 * @param overwrite
	 *            whether overwrite file on hdfs
	 * @param username
	 *            file owner
	 * @param label
	 *            user input
	 * @param solrmasterurl
	 *            solrmaster
	 * @param checkCode
	 *            1=SmartCn Check, 2=LineCheck, 3=CSV Check , <=0 means close
	 *            check
	 * @param listener
	 * @throws Exception
	 */
	public static void uploadFile2HDFSWithIndexOnSolr(String localfilepath,
			String hdfspath, boolean overwrite, String username, String label,
			String solrmasterurl, int checkCode, DataProgressListener listener)
			throws Exception {
		// autogenerate label, updated at 2015.09.25
		label = FileChecker.getAutoLabel(localfilepath);
		HDFSOperator.getDefaultInstance().uploadFile(localfilepath, hdfspath,
				overwrite, null);
		// FileChecker.checkFile(localfilepath, hdfspath, checkCode);
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(FileDataSource.CONFIG_FILE_PATH, localfilepath);
		configMap.put(FileInputDataSource.CONFIG_ANALYSIS_READER,
				FileInputDataSource.FileInfoReader);
		configMap.put(FileInputDataSource.CONFIG_ANALYSZER_DRIVER,
				FileInputDataSource.TOKEN_ANALYZER);
		configMap.put(FileInfoTokenReader.CONFIG_HDFS_PATH, hdfspath);
		if (label != null) {
			configMap.put(FileInfoTokenReader.CONFIG_LABEL, label);
		} else {
			configMap.put(FileInfoTokenReader.CONFIG_LABEL,
					FileChecker.getAutoLabel(localfilepath));
		}
		configMap.put(FileInfoTokenReader.CONFIG_USERNAME, username);
		InputDataSource inputdatasource = InputDataSourceFactory
				.createInputDataSource(Constant.NORMAL_FILE_DATASOURCE,
						configMap);
		// solr CONFIG
		HashMap<String, String> solrconfig = new HashMap<String, String>();
		solrconfig.put(SolrDataSource.CONFIG_SOLR_MASTERURL, solrmasterurl);
		solrconfig
				.put(SolrOutputDataSource.CONFIG_SOLR_COLUMNS,
						"filename,username,createdTime,filecontent,sourceType,label,hdfspath,filesize");
		solrconfig.put(SolrOutputDataSource.CONFIG_SOLR_INSTANCE,
				"core_for_searchDB");
		OutputDataSource solr = new SolrOutputDataSource(solrconfig,
				Constant.SOLR_DATASOURCE_NAME);
		DataTransporter.createTransJob(inputdatasource, solr, listener);
	}

	/**
	 * upload remote file to hdfs.
	 * 
	 * @param remotefilepath
	 * @param remoteip
	 * @param remoteusername
	 * @param remotepassword
	 * @param hdfspath
	 * @param overwrite
	 * @param uploadusername
	 * @param label
	 * @param solrmasterurl
	 * @param checkCode
	 * @throws Exception
	 */
	public static String uploadRomoteFile2HDFSWithIndexOnSolr(
			String remotefilepath, String remoteip, String remoteusername,
			String remotepassword, String hdfspath, boolean overwrite,
			String uploadusername, String label, String solrmasterurl,
			int checkCode, DataProgressListener listener) throws Exception {
		if (hdfspath.endsWith("/")) {
			hdfspath = hdfspath.substring(0, hdfspath.length() - 1);
		}
		ScpReader reader = new ScpReader();
		Map<String, String> map = new HashMap<String, String>();
		map.put(ScpReader.REMOTE_LINUX_IP, remoteip);
		map.put(ScpReader.REMOTE_FILEPATH, remotefilepath);
		map.put(ScpReader.USER_NAME, remoteusername);
		map.put(ScpReader.PASSWORD, remotepassword);
		String tmppath = null;
		if (new File("/tmp").exists()) {
			tmppath = "/tmp/data/DataManager/" + System.currentTimeMillis();
		} else {
			tmppath = "./tmp/data/DataManager/" + System.currentTimeMillis();
		}
		map.put(ScpReader.LOCAL_TEMP_PATH, tmppath);
		reader.setupReader(map);
		List<String> localfilepaths = reader.getLocalPaths();
		int successed = 0;
		for (String localfilepath : localfilepaths) {
			System.out.println(localfilepath + "\t" + hdfspath
					+ localfilepath.replace(tmppath, ""));
			try {
				uploadFile2HDFSWithIndexOnSolr(localfilepath, hdfspath
						+ localfilepath.replace(tmppath, ""), overwrite,
						uploadusername, label, solrmasterurl, checkCode,
						listener);
				successed++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "successed/total=" + successed + "/" + localfilepaths.size();
	}

	public static void uploadFile2HBASE(String localfilepath, String filetype,
			String hbasetable, String hbasecolumns, String rowkeyparam,
			DataProgressListener listener) throws Exception {
		HashMap<String, String> configMap = new HashMap<String, String>();
		configMap.put(FileDataSource.CONFIG_FILE_PATH, localfilepath);
		if (filetype.equals("csv")) {
			configMap.put(FileInputDataSource.CONFIG_ANALYSIS_READER,
					FileInputDataSource.LINE_READER);
			configMap.put(FileInputDataSource.CONFIG_ANALYSZER_DRIVER,
					FileInputDataSource.CSV_ANALYZER);
		} else if (filetype.equals("log")) {
			configMap.put(FileInputDataSource.CONFIG_ANALYSIS_READER,
					FileInputDataSource.LINE_READER);
			configMap.put(FileInputDataSource.CONFIG_ANALYSZER_DRIVER,
					FileInputDataSource.LOG_ANALYZER);
		} else {
			throw new Exception("Unsupported filetype = " + filetype
					+ "\t to upload file to hbase.");
		}
		InputDataSource inputdatasource = InputDataSourceFactory
				.createInputDataSource(Constant.NORMAL_FILE_DATASOURCE,
						configMap);

		// HBASE CONFIG
		HashMap<String, String> hbaseconfigMap = new HashMap<String, String>();
		Configuration conf = new Configuration();
		conf.addResource(new FileInputStream(DataTransporter.class
				.getClassLoader().getResource("").getPath()
				+ "hbase-site.xml"));
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZOOKEEPERLIST,
				conf.get("hbase.zookeeper.quorum"));
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZKNODE,
				conf.get("zookeeper.znode.parent"));
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_TABLENAME,
				hbasetable);
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_COLUMNSMAP,
				hbasecolumns);
		hbaseconfigMap.put(
				HBaseOutputDataSource.CONFIG_HBASE_ROWKEYGENEREATEDWAY,
				HBaseOutputDataSource.ROWKEY_GENERATED_BY_NORMAL);
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_ROWKEYPARAMS,
				rowkeyparam);
		OutputDataSource hbase = OutputDataSourceFactory
				.createOutputDataSource(Constant.HBASE_DATASOURCE,
						hbaseconfigMap);
		DataTransporter.createTransJob(inputdatasource, hbase, listener);
	}

	public static String generateDynamicSolrColumns(String sourceCols) {
		String[] splits = sourceCols.split(",");
		StringBuilder sb = new StringBuilder();
		sb.append(splits[0]);
		sb.append("_d");
		for (int i = 1; i < splits.length; i++) {
			sb.append(',');
			sb.append(splits[i]);
			sb.append("_d");
		}
		return sb.toString();
	}

	public static void transRDB2HBASEWithIndexOnSolr(String connectionurl,
			String driver, String username, String password, String tablename,
			String rdbcolumns, String partitioncolumn, int linenum,
			String hbasetable, String solrmasterurl, String label,
			DataProgressListener listener) throws Exception {
		String[] columns = rdbcolumns.split(",");
		StringBuilder sb = new StringBuilder();
		String defaultFamily = "fm";
		for (String column : columns) {
			if (sb.length() <= 0) {
				sb.append(defaultFamily);
				sb.append(':');
				sb.append(column);
			} else {
				sb.append(',');
				sb.append(defaultFamily);
				sb.append(':');
				sb.append(column);
			}
		}
		int i = 0;
		for (; i < columns.length; i++) {
			if (columns[i].equals(partitioncolumn)) {
				break;
			}
		}
		String rowkeyparam = null;
		if (i == columns.length) {
			rowkeyparam = "timestamp,0";
		} else {
			rowkeyparam = "timestamp," + i;
		}
		if (HBaseOperator.getInstance() != null) {
			HBaseOperator.getInstance().createTable(hbasetable,
					new String[] { defaultFamily });
		} else {
			throw new Exception("HBaseOperator instance created failed.");
		}
		transRDB2HBASEWithIndexOnSolr(connectionurl, driver, username,
				password, tablename, rdbcolumns, partitioncolumn, linenum,
				hbasetable, sb.toString(), rowkeyparam, solrmasterurl, label,
				listener);
	}

	public static void main(String[] args) throws Exception {
		System.setProperty("hadoop.home.dir", StaticConfiguration.HADOOP_HOME);
		DataProgressListener listener = new DataProgressListener() {
			@Override
			public void handleEvent(ProgressEvent e) {
				if (e.getSource() instanceof ProgressSource) {
					System.out.println(((ProgressSource) e.getSource())
							.getStat()
							+ "\t"
							+ ((ProgressSource) e.getSource()).getPercent());
				}
			}
		};
		// DataTransporter.uploadFile2HDFSWithIndexOnSolr(
		// "./testinput/designv1.0.3.docx",
		// "/user/zhongliang/solrTest.txt", true, "zhongliang-solr",
		// "solrTest", "http://fdp-master:8983/solr/",
		// FileChecker.SMARTCN_CHECKER, listener);
		// DataTransporter.uploadRomoteFile2HDFSWithIndexOnSolr("/root/t/",
		// "112.33.1.202", "root", "Feixian2015", "/user/public/",
		// true, "zhongliang-solr", "solrTest",
		// "http://114.215.249.43:8983/solr/", FileChecker.SMARTCN_CHECKER,
		// listener);
		// System.setProperty("file.encoding", "utf-8");
		// File f = new File("/root/t/近平和夫人.docx");
		// System.out.println(f.getPath());
		// System.out.println("exists = " + f.exists());
		DataTransporter
				.transRDB2HBASEWithIndexOnSolr(
						"jdbc:mysql://121.40.19.144:3306/lvbb",
						"com.mysql.jdbc.Driver",
						"zhangyue",
						"123456",
						"(select table_region.id as a from table_region join lvbb_vehicle where lvbb_vehicle.id = table_region.Vichle_Id) as table_1",
						"a", "a", 1, "sqoop_hbase_table", "fm:a",
						"timestamp,0,radom16", "http://fdp-master:8983/solr/",
						"testNewTable", listener); 
		// DataTransporter
		// .transRDBIncre2HBASEWithIndexOnSolr(
		// "jdbc:mysql://121.40.19.144:3306/lvbb",
		// "com.mysql.jdbc.Driver",
		// "zhangyue",
		// "123456",
		// "lvbb_balance",
		// "id,serial_number,batch_number,occur_date,biz_type,income,pay",
		// "id",
		// "id",
		// "73",
		// 1,
		// "sqoop_hbase_table",
		// "fm:id,fm:serial_number,fm:batch_number,fm:occur_date,fm:biz_type,fm:income,fm:pay",
		// "timestamp,0,radom16", "http://fdp-master:8983/solr/",
		// "testNewTable", listener);
		// DataTransporter.transRDB2HBASEWithIndexOnSolr(
		// "jdbc:mysql://121.40.19.144:3306/lvbb",
		// "com.mysql.jdbc.Driver", "zhangyue", "123456", "lvbb_balance",
		// "id,serial_number,batch_number,occur_date,biz_type,income,pay",
		// "id", 1, "zhongliang_newhbase", "http://fdp-master:8983/solr/",
		// "testNewTable", listener);
		// DataTransporter
		// .transRDB2HBASEWithIndexOnSolr(
		// "jdbc:mysql://121.40.19.144:3306/flinebigdata",
		// "com.mysql.jdbc.Driver",
		// "zhangyue",
		// "123456",
		// "act_id_info",
		// "ID_,REV_,USER_ID_,TYPE_,KEY_,VALUE_,PASSWORD_,PARENT_ID_",
		// "ID_",
		// -1,
		// "hb_sqoop_hbase_table",
		// "fm:ID_,fm:REV_,fm:USER_ID_,fm:TYPE_,fm:KEY_,fm:VALUE_,fm:PASSWORD_,fm:PARENT_ID_",
		// "timestamp,0,radom16", "http://fdp-master:8983/solr/",
		// "testNewTable", listener);
	}
}
