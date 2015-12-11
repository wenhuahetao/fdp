package com.fline.hadoop.data.client;

import java.util.HashMap;
import junit.framework.TestCase;
import com.fline.hadoop.data.common.Constant;
import com.fline.hadoop.data.common.datasource.InputDataSource;
import com.fline.hadoop.data.common.datasource.InputDataSourceFactory;
import com.fline.hadoop.data.common.datasource.OutputDataSource;
import com.fline.hadoop.data.common.datasource.OutputDataSourceFactory;
import com.fline.hadoop.data.common.datasource.impl.HBaseDataSource;
import com.fline.hadoop.data.common.datasource.impl.HBaseDataSource.HBaseOutputDataSource;
import com.fline.hadoop.data.common.datasource.impl.RDBDataSource;
import com.fline.hadoop.data.common.datasource.impl.RDBDataSource.RDBInputDataSource;
import com.fline.hadoop.data.common.event.ProgressEvent;
import com.fline.hadoop.data.common.event.ProgressSource;
import com.fline.hadoop.data.core.StaticConfiguration;

public class DataTransporterTest extends TestCase {
	public void testcreateTransJobRDB2HBASE() throws Exception {
		System.setProperty("hadoop.home.dir", StaticConfiguration.HADOOP_HOME);
		// RDB CONFIG
		HashMap<String, String> rdbconfigMap = new HashMap<String, String>();
		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_CONNECTIONSTRING,
				"jdbc:mysql://121.40.214.176:3306/Feixian");
		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_DRIVER,
				"com.mysql.jdbc.Driver");
		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_USERNAME, "zzl");
		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_USERPASSWD, "123zzl");
//		 configMap.put(RDBInputDataSource.CONFIG_JDBC_SCHEMA, "id");
		rdbconfigMap.put(RDBInputDataSource.CONFIG_JDBC_TABLE, "sqoop_test");
		// 页面勾选的字段
		rdbconfigMap.put(RDBInputDataSource.CONFIG_JDBC_PARTITIONCOLUMN, "id,content");
		InputDataSource rdb = InputDataSourceFactory.createInputDataSource(
				Constant.RDBS_DATASOURCE, rdbconfigMap);
		// HBASE CONFIG
		HashMap<String, String> hbaseconfigMap = new HashMap<String, String>();
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZOOKEEPERLIST,
				"hdp-master:2181");
		hbaseconfigMap.put(HBaseDataSource.CONFIG_HBASE_ZKNODE,
				"/hbase-unsecure");
		
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_TABLENAME,
				"sqoop_hbase_table");
		hbaseconfigMap.put(HBaseOutputDataSource.CONFIG_HBASE_COLUMNSMAP,
				"fm:id,fm:nm");
		hbaseconfigMap.put(
				HBaseOutputDataSource.CONFIG_HBASE_ROWKEYGENEREATEDWAY,
				HBaseOutputDataSource.ROWKEY_GENERATED_BY_NORMAL);
		
		//设置rowkey
		hbaseconfigMap
				.put(HBaseOutputDataSource.CONFIG_HBASE_ROWKEYPARAMS, "timestamp,0,1,md5");
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
		DataTransporter.createTransJob(rdb, hbase, listener);
	}

//	public void testTransportRDBS2ES() throws Exception {
//		System.setProperty("hadoop.home.dir", StaticConfiguration.HADOOP_HOME);
//		// RDB CONFIG
//		HashMap<String, String> rdbconfigMap = new HashMap<String, String>();
//		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_CONNECTIONSTRING,
//				"jdbc:mysql://121.40.214.176:3306/Feixian");
//		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_DRIVER,
//				"com.mysql.jdbc.Driver");
//		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_USERNAME, "zzl");
//		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_USERPASSWD, "123zzl");
//		// configMap.put(RDBInputDataSource.CONFIG_JDBC_SCHEMA, "");
//		rdbconfigMap.put(RDBInputDataSource.CONFIG_JDBC_TABLE, "sqoop_test");
//		rdbconfigMap.put(RDBInputDataSource.CONFIG_JDBC_PARTITIONCOLUMN, "id");
//		InputDataSource rdb = InputDataSourceFactory.createInputDataSource(
//				Constant.RDBS_DATASOURCE, rdbconfigMap);
//		// ES CONFIG
//		HashMap<String, String> esconfigMap = new HashMap<String, String>();
//		esconfigMap.put(ElasticSearchDataSource.CONFIG_ES_HOSTNAME,
//				"hdp-master");
//		esconfigMap.put(ElasticSearchDataSource.CONFIG_ES_HOSTPORT, "9300");
//		esconfigMap.put(ESOutputDataSource.CONFIG_ES_INDEXNAME, "testindex");
//		esconfigMap.put(ESOutputDataSource.CONFIG_ES_INDEXTYPE, "testtype");
//		esconfigMap.put(ESOutputDataSource.CONFIG_ES_COLUMNNAMES, "ID,NAME");
//		OutputDataSource es = OutputDataSourceFactory.createOutputDataSource(
//				Constant.ES_DATASOURCE, esconfigMap);
//		DataProgressListener listener = new DataProgressListener() {
//			@Override
//			public void handleEvent(ProgressEvent e) {
//				// TODO Auto-generated method stub
//				if (e.getSource() instanceof ProgressSource) {
//					System.out.println(((ProgressSource) e.getSource())
//							.getStat());
//				}
//			}
//		};
//		DataTransporter.createTransJob(rdb, es, listener);
//	}
//
//	public void testcreateTransJobRDB2Solr() throws Exception {
//		System.setProperty("hadoop.home.dir", StaticConfiguration.HADOOP_HOME);
//		// RDB CONFIG
//		HashMap<String, String> rdbconfigMap = new HashMap<String, String>();
//		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_CONNECTIONSTRING,
//				"jdbc:mysql://121.40.214.176:3306/Feixian");
//		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_DRIVER,
//				"com.mysql.jdbc.Driver");
//		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_USERNAME, "zzl");
//		rdbconfigMap.put(RDBDataSource.CONFIG_JDBC_USERPASSWD, "123zzl");
//		// configMap.put(RDBInputDataSource.CONFIG_JDBC_SCHEMA, "");
//		rdbconfigMap.put(RDBInputDataSource.CONFIG_JDBC_TABLE, "sqoop_test");
//		rdbconfigMap.put(RDBInputDataSource.CONFIG_JDBC_PARTITIONCOLUMN, "id");
//		InputDataSource rdb = InputDataSourceFactory.createInputDataSource(
//				Constant.RDBS_DATASOURCE, rdbconfigMap);
//		// solr CONFIG
//		HashMap<String, String> solrconfig = new HashMap<String, String>();
//		solrconfig.put(SolrDataSource.CONFIG_SOLR_MASTERURL,
//				"http://121.40.99.124:8983/solr/");
//		solrconfig.put(SolrOutputDataSource.CONFIG_SOLR_INSTANCE,
//				"core_for_test");
//		solrconfig.put(SolrOutputDataSource.CONFIG_SOLR_COLUMNS, "id,name");
//		OutputDataSource solr = new SolrOutputDataSource(solrconfig,
//				Constant.SOLR_DATASOURCE_NAME);
//		DataProgressListener listener = new DataProgressListener() {
//			@Override
//			public void handleEvent(ProgressEvent e) {
//				// TODO Auto-generated method stub
//				if (e.getSource() instanceof ProgressSource) {
//					System.out.println(((ProgressSource) e.getSource())
//							.getStat());
//				}
//			}
//		};
//		DataTransporter.createTransJob(rdb, solr, listener);
//	}
}
